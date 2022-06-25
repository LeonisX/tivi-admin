package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.calibre.Book;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.calibre.Data;
import md.leonis.tivi.admin.model.danneo.BookCategory;
import md.leonis.tivi.admin.model.template.ChangelogItem;
import md.leonis.tivi.admin.model.template.PlatformItem;
import md.leonis.tivi.admin.model.template.SourceItem;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.Config;
import md.leonis.tivi.admin.utils.SiteRenderer;
import md.leonis.tivi.admin.utils.TemplateUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static md.leonis.tivi.admin.model.template.SourceItem.getDomain;
import static md.leonis.tivi.admin.utils.StringUtils.*;

public class ChangelogRenderer {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final List<CalibreBook> calibreBooks;
    private final List<CalibreBook> oldCalibreBooks;
    private final LocalDateTime fromDate;

    public ChangelogRenderer(List<CalibreBook> calibreBooks, List<CalibreBook> oldCalibreBooks) {
        this.calibreBooks = calibreBooks;
        this.oldCalibreBooks = oldCalibreBooks;
        fromDate = oldCalibreBooks.stream().map(Book::getLastModified).max(LocalDateTime::compareTo)
                .orElseThrow(() -> new RuntimeException("oldCalibreBooks is empty"));
    }

    // сделать страницу с отчётом по всем добавкам - группировать по месту издательства, сортировать по платформам, показывать кто сканил
    public List<String> generateHtmlReport() {

        List<CalibreBook> modifiedBooks = getModifiedBooks();

        long lastFileId = oldCalibreBooks.stream().flatMap(b -> b.getDataList().stream()).mapToLong(Data::getId).max()
                .orElseThrow(() -> new RuntimeException("oldCalibreBooks is empty"));

        Map<Long, CalibreBook> filesMap = getFilesMap(calibreBooks, lastFileId);

        Map<String, Object> root = new HashMap<>();
        root.put("fromDate", fromDate.format(DTF));
        root.put("toDate", LocalDate.now().format(DTF));
        root.put("editedCount", modifiedBooks.size());
        root.put("editedRecordsString", plural("запись", modifiedBooks.size()));

        root.put("totalRecords", calibreBooks.size());
        root.put("totalRecordsString", plural("запись", calibreBooks.size()));

        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new ChangelogItem("Книг игровой тематики", calibreBooks, oldCalibreBooks, BOOK));
        changelog.add(new ChangelogItem("Игровых журналов", calibreBooks, oldCalibreBooks, MAGAZINE));
        changelog.add(new ChangelogItem("Руководств пользователя", calibreBooks, oldCalibreBooks, GUIDE));
        changelog.add(new ChangelogItem("Комиксов", calibreBooks, oldCalibreBooks, COMICS));
        changelog.add(new ChangelogItem("Различных документов", calibreBooks, oldCalibreBooks, DOC));
        changelog.add(new ChangelogItem("Сервисных мануалов", calibreBooks, oldCalibreBooks, MANUAL));
        changelog.add(new ChangelogItem("Описаний эмуляторов", calibreBooks, oldCalibreBooks, EMULATOR));
        root.put("changelog", changelog.stream().sorted(Comparator.comparing(ChangelogItem::getCount).reversed()).collect(Collectors.toList()));

        List<SourceItem> sources = filesMap.values().stream()
                .peek(book -> book.setSource(book.getSource() == null ? "" : book.getSource()))
                .collect(Collectors.groupingBy(book -> getDomain(book.getSource())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .map(SourceItem::new).collect(Collectors.toList());

        root.put("sources", sources);

        Map<String, BookCategory> categoryMap = BookUtils.getCategories().stream().collect(Collectors.toMap(BookCategory::getCatcpu, Function.identity()));

        Map<String, List<CalibreBook>> maps = filesMap.values().stream().collect(Collectors.groupingBy(CalibreBook::getType));

        List<PlatformItem> byPlatform = new ArrayList<>();

        if (maps.get(BOOK) != null) {
            maps.get(BOOK).stream().flatMap(b -> b.getTags().stream().map(t -> categoryMap.get(t.getName())))
                    .collect(Collectors.groupingBy(BookCategory::getParentid)).entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .forEach(e -> e.getValue().stream().collect(Collectors.groupingBy(BookCategory::getCatid)).entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).forEach(en -> {
                        byPlatform.add(new PlatformItem(en.getValue().size(), plural("книга", en.getValue().size()), en.getValue().get(0).getCatname(), SiteRenderer.generateBookCategoryUri(en.getValue().get(0).getCatcpu())));
                    }));
        }

        if (maps.get(MAGAZINE) != null && !maps.get(MAGAZINE).isEmpty()) {
            byPlatform.add(new PlatformItem(maps.get(MAGAZINE).size(), "", plural("журнал", maps.get(MAGAZINE).size()), SiteRenderer.generateBookCategoryUri(MAGAZINE)));
        }
        if (maps.get(COMICS) != null && !maps.get(COMICS).isEmpty()) {
            byPlatform.add(new PlatformItem(maps.get(COMICS).size(), "", plural("комикс", maps.get(COMICS).size()), SiteRenderer.generateBookCategoryUri(COMICS)));
        }
        if (maps.get(MAGAZINE) != null && maps.get(COMICS) != null) {
            int other = filesMap.size() - maps.get(MAGAZINE).size() - maps.get(COMICS).size() - maps.get(BOOK).size();
            if (other > 0) {
                byPlatform.add(new PlatformItem(other, "всего остального", "", ""));
            }
        }
        root.put("byPlatform", byPlatform);

        //TODO разобраться с alt, title, что важнее, если что - брать официальное название
        List<CalibreBook> addedBooks = filesMap.values().stream().sorted(Comparator.comparing(CalibreBook::getTitle)).collect(Collectors.toList());
        root.put("byPictures", addedBooks);

        return TemplateUtils.processTemplateToFile(root, "changelogReport", getReportPath());
    }

    private Map<Long, CalibreBook> getFilesMap(List<CalibreBook> modifiedBooks, long lastFileId) {
        Map<Long, CalibreBook> files = new HashMap<>();
        modifiedBooks
                .forEach(book -> book.getDataList().stream()
                        .filter(file -> file.getId() >= lastFileId)
                        .filter(file -> !"JPG".equals(file.getFormat()))
                        .min((f1, f2) -> f2.getId().compareTo(f1.getId())).ifPresent(file -> files.put(file.getId(), book))
                );
        return files;
    }

    private List<CalibreBook> getModifiedBooks() {
        return calibreBooks.stream().filter(book -> book.getLastModified().isAfter(fromDate)).collect(Collectors.toList());
    }

    public Path getReportPath() {
        return Paths.get(Config.calibreDbPath).resolve("changelog.html");
    }
}
package md.leonis.tivi.admin.view.media;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.media.Book;
import md.leonis.tivi.admin.model.media.CalibreBook;
import md.leonis.tivi.admin.model.media.Data;
import md.leonis.tivi.admin.model.template.ChangelogItem;
import md.leonis.tivi.admin.model.template.PlatformItem;
import md.leonis.tivi.admin.model.template.SourceItem;
import md.leonis.tivi.admin.utils.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static md.leonis.tivi.admin.model.template.SourceItem.getDomain;
import static md.leonis.tivi.admin.utils.StringUtils.plural;

public class CalibreReportsController extends SubPane implements CalibreInterface {

    @FXML
    public GridPane gridPane;

    public Label calibreCountLabel;
    public Label prevCalibreCountLabel;

    public Label fromDate;

    public TextArea textArea;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    String oldestDbDumpPath;
    List<CalibreBook> calibreBooks = new ArrayList<>();
    List<CalibreBook> oldCalibreBooks = new ArrayList<>();

    long lastBookId;
    long lastFileId;

    @FXML
    private void initialize() {
        loadOldestCalibreBooks();
            reloadCalibreBooks();
        System.out.println("initialize()");
    }

    @Override
    public void init() {
        System.out.println("init()");
    }

    public void reloadCalibreBooks() {
        BookUtils.readBooks(this, calibreBooks);
    }

    public void loadOldestCalibreBooks() {
        oldestDbDumpPath = CalibreUtils.getOldestDbDumpPath();
        BookUtils.readBooks(this, oldCalibreBooks, oldestDbDumpPath);
    }

    // сделать страницу с отчётом по всем добавкам - группировать по месту издательства, сортировать по платформам, показывать кто сканил
    public void generateHtmlReport() {

        clearTextArea();

        List<CalibreBook> modifiedBooks = getModifiedBooks();

        Map<Long, CalibreBook> filesMap = getFilesMap(calibreBooks);

        Map<String, Object> root = new HashMap<>();
        root.put("fromDate", fromDate.getText());
        root.put("toDate", LocalDate.now().format(DTF));
        root.put("editedCount", modifiedBooks.size());
        root.put("editedRecordsString", plural("запись", modifiedBooks.size()));

        root.put("totalRecords", calibreBooks.size());
        root.put("totalRecordsString", plural("запись", calibreBooks.size()));

        List<ChangelogItem> changelog = new ArrayList<>();
        changelog.add(new ChangelogItem("Книг игровой тематики", calibreBooks, oldCalibreBooks, "book"));
        changelog.add(new ChangelogItem("Игровых журналов", calibreBooks, oldCalibreBooks, "magazine"));
        changelog.add(new ChangelogItem("Руководств пользователя", calibreBooks, oldCalibreBooks, "guide"));
        changelog.add(new ChangelogItem("Комиксов", calibreBooks, oldCalibreBooks, "comics"));
        changelog.add(new ChangelogItem("Различных документов", calibreBooks, oldCalibreBooks, "doc"));
        changelog.add(new ChangelogItem("Сервисных мануалов", calibreBooks, oldCalibreBooks, "manual"));
        changelog.add(new ChangelogItem("Описаний эмуляторов", calibreBooks, oldCalibreBooks, "emulator"));
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

        if (maps.get("book") != null) {
            maps.get("book").stream().flatMap(b -> b.getTags().stream().map(t -> categoryMap.get(t.getName())))
                    .collect(Collectors.groupingBy(BookCategory::getParentid)).entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .forEach(e -> e.getValue().stream().collect(Collectors.groupingBy(BookCategory::getCatid)).entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).forEach(en -> {
                        byPlatform.add(new PlatformItem(en.getValue().size(), plural("книга", en.getValue().size()), en.getValue().get(0).getCatname(), SiteRenderer.generateBookCategoryUri(en.getValue().get(0).getCatcpu())));
                    }));
        }

        if (maps.get("magazine") != null && !maps.get("magazine").isEmpty()) {
            byPlatform.add(new PlatformItem(maps.get("magazine").size(), "", plural("журнал", maps.get("magazine").size()), SiteRenderer.generateBookCategoryUri("magazines")));
        }
        if (maps.get("comics") != null && !maps.get("comics").isEmpty()) {
            byPlatform.add(new PlatformItem(maps.get("comics").size(), "", plural("комикс", maps.get("comics").size()), SiteRenderer.generateBookCategoryUri("comics")));
        }
        if (maps.get("magazine") != null && maps.get("comics") != null) {
            int other = filesMap.size() - maps.get("magazine").size() - maps.get("comics").size() - maps.get("book").size();
            if (other > 0) {
                byPlatform.add(new PlatformItem(other, "всего остального", "", ""));
            }
        }
        root.put("byPlatform", byPlatform);

        List<CalibreBook> addedBooks = filesMap.values().stream().sorted(Comparator.comparing(CalibreBook::getTitle)).collect(Collectors.toList());
        root.put("byPictures", addedBooks);


        //TODO return, fix
        //TemplateUtils.test(root, textArea);
        TemplateUtils.test2(root);
    }

    private Map<Long, CalibreBook> getFilesMap(List<CalibreBook> modifiedBooks) {
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
        LocalDateTime date = LocalDate.parse(fromDate.getText(), DTF).atStartOfDay();
        return calibreBooks.stream().filter(book -> book.getLastModified().isAfter(date)).collect(Collectors.toList());
    }

    private void addLine() {
        addLine("");
    }

    private void addLine(String text) {
        textArea.appendText(text + System.lineSeparator());
    }

    private void clearTextArea() {
        textArea.clear();
    }

    @SuppressWarnings("all")
    public void updateStatus(boolean status) {
        gridPane.setDisable(!status);
        if (status) {
            lastBookId = oldCalibreBooks.stream().mapToLong(Book::getId).max().getAsLong();
            lastFileId = oldCalibreBooks.stream().flatMap(b -> b.getDataList().stream()).mapToLong(Data::getId).max().getAsLong();
            calibreCountLabel.setText("" + calibreBooks.size());
            prevCalibreCountLabel.setText("" + oldCalibreBooks.size());
            fromDate.setText(CalibreUtils.getDateFromFile(oldestDbDumpPath));
            textArea.clear();
            calibreBooks.forEach(calibreBook -> addLine(calibreBook.getTitle()));
        }
    }
}

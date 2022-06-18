package md.leonis.tivi.admin.view.media;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.media.CalibreBook;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SubPane;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static md.leonis.tivi.admin.utils.BookUtils.calibreBooks;
import static md.leonis.tivi.admin.utils.BookUtils.categories;

public class CalibreReportsController extends SubPane implements CalibreInterface {

    @FXML
    public GridPane gridPane;

    public Label calibreCountLabel;

    public TextField fromDate;

    public TextArea textArea;
    public TextField fromId;

    @FXML
    private void initialize() {
        if (calibreBooks.isEmpty()) {
            reloadCalibreBooks();
        }
        if (categories.isEmpty()) {
            BookUtils.readCategories();
        }
        System.out.println("initialize()");
    }

    @Override
    public void init() {
        System.out.println("init()");
    }

    public void reloadCalibreBooks() {
        BookUtils.readBooks(this);
        clearTextArea();
        calibreBooks.forEach(calibreBook -> addLine(calibreBook.getTitle()));
    }

    public void generateListReport() {
        clearTextArea();
        getFilesMap().entrySet().stream()
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
                .forEach(file -> addLine(file.getKey() + ": " + file.getValue().getTitle() + ": " + file.getValue().getLastModified()));
    }

    // сделать страницу с отчётом по всем добавкам - группировать по месту издательства, сортировать по платформам, показывать кто сканил
    public void generateHtmlReport() {
        clearTextArea();
        addLine();
        addLine("### Источники");
        addLine();
        getFilesMap().values().stream()
                .peek(book -> book.setSource(book.getSource() == null ? "" : book.getSource()))
                .collect(Collectors.groupingBy(book -> getDomain(book.getSource())))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .forEach(e -> {
                    List<String> names = new ArrayList<>();
                    e.getValue().forEach(book -> {
                        names.addAll(book.getAuthors().stream().flatMap(a -> Stream.of(a.getName().split("\\| "))).distinct().collect(Collectors.toList()));
                        names.add(book.getScannedBy());
                        names.add(book.getPostprocessing());
                    });
                    String name = names.stream().filter(Objects::nonNull).filter(n -> !n.equals("Неизвестный")).distinct().collect(Collectors.joining(", "));

                    String link = formatDomain(e.getKey(), e.getValue().get(0).getSource(), name);

                    name = name.isEmpty() || link.contains(name) ? "" : " (" + name + ")";

                    addLine();
                    addLine("**" + link + "**" + name);
                    e.getValue().stream().sorted(Comparator.comparing(CalibreBook::getSort)).forEach(b -> addLine("- " + b.getTitle()));
                });


        Map<String, BookCategory> categoryMap = categories.stream().collect(Collectors.toMap(BookCategory::getCatcpu, Function.identity()));

        addLine();
        addLine("### Статистика по платформам");
        addLine();
        Map<String, List<CalibreBook>> maps = getFilesMap().values().stream().collect(Collectors.groupingBy(CalibreBook::getType));

        maps.get("book").stream().flatMap(b -> b.getTags().stream().map(t -> categoryMap.get(t.getName())))
                .collect(Collectors.groupingBy(BookCategory::getParentid)).entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey))
                .forEach(e -> e.getValue().stream().collect(Collectors.groupingBy(BookCategory::getCatid)).entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).forEach(en -> {
                    addLine(String.format("- +%s %s %s", en.getValue().size(), pluralj("книга", en.getValue().size()), bookCatLink(en.getValue().get(0).getCatname(), en.getValue().get(0).getCatcpu())));
                }));

        if (!maps.get("magazine").isEmpty()) {
            addLine(String.format("- +%s %s", maps.get("magazine").size(), bookCatLink(pluralm("журнал", maps.get("magazine").size()), "magazines")));
        }
        if (!maps.get("comics").isEmpty()) {
            addLine(String.format("- +%s %s", maps.get("comics").size(), bookCatLink(pluralm("комикс", maps.get("comics").size()), "comics")));
        }
        int other = getFilesMap().size() - maps.get("magazine").size() - maps.get("comics").size() - maps.get("book").size();
        if (other > 0) {
            addLine(String.format("- +%s к руководствам, мануалам и другим документам", other));
        }
    }

    private String bookCatLink(String title, String cpu) {
        return String.format("[%s](http://tv-games.ru/media/view/%s.html)", title, cpu);
    }

    //TODO plural function https://www.irlc.msu.ru/irlc_projects/speak-russian/time_new/rus/grammar/

    private String pluralj(String word, int count) {
        return plural(word.substring(0, word.length() - 1), RULE_J, count);
    }

    private String pluralm(String word, int count) {
        return plural(word, RULE_M, count);
    }


    private String plural(String word, String[] rule, int count) {
        if (count == 11) {
            return word + rule[2];
        }
        switch (count % 10) {
            case 1:
                return word + rule[0];
            case 2:
            case 3:
            case 4:
                return word + rule[1];
            default:
                return word + rule[2];
        }
    }

    //                                       1   2-4  6...11,...
    private static final String[] RULE_J = {"a", "и", ""};
    private static final String[] RULE_M = {"", "а", "ов"};

    private String getDomain(String source) {
        if (source == null || source.isEmpty()) {
            return "Другие источники";
        }
        String domain = URI.create(source).getHost();
        if (domain.contains("vk.com")) {
            domain = source;
        }
        return domain;
    }

    private String formatDomain(String domain, String source, String name) {
        if (source == null || source.isEmpty()) {
            return domain;
        }
        if (domain.contains("vk.com")) {
            return String.format("[%s](%s)", name, domain);
        } else {
            String scheme = URI.create(source).getScheme();
            return String.format("[%s](%s://%s)", domain, scheme, domain);
        }
    }

    private Map<Long, CalibreBook> getFilesMap() {
        long id = 0L;
        try {
            id = Long.parseLong(fromId.getText());
        } catch (Exception ignored) {
        }
        long finalId = id;
        LocalDateTime date = LocalDate.parse(fromDate.getText(), DateTimeFormatter.ofPattern("dd.MM.yyyy")).atStartOfDay();

        Map<Long, CalibreBook> files = new HashMap<>();
        calibreBooks.stream()
                .filter(book -> book.getLastModified().isAfter(date))
                .forEach(book -> book.getDataList().stream()
                        .filter(file -> file.getId() >= finalId)
                        .filter(file -> !"JPG".equals(file.getFormat()))
                        .min((f1, f2) -> f2.getId().compareTo(f1.getId())).ifPresent(file -> files.put(file.getId(), book))
                );
        return files;
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

    public void updateStatus(boolean status) {
        gridPane.setDisable(!status);
        calibreCountLabel.setText("" + BookUtils.calibreBooks.size());
        textArea.clear();
        calibreBooks.forEach(calibreBook -> addLine(calibreBook.getTitle()));
    }
}

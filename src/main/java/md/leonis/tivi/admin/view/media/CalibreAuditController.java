package md.leonis.tivi.admin.view.media;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import md.leonis.tivi.admin.model.danneo.BookCategory;
import md.leonis.tivi.admin.model.calibre.Book;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.calibre.CustomColumn;
import md.leonis.tivi.admin.model.calibre.Language;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.CalibreUtils;
import md.leonis.tivi.admin.utils.SiteDbUtils;
import md.leonis.tivi.admin.utils.SubPane;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class CalibreAuditController extends SubPane implements CalibreInterface {

    @FXML
    public GridPane gridPane;

    public Label calibreCountLabel;

    public TextField scannerName;
    public Button scannerLinksButton;
    public Button filesOwnButton;

    public TextArea auditLog;

    List<CalibreBook> calibreBooks = new ArrayList<>();

    private CalibreUtils calibreUtils;

    @FXML
    private void initialize() {
        calibreUtils = new CalibreUtils();
        reloadCalibreBooks();
        System.out.println("initialize()");
    }

    @Override
    public void init() {
        System.out.println("init()");
    }

    private void addLog(String text) {
        auditLog.appendText(text + System.lineSeparator());
    }

    public void checkFilesOwn() {
        auditLog.clear();
        getFilesOwn().forEach(calibreBook -> addLog(calibreBook.getTitle()));

        Map<String, List<CalibreBook>> groupedBooks = calibreBooks.stream().filter(b -> b.getFileName() != null)
                .collect(groupingBy(CalibreBook::getFileName)).entrySet().stream().filter(e -> e.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        if (!groupedBooks.isEmpty()) {
            addLog("");
            addLog("Duplicated fileNames:");
            groupedBooks.keySet().forEach(this::addLog);
        }
    }

    public List<CalibreBook> getFilesOwn() {
        return calibreBooks.stream()
                .filter(calibreBook -> !calibreBook.getDataList().isEmpty()
                        && ((calibreBook.getOwn() == null) || !calibreBook.getOwn())).collect(toList());
    }


    public void checkFileNames() {
        auditLog.clear();
        getFileNames().forEach(f -> addLog(f.getFileName()));
    }

    public List<CalibreBook> getFileNames() {
        return calibreBooks.stream()
                .filter(b -> !b.getDataList().isEmpty())
                /*.peek(b -> {
                    System.out.println(b.getTitle());
                    System.out.println(b.getOwn());
                })*/
                .filter(b -> b.getOwn() != null)
                .filter(CalibreBook::getOwn)
                .filter(b -> b.getFileName() != null)
                .filter(b -> b.getFileName().contains("/") || b.getFileName().contains("\\") || b.getFileName().contains(":") || b.getFileName().contains("\t")
                        || b.getFileName().contains("\n") || b.getFileName().contains("\r") || b.getFileName().contains("\"")).collect(toList());
    }

    public void fixFileNames() {
        getFileNames().forEach(b -> {
            String query = String.format("SELECT * FROM `custom_column_6` WHERE value='%s'", b.getFileName().replace("'", "''"));
            System.out.println(query);
            CustomColumn source = calibreUtils.readObject(query, CustomColumn.class);
            Long id = source.getId();
            System.out.println(id);
            query = String.format("UPDATE `custom_column_6` SET value='%s' WHERE id=%d", b.getFileName().replace("/", "-")
                    .replace("\\", "-").replace(":", " -").replace("\t", " ")
                    .replace("\r", " ").replace("\n", " ").replace("\"", "")
                    .replace("  ", " ").replace("  ", " ").replace("'", "''"), id);
            System.out.println(query);
            id = (long) calibreUtils.executeUpdateQuery(query);
            System.out.println(id);
        });
    }

    public void fixFilesOwn() {
        getFilesOwn().forEach(calibreBook -> {
            if (calibreBook.getOwn() == null) {
                String query = String.format("INSERT INTO `custom_column_9` VALUES (null, %d, 1)", calibreBook.getId());
                System.out.println(query);
                Integer id = calibreUtils.executeInsertQuery(query);
                System.out.println(id);
            } else {
                String query = String.format("UPDATE `custom_column_9` SET value=1 WHERE book=%d", calibreBook.getId());
                System.out.println(query);
                Integer id = calibreUtils.executeUpdateQuery(query);
                System.out.println(id);
            }
        });
    }

    public void checkScannerLinks() {
        auditLog.clear();
        getScannerLinks().forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public List<CalibreBook> getScannerLinks() {
        List<CalibreBook> books = calibreBooks.stream().filter(book -> book.getScannedBy() != null && book.getSource() == null)
                .filter(book -> book.getScannedBy().toLowerCase().contains(scannerName.getText())).collect(toList());
        books.addAll(calibreBooks.stream().filter(book -> book.getPostprocessing() != null && book.getSource() == null)
                .filter(book -> book.getPostprocessing().toLowerCase().contains(scannerName.getText())).collect(toList()));
        return books;
    }

    public void fixScannerLinks() {
        TextInputDialog dialog = new TextInputDialog();
        /*dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter your name:");*/

        Optional<String> response = dialog.showAndWait();

        if (response.isPresent()) {
            //find in custom_column_12
            String query = String.format("SELECT * FROM `custom_column_12` WHERE value='%s'", response.get());
            System.out.println(query);
            CustomColumn source = calibreUtils.readObject(query, CustomColumn.class);
            Long sourceId;
            if (source != null) {
                sourceId = source.getId();
            } else {
                //if no - add
                query = String.format("INSERT INTO `custom_column_12` VALUES (null, '%s')", response.get());
                System.out.println(query);
                Integer id = calibreUtils.executeInsertQuery(query);
                sourceId = id.longValue();
            }
            getScannerLinks().forEach(calibreBook -> {
                //TODO check, probably update
                String q = String.format("INSERT INTO `books_custom_column_12_link` VALUES (null, %d, %d)", calibreBook.getId(), sourceId);
                System.out.println(q);
                Integer id = calibreUtils.executeInsertQuery(q);
                System.out.println(id);
            });
        }
    }

    public void checkLanguages() {
        auditLog.clear();
        getLanguages().forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public List<CalibreBook> getLanguages() {
        return calibreBooks.stream().filter(calibreBook -> calibreBook.getLanguages() != null)
                .filter(calibreBook -> calibreBook.getLanguages().isEmpty()).collect(toList());
    }

    public void fixLanguages() {
        List<String> choices = new ArrayList<>();
        choices.add("rus");
        choices.add("eng");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("rus", choices);
        dialog.setTitle("Choice Dialog");
        dialog.setHeaderText("Look, a Choice Dialog");
        dialog.setContentText("Choose your language:");

        Optional<String> response = dialog.showAndWait();

        if (response.isPresent()) {
            String query = String.format("SELECT * FROM `languages` WHERE lang_code='%s'", response.get());
            System.out.println(query);
            Language language = calibreUtils.readObject(query, Language.class);
            Long langId = language.getId();
            getLanguages().forEach(calibreBook -> {
                String q = String.format("INSERT INTO `books_languages_link` VALUES (null, %d, %d, 0)", calibreBook.getId(), langId);
                System.out.println(q);
                Integer id = calibreUtils.executeInsertQuery(q);
                System.out.println(id);
            });
        }
    }

    public void checkOwnTags() {
        auditLog.clear();
        calibreBooks.stream().filter(book -> book.getOwn() != null && book.getOwn())
                .filter(book -> book.getTags().isEmpty())
                .forEach(book -> addLog(book.getTitle()));
    }

    public void checkTitleFileNames() {
        auditLog.clear();
        getTitleFileNames().forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public List<CalibreBook> getTitleFileNames() {
        return calibreBooks.stream().filter(book -> book.getFileName() == null).collect(toList());
    }

    public void fixTitleFileNames() {
        getTitleFileNames().forEach(book -> {
            String query = String.format("SELECT * FROM `custom_column_6` WHERE value='%s'", book.getTitle().replace("'", "''"));
            System.out.println(query);
            CustomColumn source = calibreUtils.readObject(query, CustomColumn.class);
            Integer id;
            if (source == null) {
                query = String.format("INSERT INTO `custom_column_6` VALUES (null, '%s')", book.getTitle().replace("'", "''"));
                System.out.println(query);
                id = calibreUtils.executeInsertQuery(query);
            } else {
                id = Math.toIntExact(source.getId());
                query = String.format("INSERT INTO `books_custom_column_6_link` VALUES (null, %d, %d)", book.getId(), id);
                System.out.println(query);
                id = calibreUtils.executeInsertQuery(query);
                System.out.println(id);
            }
        });
    }

    public void checkOwnPublishers() {
        auditLog.clear();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getOwn() != null)
                .filter(calibreBook -> !calibreBook.getType().equals("guide"))
                .filter(calibreBook -> calibreBook.getPublisher() == null && calibreBook.getOwn())
                .forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public void checkIsbns() {
        auditLog.clear();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getIsbn() != null)
                .filter(calibreBook -> calibreBook.getIdentifiers().isEmpty())
                .forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public void checkSeriesTitles() {
        auditLog.clear();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getSeries() != null)
                .sorted(Comparator.comparing((CalibreBook b) -> b.getSeries().getSort()).thenComparing(Book::getSort))
                .forEach(calibreBook -> System.out.println(calibreBook.getSeries().getName() + " [" + calibreBook.getSerieIndex() + "] : " + calibreBook.getTitle()));
    }

    //TODO if true - show all books
    // clearTextArea();
    //        calibreBooks.forEach(calibreBook -> addLine(calibreBook.getTitle()));
    public void updateStatus(boolean status) {
        gridPane.setDisable(!status);
        calibreCountLabel.setText("" + calibreBooks.size());
        auditLog.clear();
        calibreBooks.forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public void checkCategories() {
        //BookUtils.addCategory(0, "test");
        /*BookUtils.countVideos();
        addLog(BookUtils.booksCount + "");*/
        BookUtils.siteBooks = SiteDbUtils.listBooks();
        //BookUtils.siteBooks.forEach(b -> addLog(b.mixedTitleProperty().toString()));
        addLog("Books on site: " + BookUtils.siteBooks.size());

        //System.out.println(categories);
        List<String> siteCatNames = BookUtils.getCategories().stream().map(BookCategory::getCatcpu).collect(toList());
        List<String> catNames = calibreBooks.stream().map(this::catName).filter(Objects::nonNull).distinct().collect(toList());
        catNames = catNames.stream().filter(cat -> !siteCatNames.contains(cat)).collect(toList());
        catNames.forEach(this::addLog);

        //TODO add cats
    }

    public void checkTiviIdOwn() {
        auditLog.clear();
        addLog("TiviId / владею");
        calibreBooks.stream()
                .filter(calibreBook -> (calibreBook.getTiviId() == null || calibreBook.getTiviId() < 1)
                        && (calibreBook.getOwn() != null))
                .forEach(calibreBook -> addLog("   - " + calibreBook.getTitle()));
        addLog("TiviId / уникальность");
        calibreBooks.stream()
                .filter(calibreBook -> calibreBook.getTiviId() != null)
                .collect(Collectors.groupingBy(CalibreBook::getTiviId))
                .values().stream().filter(books -> books.size() > 1).flatMap(List::stream)
                .forEach(calibreBook -> addLog("   - " + calibreBook.getTitle() + " (" + calibreBook.getTiviId() + ")"));
    }

    public void checkCpuOwn() {
        auditLog.clear();
        addLog("ЧПУ / владею");
        getCpuOwn().forEach(calibreBook -> addLog("   - " + calibreBook.getTitle()));
        addLog("ЧПУ / валидность");
        getCpuValid().forEach(calibreBook -> addLog("   - " + calibreBook.getTitle() + " (" + calibreBook.getCpu() + ")"));
        addLog("ЧПУ / уникальность");
        calibreBooks.stream()
                .filter(calibreBook -> calibreBook.getCpu() != null)
                .collect(Collectors.groupingBy(CalibreBook::getCpu))
                .values().stream().filter(books -> uniqueCount(books) > 1).flatMap(List::stream)
                .forEach(calibreBook -> addLog("   - " + calibreBook.getTitle() + " (" + calibreBook.getCpu() + ")"));
    }

    private int uniqueCount(List<CalibreBook> books) {
        return books.stream().collect(Collectors.groupingBy(calibreBook -> calibreBook.getTags().toString())).size();
    }

    private List<CalibreBook> getCpuOwn() {
        return calibreBooks.stream()
                .filter(calibreBook -> (calibreBook.getCpu() == null)
                        /*&& (calibreBook.getOwn() != null) && calibreBook.getOwn()*/).collect(toList());
    }

    private List<CalibreBook> getCpuValid() {
        return calibreBooks.stream()
                .filter(calibreBook -> (calibreBook.getCpu() != null) && isInvalidCpu(calibreBook.getCpu())).collect(toList());
    }

    public void fixCpu() {
        getCpuValid().forEach(calibreBook -> {
            String query = String.format("SELECT * FROM `custom_column_16` WHERE value='%s'", calibreBook.getCpu());
            System.out.println(query);
            CustomColumn source = calibreUtils.readObject(query, CustomColumn.class);
            Long cpuId = source.getId();

            query = String.format("DELETE FROM `books_custom_column_16_link` WHERE book=%d AND value=%d", calibreBook.getId(), cpuId);
            System.out.println(query);
            int id = calibreUtils.executeUpdateQuery(query);
            System.out.println(id);

            query = String.format("SELECT * FROM `books_custom_column_16_link` WHERE value=%d", cpuId);
            System.out.println(query);
            List<CustomColumn> fileNames = calibreUtils.readObjectList(query, CustomColumn.class);
            if (fileNames.isEmpty()) {
                query = String.format("DELETE FROM `custom_column_16` WHERE id=%d", cpuId);
                System.out.println(query);
                id = calibreUtils.executeUpdateQuery(query);
                System.out.println(id);
            }
        });


        getCpuOwn().forEach(calibreBook -> {
            String cpu = calibreBook.getFileName() == null ? calibreBook.getTitle() : calibreBook.getFileName();
            cpu = BookUtils.generateCpu(cpu);
            if (isInvalidCpu(cpu)) {
                System.out.println(cpu);
            }
            //find in custom_column_16
            String query = String.format("SELECT * FROM `custom_column_16` WHERE value='%s'", cpu);
            System.out.println(query);
            CustomColumn source = calibreUtils.readObject(query, CustomColumn.class);
            Long sourceId;
            if (source != null) {
                sourceId = source.getId();
            } else {
                //if no - add
                query = String.format("INSERT INTO `custom_column_16` VALUES (null, '%s')", cpu);
                System.out.println(query);
                Integer id = calibreUtils.executeInsertQuery(query);
                sourceId = id.longValue();
            }
            String q = String.format("INSERT INTO `books_custom_column_16_link` VALUES (null, %d, %d)", calibreBook.getId(), sourceId);
            System.out.println(q);
            Integer id = calibreUtils.executeInsertQuery(q);
            System.out.println(id);
        });
    }

    private static boolean isInvalidCpu(String cpu) {
        return !cpu.matches("^[a-z0-9_]+$")/* && cpu.length() < 64*/;
    }

    // solutions, manuals???, docs, programming, ???
    // magazines -> separate category, gd -> gd
    private String catName(CalibreBook calibreBook) {
        if (calibreBook.getTags() == null || calibreBook.getTags().isEmpty()) {
            return null;
        }
        String catName = BookUtils.getCategoryByTags(calibreBook);
        // TODO
        if ("calibreBook".equals(calibreBook.getType())) {
            return catName;
        }
        return catName;
    }

    public void reloadCalibreBooks() {
        BookUtils.readBooks(this, calibreBooks);
        auditLog.clear();
    }

    public void checkDirtyHtml() {
        auditLog.clear();
        for (CalibreBook calibreBook : getDirtyHtml()) {
            addLog(calibreBook.getTitle());
            if (calibreBook.getTextShort() != null && !calibreBook.getTextShort().isEmpty()) {
                outText(calibreBook.getTextShort());
                //outText(CalibreUtils.unify(calibreBook.getTextShort()));
                //outText(CalibreUtils.sanitize(calibreBook.getTextShort()));
            }
            if (calibreBook.getReleaseNote() != null && !calibreBook.getReleaseNote().isEmpty()) {
                outText(calibreBook.getReleaseNote());
                //outText(CalibreUtils.unify(calibreBook.getReleaseNote()));
                //outText(CalibreUtils.sanitize(calibreBook.getReleaseNote()));
            }
            if (calibreBook.getTextMore() != null && !calibreBook.getTextMore().isEmpty()) {
                //outText(calibreBook.getTextMore());
                outText(CalibreUtils.unify(calibreBook.getTextMore()));
                outText(CalibreUtils.sanitize(calibreBook.getTextMore()));
            }
            /*if (calibreBook.getId() > 10) {
                break;
            }*/
        }
    }

    private void outText(String text) {
        int maxLength = 140;
        Pattern p = Pattern.compile("\\G\\s*(.{1," + maxLength + "})(?=\\s|$)", Pattern.DOTALL);
        Arrays.stream(text.substring(0, text.length() > maxLength * 3 ? maxLength * 3 : text.length() - 1).split("\n")).forEach(l -> {
            Matcher m = p.matcher(l);
            while (m.find()) {
                addLog("    " + m.group(1));
            }
        });
    }

    public List<CalibreBook> getDirtyHtml() {
        return calibreBooks.stream()
                //.filter(b -> b.getTags() != null && b.getTags().stream().map(Tag::getName).collect(toList()).contains("3do"))
                .filter(CalibreUtils::hasDirtyHtml).collect(toList());
    }

    public void fixDirtyHtml() {
        //TODO
        /*getDirtyHtml()*/
        calibreBooks.forEach(b -> {
            if (b.getComment() != null && !b.getComment().isEmpty()) {
                String text = CalibreUtils.sanitize(CalibreUtils.getFullText(b.getTextShort(), b.getTextMore()));
                text = CalibreUtils.fixSomeChars(text);
                String q = String.format("UPDATE `comments` SET text='%s' WHERE book=%d", text.replace("'", "''"), b.getId());
                Integer id = calibreUtils.executeInsertQuery(q);
                System.out.println(id);
            }
            if (b.getReleaseNote() != null && !b.getReleaseNote().isEmpty()) {
                //TODO get ID first
                Long rid = calibreUtils.readObjectList("SELECT * FROM books_custom_column_20_link WHERE book=" + b.getId(), CustomColumn.class).get(0).getId();
                String q = String.format("UPDATE `custom_column_20` SET value='%s' WHERE id=%d", CalibreUtils.sanitize(b.getReleaseNote()).replace("'", "''"), rid);
                Integer id = calibreUtils.executeInsertQuery(q);
                System.out.println(id);
            }
        });
    }
}

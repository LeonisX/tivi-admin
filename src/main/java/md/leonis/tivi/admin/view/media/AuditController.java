package md.leonis.tivi.admin.view.media;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.media.CalibreBook;
import md.leonis.tivi.admin.model.media.CustomColumn;
import md.leonis.tivi.admin.model.media.Language;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.CalibreUtils;
import md.leonis.tivi.admin.utils.SubPane;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.BookUtils.calibreBooks;
import static md.leonis.tivi.admin.utils.BookUtils.categories;

public class AuditController extends SubPane {

    @FXML
    public TextField scannerName;
    public TextArea auditLog;
    public Button filesOwnButton;
    public Button scannerLinksButton;
    public GridPane gridPane;
    public Label siteCountLabel;
    public Label calibreCountLabel;

    @FXML
    private void initialize() {
        if (calibreBooks.isEmpty()) {
            reloadCalibreBooks();
        }
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
                .filter(calibreBook -> !calibreBook.getDataList().isEmpty()
                        && (!(calibreBook.getOwn() == null) || calibreBook.getOwn())).filter(b -> b.getFileName() != null)
                .filter(b -> b.getFileName().contains("/") || b.getFileName().contains("\\") || b.getFileName().contains(":") || b.getFileName().contains("\t")
                        || b.getFileName().contains("\n") || b.getFileName().contains("\r") || b.getFileName().contains("\"")).collect(toList());
    }

    public void fixFileNames() {
        getFileNames().forEach(b -> {
            String query = String.format("SELECT * FROM `custom_column_6` WHERE value='%s'", b.getFileName().replace("'", "''"));
            System.out.println(query);
            CustomColumn source = CalibreUtils.readObject(query, CustomColumn.class);
            Long id = source.getId();
            System.out.println(id);
            query = String.format("UPDATE `custom_column_6` SET value='%s' WHERE id=%d", b.getFileName().replace("/", "-")
                    .replace("\\", "-").replace(":", " -").replace("\t", " ")
                    .replace("\r", " ").replace("\n", " ").replace("\"", "")
                    .replace("  ", " ").replace("  ", " ").replace("'", "''"), id);
            System.out.println(query);
            id = (long) CalibreUtils.executeUpdateQuery(query);
            System.out.println(id);
        });
    }

    public void fixFilesOwn() {
        getFilesOwn().forEach(calibreBook -> {
            if (calibreBook.getOwn() == null) {
                String query = String.format("INSERT INTO `custom_column_9` VALUES (null, %d, 1)", calibreBook.getId());
                System.out.println(query);
                Integer id = CalibreUtils.executeInsertQuery(query);
                System.out.println(id);
            } else {
                String query = String.format("UPDATE `custom_column_9` SET value=1 WHERE book=%d", calibreBook.getId());
                System.out.println(query);
                Integer id = CalibreUtils.executeUpdateQuery(query);
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
            CustomColumn source = CalibreUtils.readObject(query, CustomColumn.class);
            Long sourceId;
            if (source != null) {
                sourceId = source.getId();
            } else {
                //if no - add
                query = String.format("INSERT INTO `custom_column_12` VALUES (null, '%s')", response.get());
                System.out.println(query);
                Integer id = CalibreUtils.executeInsertQuery(query);
                sourceId = id.longValue();
            }
            getScannerLinks().forEach(calibreBook -> {
                //TODO check, probably update
                String q = String.format("INSERT INTO `books_custom_column_12_link` VALUES (null, %d, %d)", calibreBook.getId(), sourceId);
                System.out.println(q);
                Integer id = CalibreUtils.executeInsertQuery(q);
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
            Language language = CalibreUtils.readObject(query, Language.class);
            Long langId = language.getId();
            getLanguages().forEach(calibreBook -> {
                String q = String.format("INSERT INTO `books_languages_link` VALUES (null, %d, %d, 0)", calibreBook.getId(), langId);
                System.out.println(q);
                Integer id = CalibreUtils.executeInsertQuery(q);
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
            CustomColumn source = CalibreUtils.readObject(query, CustomColumn.class);
            Integer id;
            if (source == null) {
                query = String.format("INSERT INTO `custom_column_6` VALUES (null, '%s')", book.getTitle().replace("'", "''"));
                System.out.println(query);
                id = CalibreUtils.executeInsertQuery(query);
            } else {
                id = Math.toIntExact(source.getId());

                query = String.format("INSERT INTO `books_custom_column_6_link` VALUES (null, %d, %d)", book.getId(), id);
                System.out.println(query);
                id = CalibreUtils.executeInsertQuery(query);
                System.out.println(id);
            }
        });
    }

    public void checkOwnPublishers() {
        auditLog.clear();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getOwn() != null)
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
                .sorted((b1, b2) -> {
                    int result = b1.getSeries().getSort().compareTo(b2.getSeries().getSort());
                    if (result != 0) {
                        return result;
                    }
                    return b1.getSort().compareTo(b2.getSort());
                })
                .forEach(calibreBook -> System.out.println(calibreBook.getSeries().getName() + " [" + calibreBook.getSerieIndex() + "] : " + calibreBook.getTitle()));
    }

    public void updateStatus(boolean status) {
        gridPane.setDisable(!status);
        calibreCountLabel.setText("" + BookUtils.calibreBooks.size());
        auditLog.clear();
        calibreBooks.forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public void checkCategories() {
        //BookUtils.addCategory(0, "test");
        /*BookUtils.countVideos();
        addLog(BookUtils.booksCount + "");*/
        BookUtils.listBooks();
        //BookUtils.siteBooks.forEach(b -> addLog(b.mixedTitleProperty().toString()));
        addLog("Books on site: " + BookUtils.siteBooks.size());
        BookUtils.readCategories();
        //System.out.println(categories);
        List<String> siteCatNames = categories.stream().map(BookCategory::getCatcpu).collect(toList());
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
                .entrySet().stream().filter(f -> f.getValue().size() > 1).map(Map.Entry::getValue).flatMap(List::stream)
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
                .entrySet().stream().filter(f -> uniqueCount(f.getValue()) > 1).map(Map.Entry::getValue).flatMap(List::stream)
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
                .filter(calibreBook -> (calibreBook.getCpu() != null) && !isValidCpu(calibreBook.getCpu())).collect(toList());
    }

    public void fixCpu() {
        getCpuValid().forEach(calibreBook -> {
            String query = String.format("SELECT * FROM `custom_column_16` WHERE value='%s'", calibreBook.getCpu());
            System.out.println(query);
            CustomColumn source = CalibreUtils.readObject(query, CustomColumn.class);
            Long cpuId = source.getId();

            query = String.format("DELETE FROM `books_custom_column_16_link` WHERE book=%d AND value=%d", calibreBook.getId(), cpuId);
            System.out.println(query);
            Integer id = CalibreUtils.executeUpdateQuery(query);
            System.out.println(id);

            query = String.format("SELECT * FROM `books_custom_column_16_link` WHERE value=%d", cpuId);
            System.out.println(query);
            List<CustomColumn> fileNames = CalibreUtils.readObjectList(query, CustomColumn.class);
            if (fileNames.isEmpty()) {
                query = String.format("DELETE FROM `custom_column_16` WHERE id=%d", cpuId);
                System.out.println(query);
                id = CalibreUtils.executeUpdateQuery(query);
                System.out.println(id);
            }
        });


        getCpuOwn().forEach(calibreBook -> {
            String cpu = calibreBook.getFileName() == null ? calibreBook.getTitle() : calibreBook.getFileName();
            cpu = BookUtils.generateCpu(cpu);
            if (!isValidCpu(cpu)) {
                System.out.println(cpu);
            }
            //find in custom_column_16
            String query = String.format("SELECT * FROM `custom_column_16` WHERE value='%s'", cpu);
            System.out.println(query);
            CustomColumn source = CalibreUtils.readObject(query, CustomColumn.class);
            Long sourceId;
            if (source != null) {
                sourceId = source.getId();
            } else {
                //if no - add
                query = String.format("INSERT INTO `custom_column_16` VALUES (null, '%s')", cpu);
                System.out.println(query);
                Integer id = CalibreUtils.executeInsertQuery(query);
                sourceId = id.longValue();
            }
            String q = String.format("INSERT INTO `books_custom_column_16_link` VALUES (null, %d, %d)", calibreBook.getId(), sourceId);
            System.out.println(q);
            Integer id = CalibreUtils.executeInsertQuery(q);
            System.out.println(id);
        });
    }

    private static boolean isValidCpu(String cpu) {
        return cpu.matches("^[a-z0-9_]+$")/* && cpu.length() < 64*/;
    }

    // solutions, manuals???, docs, programming, ???
    // journals -> separate category, gd -> gd
    private String catName(CalibreBook calibreBook) {
        if (calibreBook.getTags() == null || calibreBook.getTags().isEmpty()) {
            return null;
        }
        String catName = calibreBook.getTags().size() > 1 ? "consoles" : calibreBook.getTags().get(0).getName();
        // TODO
        switch (calibreBook.getType()) {
            case "calibreBook":
                return catName;
            default:
                return catName;
        }

    }

    public void reloadCalibreBooks() {
        BookUtils.readBooks(this);
        auditLog.clear();
        calibreBooks.forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }


    public void checkDirtyHtml() {
        auditLog.clear();
        for (CalibreBook calibreBook: getDirtyHtml()) {
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
        /*getDirtyHtml()*/calibreBooks.forEach(b -> {
            if (b.getTextShort() != null && !b.getTextShort().isEmpty()) {
                String q = String.format("UPDATE `custom_column_18` SET value='%s' WHERE book=%d", CalibreUtils.sanitize(b.getTextShort()).replace("'","''"), b.getId());
                System.out.println(q);
                Integer id = CalibreUtils.executeInsertQuery(q);
                System.out.println(id);
            }
            if (b.getReleaseNote() != null && !b.getReleaseNote().isEmpty()) {
                //TODO get ID first
                Long rid = CalibreUtils.readObjectList("SELECT * FROM books_custom_column_20_link WHERE book=" + b.getId(), CustomColumn.class).get(0).getId();
                String q = String.format("UPDATE `custom_column_20` SET value='%s' WHERE id=%d", CalibreUtils.sanitize(b.getReleaseNote()).replace("'","''"), rid);
                System.out.println(q);
                Integer id = CalibreUtils.executeInsertQuery(q);
                System.out.println(id);
            }
            if (b.getTextMore() != null && !b.getTextMore().isEmpty()) {
                String q = String.format("UPDATE `comments` SET text='%s' WHERE book=%d", CalibreUtils.sanitize(b.getTextMore()).replace("'","''"), b.getId());
                System.out.println(q);
                Integer id = CalibreUtils.executeInsertQuery(q);
                System.out.println(id);
            }
        });
    }





    public void reloadSiteBooks() {
    }

}

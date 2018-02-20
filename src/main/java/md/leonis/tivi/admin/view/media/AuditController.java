package md.leonis.tivi.admin.view.media;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.media.Book;
import md.leonis.tivi.admin.model.media.CalibreBook;
import md.leonis.tivi.admin.model.media.CustomColumn;
import md.leonis.tivi.admin.model.media.Language;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.CalibreUtils;
import md.leonis.tivi.admin.utils.SubPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.BookUtils.calibreBooks;

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
        if (BookUtils.calibreBooks.isEmpty()) {
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
        List<CalibreBook> books = calibreBooks.stream().filter(calibreBook -> calibreBook.getScannedBy() != null && calibreBook.getSource() == null)
                .filter(calibreBook -> !calibreBook.getScannedBy().toLowerCase().contains(scannerName.getText())).collect(toList());
        books.addAll(calibreBooks.stream().filter(calibreBook -> calibreBook.getPostprocessing() != null && calibreBook.getSource() == null)
                .filter(calibreBook -> !calibreBook.getPostprocessing().toLowerCase().contains(scannerName.getText())).collect(toList()));
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
        calibreBooks.stream().filter(calibreBook -> calibreBook.getOwn() != null)
                .filter(calibreBook -> calibreBook.getTags().isEmpty())
                .forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public void checkTitleFileNames() {
        auditLog.clear();
        getTitleFileNames().forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public List<CalibreBook> getTitleFileNames() {
        return calibreBooks.stream().filter(calibreBook -> calibreBook.getFileName() != null)
                .filter(calibreBook -> calibreBook.getFileName().equals(calibreBook.getTitle())).collect(toList());
    }

    public void fixTitleFileNames() {
        getTitleFileNames().forEach(calibreBook -> {
            String query = String.format("SELECT * FROM `custom_column_6` WHERE value='%s'", calibreBook.getFileName());
            System.out.println(query);
            CustomColumn source = CalibreUtils.readObject(query, CustomColumn.class);
            Long fileNameId = source.getId();


            query = String.format("DELETE FROM `books_custom_column_6_link` WHERE book=%d AND value=%d", calibreBook.getId(), fileNameId);
            System.out.println(query);
            Integer id = CalibreUtils.executeUpdateQuery(query);
            System.out.println(id);

            //TODO delete if no links
            query = String.format("SELECT * FROM `books_custom_column_6_link` WHERE value=%d", fileNameId);
            System.out.println(query);
            List<CustomColumn> fileNames = CalibreUtils.readObjectList(query, CustomColumn.class);
            if (fileNames.isEmpty()) {
                query = String.format("DELETE FROM `custom_column_6` WHERE id=%d", fileNameId);
                System.out.println(query);
                id = CalibreUtils.executeUpdateQuery(query);
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

    //TODO tiviid, own
    //TODO tiviid - unique
    //TODO cpu - own
    //TODO cpu - incorrect symbols

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
                .forEach(calibreBook -> System.out.println("   - " + calibreBook.getSeries().getName() + " [" + calibreBook.getSerieIndex() + "] : " + calibreBook.getTitle()));
    }

    public void updateStatus(boolean status) {
        gridPane.setDisable(!status);
        calibreCountLabel.setText("" + BookUtils.calibreBooks.size());
        auditLog.clear();
        calibreBooks.forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public void checkCategories(ActionEvent actionEvent) {
        BookUtils.addCategory(0, "test");
        /*BookUtils.countVideos();
        addLog(BookUtils.booksCount + "");*/
        BookUtils.listBooks();
        //BookUtils.siteBooks.forEach(b -> addLog(b.mixedTitleProperty().toString()));
        addLog(BookUtils.siteBooks.size() + "");
        List<BookCategory> categories = BookUtils.readCategories();
        //System.out.println(categories);
        //TODO computers
        //TODO manuals, solutions, ...
        List<String> siteCatNames = categories.stream().map(BookCategory::getCatcpu).collect(toList());
        List<String> catNames = calibreBooks.stream().map(this::catName).filter(Objects::nonNull).distinct().collect(toList());
        catNames = catNames.stream().filter(cat -> !siteCatNames.contains(cat)).collect(toList());
        catNames.forEach(this::addLog);
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

    public void reloadSiteBooks() {
    }

}

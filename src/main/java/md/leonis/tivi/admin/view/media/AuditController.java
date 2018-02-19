package md.leonis.tivi.admin.view.media;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.media.CalibreBook;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SubPane;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.BookUtils.calibreBooks;

public class AuditController extends SubPane {

    @FXML
    public TextField scannerName;
    public TextArea auditLog;
    public Button filesOwnButton;
    public Button scannerLinksButton;
    public GridPane gridPane;

    @FXML
    private void initialize() {
        BookUtils.readBooks(this);
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
        calibreBooks.stream().filter(calibreBook -> calibreBook.getDataList() != null && calibreBook.getOwn() != null)
                .filter(calibreBook -> !calibreBook.getDataList().isEmpty() && !calibreBook.getOwn())
                .forEach(calibreBook -> addLog(calibreBook.getTitle()));
    }

    public void checkScannerLinks() {
        auditLog.clear();
        String name = scannerName.getText().toLowerCase();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getScannedBy() != null && calibreBook.getSource() == null)
                .filter(calibreBook -> !calibreBook.getScannedBy().toLowerCase().contains(name))
                .forEach(calibreBook -> addLog(calibreBook.getTitle()));
        calibreBooks.stream().filter(calibreBook -> calibreBook.getPostprocessing() != null && calibreBook.getSource() == null)
                .filter(calibreBook -> !calibreBook.getPostprocessing().toLowerCase().contains(name))
                .forEach(calibreBook -> addLog(calibreBook.getTitle()));
        //TODO show fix button
    }

    public void checkLanguages() {
        auditLog.clear();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getLanguages() != null)
                .filter(calibreBook -> calibreBook.getLanguages().isEmpty())
                .forEach(calibreBook -> System.out.println("   - " + calibreBook.getTitle()));
    }

    public void checkOwnTags() {
        auditLog.clear();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getOwn() != null)
                .filter(calibreBook -> calibreBook.getTags() == null)
                .forEach(calibreBook -> System.out.println("   - " + calibreBook.getTitle()));
    }

    public void checkTitleFileNames() {
        auditLog.clear();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getFileName() != null)
                .filter(calibreBook -> calibreBook.getFileName().equals(calibreBook.getTitle()))
                .forEach(calibreBook -> System.out.println("   - " + calibreBook.getTitle()));
        //TODO fix
    }

    public void checkOwnPublishers() {
        auditLog.clear();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getOwn() != null)
                .filter(calibreBook -> calibreBook.getPublisher() == null && calibreBook.getOwn())
                .forEach(calibreBook -> System.out.println("   - " + calibreBook.getTitle()));
    }

    public void checkIsbns() {
        auditLog.clear();
        calibreBooks.stream().filter(calibreBook -> calibreBook.getIsbn() != null)
                .filter(calibreBook -> calibreBook.getIdentifiers().isEmpty())
                .forEach(calibreBook -> System.out.println("   - " + calibreBook.getTitle()));
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
                .forEach(calibreBook -> System.out.println("   - " + calibreBook.getSeries().getName() + " [" + calibreBook.getSerieIndex() + "] : " + calibreBook.getTitle()));
    }

    public void updateStatus(boolean status) {
        gridPane.setDisable(!status);
    }

    public void checkCategories(ActionEvent actionEvent) {
        BookUtils.addCategory(0, "test");
        BookUtils.countVideos();
        addLog(BookUtils.booksCount + "");
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
            case "calibreBook": return catName;
            default:
                return  catName;
        }

    }
}
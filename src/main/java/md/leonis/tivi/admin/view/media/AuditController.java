package md.leonis.tivi.admin.view.media;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.media.Book;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SubPane;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.BookUtils.books;

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
        books.stream().filter(book -> book.getDataList() != null && book.getOwn() != null)
                .filter(book -> !book.getDataList().isEmpty() && !book.getOwn())
                .forEach(book -> addLog(book.getTitle()));
    }

    public void checkScannerLinks() {
        auditLog.clear();
        String name = scannerName.getText().toLowerCase();
        books.stream().filter(book -> book.getScannedBy() != null && book.getSource() == null)
                .filter(book -> !book.getScannedBy().toLowerCase().contains(name))
                .forEach(book -> addLog(book.getTitle()));
        books.stream().filter(book -> book.getPostprocessing() != null && book.getSource() == null)
                .filter(book -> !book.getPostprocessing().toLowerCase().contains(name))
                .forEach(book -> addLog(book.getTitle()));
        //TODO show fix button
    }

    public void checkLanguages() {
        auditLog.clear();
        books.stream().filter(book -> book.getLanguages() != null)
                .filter(book -> book.getLanguages().isEmpty())
                .forEach(book -> System.out.println("   - " + book.getTitle()));
    }

    public void checkOwnTags() {
        auditLog.clear();
        books.stream().filter(book -> book.getOwn() != null)
                .filter(book -> book.getTags() == null)
                .forEach(book -> System.out.println("   - " + book.getTitle()));
    }

    public void checkTitleFileNames() {
        auditLog.clear();
        books.stream().filter(book -> book.getFileName() != null)
                .filter(book -> book.getFileName().equals(book.getTitle()))
                .forEach(book -> System.out.println("   - " + book.getTitle()));
        //TODO fix
    }

    public void checkOwnPublishers() {
        auditLog.clear();
        books.stream().filter(book -> book.getOwn() != null)
                .filter(book -> book.getPublisher() == null && book.getOwn())
                .forEach(book -> System.out.println("   - " + book.getTitle()));
    }

    public void checkIsbns() {
        auditLog.clear();
        books.stream().filter(book -> book.getIsbn() != null)
                .filter(book -> book.getIdentifiers().isEmpty())
                .forEach(book -> System.out.println("   - " + book.getTitle()));
    }

    public void checkSeriesTitles() {
        auditLog.clear();
        books.stream().filter(book -> book.getSeries() != null)
                .sorted((b1, b2) -> {
                    int result = b1.getSeries().getSort().compareTo(b2.getSeries().getSort());
                    if (result != 0) {
                        return result;
                    }
                    return b1.getSort().compareTo(b2.getSort());
                })
                .forEach(book -> System.out.println("   - " + book.getSeries().getName() + " [" + book.getSerieIndex() + "] : " + book.getTitle()));
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
        List<String> catNames = books.stream().map(this::catName).filter(Objects::nonNull).distinct().collect(toList());
        catNames = catNames.stream().filter(cat -> !siteCatNames.contains(cat)).collect(toList());
        catNames.forEach(this::addLog);
    }

    // solutions, manuals???, docs, programming, ???
    // journals -> separate category, gd -> gd
    private String catName(Book book) {
        if (book.getTags() == null || book.getTags().isEmpty()) {
            return null;
        }
        String catName = book.getTags().size() > 1 ? "consoles" : book.getTags().get(0).getName();
        // TODO
        switch (book.getType()) {
            case "book": return catName;
            default:
                return  catName;
        }

    }
}
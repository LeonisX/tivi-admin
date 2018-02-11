package md.leonis.tivi.admin.view.media;

import com.sun.org.apache.xml.internal.serialize.LineSeparator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import md.leonis.tivi.admin.utils.JavaFxUtils;
import md.leonis.tivi.admin.utils.MediaUtils;
import md.leonis.tivi.admin.utils.SubPane;

import static md.leonis.tivi.admin.utils.MediaUtils.books;

public class AuditController extends SubPane {

    @FXML
    public TextField scannerName;
    public TextArea auditLog;
    public Button filesOwnButton;
    public Button scannerLinksButton;
    public GridPane gridPane;

    @FXML
    private void initialize() {
        MediaUtils.readBooks(this);
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

        /*


        System.out.println("3. Languages");
        books.stream().filter(book -> book.getLanguages() != null)
                .filter(book -> book.getLanguages().isEmpty())
                .forEach(book -> System.out.println("   - " + book.getTitle()));

        System.out.println("4. Own/Tags");
        books.stream().filter(book -> book.getOwn() != null)
                .filter(book -> book.getTags() == null)
                .forEach(book -> System.out.println("   - " + book.getTitle()));

        System.out.println("5. Title/FileName");
        books.stream().filter(book -> book.getFileName() != null)
                .filter(book -> book.getFileName().equals(book.getTitle()))
                .forEach(book -> System.out.println("   - " + book.getTitle()));
        //TODO fix

        System.out.println("6. Own/Publisher");
        books.stream().filter(book -> book.getOwn() != null)
                .filter(book -> book.getPublisher() == null && book.getOwn())
                .forEach(book -> System.out.println("   - " + book.getTitle()));

        System.out.println("7. ISBN");
        books.stream().filter(book -> book.getIsbn() != null)
                .filter(book -> book.getIdentifiers().isEmpty())
                .forEach(book -> System.out.println("   - " + book.getTitle()));



        System.out.println("8. Series/Title");
        books.stream().filter(book -> book.getSeries() != null)
                .sorted((b1, b2) -> {
                    int result = b1.getSeries().getSort().compareTo(b2.getSeries().getSort());
                    if (result != 0) {
                        return result;
                    }
                    return b1.getSort().compareTo(b2.getSort());
                })
                .forEach(book -> System.out.println("   - " + book.getSeries().getName() + " [" + book.getSerieIndex() + "] : " + book.getTitle()));
*/
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

    public void updateStatus(boolean status) {
        gridPane.setDisable(!status);
    }
}
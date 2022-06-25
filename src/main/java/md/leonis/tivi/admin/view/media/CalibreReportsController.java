package md.leonis.tivi.admin.view.media;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.renderer.ChangelogRenderer;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.CalibreUtils;
import md.leonis.tivi.admin.utils.SubPane;
import md.leonis.tivi.admin.utils.WebUtils;

import java.util.ArrayList;
import java.util.List;

public class CalibreReportsController extends SubPane implements CalibreInterface {

    @FXML
    public GridPane gridPane;

    public Label calibreCountLabel;
    public Label prevCalibreCountLabel;

    public Label fromDate;

    public TextArea textArea;

    String oldestDbDumpPath;
    List<CalibreBook> calibreBooks = new ArrayList<>();
    List<CalibreBook> oldCalibreBooks = new ArrayList<>();

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

    public void generateHtmlReport() {
        ChangelogRenderer renderer = new ChangelogRenderer(calibreBooks, oldCalibreBooks);
        clearTextArea();
        renderer.generateHtmlReport().forEach(this::addLine);
        WebUtils.openWebPage(renderer.getReportPath());
    }

    private void addLine(String text) {
        textArea.appendText(text + System.lineSeparator());
    }

    private void clearTextArea() {
        textArea.clear();
    }

    public void updateStatus(boolean status) {
        gridPane.setDisable(!status);
        if (status) {
            calibreCountLabel.setText("" + calibreBooks.size());
            prevCalibreCountLabel.setText("" + oldCalibreBooks.size());
            fromDate.setText(CalibreUtils.getDateFromFile(oldestDbDumpPath));
            textArea.clear();
            calibreBooks.forEach(calibreBook -> addLine(calibreBook.getTitle()));
        }
    }
}

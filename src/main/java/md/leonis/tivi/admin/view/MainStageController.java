package md.leonis.tivi.admin.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Hyperlink;
import md.leonis.tivi.admin.model.Actions;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.CalibreUtils;
import md.leonis.tivi.admin.utils.VideoUtils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class MainStageController {

    @FXML
    public Hyperlink auditHyperlink;
    public Hyperlink calibreCompareHyperlink;
    public Hyperlink siteCompareHyperlink;
    public Hyperlink calibreDumpHyperlink;
    public Hyperlink siteDumpHyperlink;

    @FXML
    private Accordion accordion;
    @FXML
    private Hyperlink settingsHyperlink;
    @FXML
    private Hyperlink allVideoHyperlink;
    @FXML
    private Hyperlink addVideoHyperlink;
    @FXML
    private Hyperlink categoriesHyperlink;
    @FXML
    private Hyperlink addCategoryHyperlink;
    @FXML
    private Hyperlink inaccesibleHyperlink;
    @FXML
    private Hyperlink tagsHyperlink;
    @FXML
    private Hyperlink commentsHyperlink;

    @FXML
    private void initialize() {
        accordion.setExpandedPane(accordion.getPanes().get(2));
    }

    @FXML
    private void addVideo() {
        VideoUtils.action = Actions.ADD;
        VideoUtils.showAddVideo();
    }

    @FXML
    private void listVideos() {
        VideoUtils.showListVideous();
    }

    public void auditBooks() {
        BookUtils.auditBooks();
    }

    public void dumpCalibreDB() {
        CalibreUtils.dumpDB();
    }

    public void compareCalibreDbs() {
        BookUtils.compareCalibreDbs();
    }

    public void dumpSiteDB() throws FileNotFoundException {
        BookUtils.dumpDB();
    }

    public void compareWithSite() {
        BookUtils.compareWithSite();
    }
}
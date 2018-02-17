package md.leonis.tivi.admin.view;

import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Hyperlink;
import md.leonis.tivi.admin.model.Actions;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.VideoUtils;

public class MainStageController {

    @FXML
    public Hyperlink auditHyperlink;

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
}
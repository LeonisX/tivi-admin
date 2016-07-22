package md.leonis.tivi.admin.view.video;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import md.leonis.tivi.admin.utils.SubPane;
import md.leonis.tivi.admin.utils.VideoUtils;

public class ListVideosController extends SubPane {
    @FXML
    private Button nextButton;

    @FXML
    private TextField urlTextField;

    @FXML
    private void processVideo() {
        VideoUtils.video.setUrl(urlTextField.getText().trim());
        VideoUtils.parseVideoPage();
        VideoUtils.showAddVideo2();
    }

}
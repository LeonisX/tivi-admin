package md.leonis.tivi.admin.view.video;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.utils.SubPane;
import md.leonis.tivi.admin.utils.VideoUtils;

public class AddVideoController extends SubPane {

    @FXML
    private TextField urlTextField;

    @FXML
    private void processVideo() {
        if (VideoUtils.action == VideoUtils.Actions.ADD) {
            VideoUtils.video = new Video();
        }
        VideoUtils.video.setUrl(urlTextField.getText().trim());
        if (VideoUtils.action == VideoUtils.Actions.ADD) {
            VideoUtils.parseVideoPage();
        }
        if (VideoUtils.action == VideoUtils.Actions.CLONE) {
            VideoUtils.parseUrl(VideoUtils.video);
            VideoUtils.video.setCpu(VideoUtils.video.getCpu() + "_clone");
            VideoUtils.video.setImage("");
        }
        VideoUtils.showAddVideo2();
    }

}
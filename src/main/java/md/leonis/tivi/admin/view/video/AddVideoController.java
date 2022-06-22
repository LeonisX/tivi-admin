package md.leonis.tivi.admin.view.video;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import md.leonis.tivi.admin.model.Actions;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.SubPane;
import md.leonis.tivi.admin.utils.VideoUtils;

public class AddVideoController extends SubPane {

    @FXML
    private TextField urlTextField;

    @FXML
    private void initialize() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String text = clipboard.getString();
        if (text != null && text.startsWith("http")) {
            urlTextField.setText(removeTrail(text));
            clipboard.clear();
        }
    }

    @FXML
    private void processVideo() {
        if (VideoUtils.action == Actions.ADD) {
            VideoUtils.video = new Video();
        }
        VideoUtils.video.setUrl(urlTextField.getText().trim());
        if (VideoUtils.action == Actions.ADD) {
            VideoUtils.parseVideoPage();
        }
        if (VideoUtils.action == Actions.CLONE) {
            VideoUtils.parseUrl(VideoUtils.video);
            VideoUtils.video.setCpu(VideoUtils.video.getCpu() + "_clone");
            VideoUtils.video.setImage("");
        }
        VideoUtils.showAddVideo2();
    }

    private String removeTrail(String url) {
        int index = url.indexOf("&");
        String result = url;
        if (index != -1) result = url.substring(0, index);
        return result;
    }
}

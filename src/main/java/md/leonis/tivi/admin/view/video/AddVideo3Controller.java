package md.leonis.tivi.admin.view.video;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import md.leonis.tivi.admin.utils.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class AddVideo3Controller extends SubPane {

    @FXML
    private FlowPane imageViews;

    @FXML
    private ImageView imageView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    /*@FXML
    private Button finishButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button backButton;*/

    @FXML
    private Label sizeLabel;

    @FXML
    private void delete() {
        VideoUtils.video.setPreviousImage(VideoUtils.video.getImage());
        VideoUtils.video.setImage("");
        showImage();
    }

    @FXML
    private void cancel() {
        VideoUtils.showListVideous();
    }

    @FXML
    private void back() {
        VideoUtils.showAddVideo2();
    }

    private boolean checkAllValues() {
        CheckUtils checker = new CheckUtils();
        checker.checkCpuExist(VideoUtils.video.getCpu());
        if (!checker.isOk()) JavaFxUtils.showAlert("Ошибка",
                "Следующие данные следует поправить:",
                checker.getErrors(),
                Alert.AlertType.ERROR);
        return checker.isOk();
    }

    @FXML
    private void finish() throws IOException {
        if (checkAllValues()) {
            //TODO оптимизировать
            InputStream is = null;
            if (imageView.isVisible()) {
                is = toInputStream(imageView.getImage());
                //TODO previousImage
                VideoUtils.video.setImage("images/video/thumbs/" + VideoUtils.video.getCpu() + ".png");
            }
            String json = JsonUtils.gson.toJson(VideoUtils.video);
            System.out.println(json);
            try {
                String res = VideoUtils.addVideo(json, VideoUtils.video.getImage(), is, VideoUtils.video.getPreviousImage());
                System.out.println("OK Add Video");
                System.out.println(res);
            } catch (IOException e) {
                System.out.println("Error Add Video");
                System.out.println(e.getMessage());
                //TODO window with error
            }
            VideoUtils.showListVideous();
        }

    }

    @Override
    public void init() {
        System.out.println("AddVideo3Controller.init()");
        // show current image and buttons
        showImage();

        // load all images, add to images
        imageViews.getChildren().clear();

        if (!VideoUtils.video.getYid().isEmpty()) {
            loadImage(imageViews, "default");
            loadImage(imageViews, "1");
            loadImage(imageViews, "2");
            loadImage(imageViews, "3");
            //loadImage(imageViews, "0");
            //loadImage(imageViews, "mqdefault");
            //loadImage(imageViews, "hqdefault");
            //loadImage(imageViews, "maxresdefault");
        }
    }

    private long getImageSize(Image image) {
        byte[] buffer = new byte[4096];
        long size = 0;
        int bytesRead;
        try {
            InputStream inputStream = toInputStream(image);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                size += bytesRead;
            }
            inputStream.close();
        } catch (Throwable e) { e.printStackTrace(); }
        return size;
    }

    private static InputStream toInputStream(Image image) throws IOException {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    private void loadImage(Pane parent, String name) {
        String imageSource = formatUrl(VideoUtils.video.getYid(), name);
        Image image = new Image(imageSource);
        System.out.println(image.isError());
        ImageView imageView = new ImageView(image);
        imageView.setOnMouseClicked((MouseEvent e) -> {
                //System.out.println(name);
            // set video image
            if (VideoUtils.video.getPreviousImage().isEmpty()) VideoUtils.video.setPreviousImage(VideoUtils.video.getImage());
            VideoUtils.video.setImage(imageSource);
            //TODO in future - if image changed - [delete at site], upload
            // show video image
            showImage();
            });
        Label title = new Label(name);
        Label label = new Label("(" + Math.ceil(image.getWidth()) + " x " + Math.ceil(image.getHeight()) + ")");
        imageView.setFitWidth(120.0);
        imageView.setPreserveRatio(true);
        VBox vbox = new VBox(2.0, imageView, title, label);
        vbox.setAlignment(Pos.BOTTOM_CENTER);
        parent.getChildren().add(vbox);
    }

    private void showImage() {
        System.out.println(VideoUtils.video.getImage());
        boolean visible = !VideoUtils.video.getImage().isEmpty();
        if (visible) {
            String path = VideoUtils.video.getImage();
            if (!path.startsWith("http")) path = Config.sitePath + path;
            Image image = new Image(path);
            System.out.println(image.isError());
            imageView.setImage(image);
            sizeLabel.setText("(" + getImageSize(image) + " байт)");
        }
        imageView.setVisible(visible);
        deleteButton.setVisible(visible);
        editButton.setVisible(visible);
        sizeLabel.setVisible(visible);
    }

    private String formatUrl(String yid, String name) {
        return "https://i1.ytimg.com/vi/" + yid + "/" + name + ".jpg";
    }

}
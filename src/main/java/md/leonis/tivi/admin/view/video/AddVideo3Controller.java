package md.leonis.tivi.admin.view.video;

import helloworld.MainApp;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// remember to delete image on server

public class AddVideo3Controller {
    @FXML
    private FlowPane imageViews;

    @FXML
    private ImageView imageView;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Button finishButton;

    @FXML
    private Button cancelButton;

    private MainApp mainApp;

    @FXML
    private void delete() {
        mainApp.addVideo.setPreviousImage(mainApp.addVideo.getImage());
        mainApp.addVideo.setImage("");
        showImage();
    }

    //TODO check if cpu exists
    //TODO http://codereview.stackexchange.com/questions/112331/inserting-json-array-data-into-a-mysql-database-using-php
    //TODO upload after send video - when upload - verify video id (or cpu) and received parameter
    //TODO http://php.net/manual/ru/function.mysql-insert-id.php

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        // show current image and buttons
        showImage();

        // load all images, add to images
        imageViews.getChildren().clear();
        if (!mainApp.addVideo.getYid().isEmpty()) {
            loadImage(imageViews, "default");
            loadImage(imageViews, "1");
            loadImage(imageViews, "2");
            loadImage(imageViews, "3");
            loadImage(imageViews, "0");
            loadImage(imageViews, "mqdefault");
            loadImage(imageViews, "hqdefault");
            loadImage(imageViews, "maxresdefault");
        }
    }

    public static void saveToFile(Image image) {
        File outputFile = new File("/home/leonis/test.png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        //bImage.
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadImage(Pane parent, String name) {
        String imageSource = formatUrl(mainApp.addVideo.getYid(), name);
        Image image = new Image(imageSource);
        System.out.println(image.isError());
        ImageView imageView = new ImageView(image);
        imageView.setOnMouseClicked((MouseEvent e) -> {
                //System.out.println(name);
            // set video image
            if (mainApp.addVideo.getPreviousImage().isEmpty()) mainApp.addVideo.setPreviousImage(mainApp.addVideo.getImage());
            mainApp.addVideo.setImage(imageSource);
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

    public void showImage() {
        System.out.println(mainApp.addVideo.getImage());
        Boolean visible = !mainApp.addVideo.getImage().isEmpty();
        if (visible) {
            Image image = new Image(mainApp.addVideo.getImage());
            System.out.println(image.isError());
            imageView.setImage(image);
        }
        imageView.setVisible(visible);
        deleteButton.setVisible(visible);
        editButton.setVisible(visible);
    }

    public String formatUrl(String yid, String name) {
        return "https://i1.ytimg.com/vi/" + yid + "/" + name + ".jpg";
    }

}
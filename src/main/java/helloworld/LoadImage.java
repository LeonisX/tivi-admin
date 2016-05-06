package helloworld;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.GroupBuilder;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.stage.Stage;

public class LoadImage extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();

        String imageSource = "https://i1.ytimg.com/vi/Tuy1UQjZYCQ/0.jpg";

        Image image = new Image(imageSource);
        System.out.println(image.isError());
        ImageView imageView = new ImageView(image);



        root.getChildren().add(imageView);

        primaryStage.setScene(new Scene(root, 500, 400));
        primaryStage.show();
    }
}
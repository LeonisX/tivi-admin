package helloworld;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.fxml.*;
import md.leonis.tivi.admin.view.MainStageController;

import java.io.IOException;

public class HelloWorldFXML extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("TiVi Admin Panel");

        initRootLayout();

        //showAddVideo();
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HelloWorldFXML.class.getResource("/md/leonis/tivi/admin/view/MainStage.fxml"));
            rootLayout = loader.load();

            // Give the controller access to the main app.
            MainStageController controller = loader.getController();
            controller.setMainApp(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddVideo() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HelloWorldFXML.class.getResource("/md/leonis/tivi/admin/view/video/AddVideo.fxml"));
            AnchorPane addVideo = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(addVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
package helloworld;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.fxml.*;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.view.MainStageController;
import md.leonis.tivi.admin.view.video.AddVideo2Controller;
import md.leonis.tivi.admin.view.video.AddVideoController;

import java.io.IOException;

public class MainApp extends Application {

    public Video addVideo = new Video();

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
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/md/leonis/tivi/admin/view/MainStage.fxml"));
            rootLayout = loader.load();

            // Give the controller access to the main app.
            MainStageController controller = loader.getController();
            controller.setMainApp(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAddVideo() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/md/leonis/tivi/admin/view/video/AddVideo.fxml"));
            Parent addVideo = loader.load();
            AddVideoController controller = loader.getController();
            controller.setMainApp(this);
            rootLayout.setCenter(addVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showProcessVideo() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/md/leonis/tivi/admin/view/video/AddVideo2.fxml"));
            Parent addVideo2 = loader.load();
            //TODO
            AddVideo2Controller controller = loader.getController();
            controller.setMainApp(this);
            rootLayout.setCenter(addVideo2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
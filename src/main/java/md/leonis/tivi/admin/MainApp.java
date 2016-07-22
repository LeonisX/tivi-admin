package md.leonis.tivi.admin;

import javafx.application.Application;
import javafx.stage.Stage;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.utils.Config;
import md.leonis.tivi.admin.utils.JavaFxUtils;
import md.leonis.tivi.admin.utils.VideoUtils;

import java.util.List;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Config.loadProperties();
        List<Video> videos = VideoUtils.listVideos(5, 0, 1, "downid", "desc");
        videos.forEach(System.out::println);
        JavaFxUtils.showMainPane(primaryStage);
    }
}
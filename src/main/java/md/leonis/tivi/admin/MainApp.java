package md.leonis.tivi.admin;

import javafx.application.Application;
import javafx.stage.Stage;
import md.leonis.tivi.admin.utils.Config;
import md.leonis.tivi.admin.utils.JavaFxUtils;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        try {
            Config.loadProperties();
            Config.loadProtectedProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Config.loadProperties();
        Config.loadProtectedProperties();
        JavaFxUtils.showMainPane(primaryStage);
    }
}
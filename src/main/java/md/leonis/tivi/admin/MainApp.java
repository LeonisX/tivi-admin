package md.leonis.tivi.admin;

import javafx.application.Application;
import javafx.stage.Stage;
import md.leonis.tivi.admin.utils.Config;
import md.leonis.tivi.admin.utils.JavaFxUtils;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Config.loadProperties();
        JavaFxUtils.showMainPane(primaryStage);
    }
}
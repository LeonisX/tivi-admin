package md.leonis.tivi.admin;

import javafx.application.Application;
import javafx.stage.Stage;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.Config;
import md.leonis.tivi.admin.utils.JavaFxUtils;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        try {
            Config.loadProperties();
            Config.loadProtectedProperties();
            BookUtils.queryRequest("SELECTъ пропро COUNT(downid) AS count FROM danny_media");
            //BookUtils.queryRequest("SELECT * FROM danny_media");
            //BookUtils.queryOperation(String.format("INSERT INTO danny_info VALUES(NULL, \"%s\", \"%s\", \"%s\")", "протест", BookUtils.prepareQuery("текст \" ' &^%$#@!*(<>{} fff"), "as"));
            //BookUtils.queryOperation("DELETE FROM danny_info WHERE infoid = 213");

        } catch (IOException e) {
            e.printStackTrace();
        }
        //launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Config.loadProperties();
        Config.loadProtectedProperties();
        JavaFxUtils.showMainPane(primaryStage);
    }
}
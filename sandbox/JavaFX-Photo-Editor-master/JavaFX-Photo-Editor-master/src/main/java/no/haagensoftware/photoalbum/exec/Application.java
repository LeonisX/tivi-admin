package no.haagensoftware.photoalbum.exec;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import no.haagensoftware.photoalbum.controllers.MainController;
import no.haagensoftware.photoalbum.util.FileUtil;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.io.IOException;
import java.net.URL;


public class Application extends javafx.application.Application {
    private static final Logger logger = Logger.getLogger(Application.class.getName());


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        MainController mainController;

        configureLog4J();

        URL resource = FileUtil.getUrlForResource("MainController.fxml");

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            BorderPane page = fxmlLoader.load();

            mainController = fxmlLoader.getController();
            mainController.setPrimaryStage(stage);

            //Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(page, 800, 600);

            stage.setScene(scene);

            stage.setTitle("JavaBin Photo Album");
            stage.show();

            mainController.setupUi();
            logger.info("Showed Primary Stage");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureLog4J() throws IOException {
        Logger root = Logger.getRootLogger();
        if (!root.getAllAppenders().hasMoreElements()) {
            //Log4J is not configured. Set it up correctly!
            root.setLevel(Level.INFO);
            root.addAppender(new ConsoleAppender(new PatternLayout("%-5p [%t]: %m%n")));
            //root.addAppender(new FileAppender(new PatternLayout("%-5p [%t]: %m%n"), workspace.getWorkspaceDir() + File.separatorChar + "maps.log", true));
        }
    }
}

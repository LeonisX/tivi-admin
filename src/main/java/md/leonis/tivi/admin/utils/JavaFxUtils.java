package md.leonis.tivi.admin.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaFxUtils {

    private static BorderPane rootLayout;

    public static void showMainPane(Stage primaryStage) {

        primaryStage.setTitle("TiVi Admin Panel");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(JavaFxUtils.class.getResource("/fxml/MainStage.fxml"));
            rootLayout = loader.load();
            //MainStageController controller = loader.getController();
            Scene scene = new Scene(rootLayout, 1280, 960);
            primaryStage.setScene(scene);

            showVoidPanel();

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showVoidPanel() {
        showPane("void.fxml");
    }

    static void showPane(String resource) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(JavaFxUtils.class.getResource("/fxml/" + resource));
            Parent parent = loader.load();
            Object controller = loader.getController();
            if (controller instanceof SubPane) ((SubPane) controller).init();
            rootLayout.setCenter(parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(String title, String header, String text, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);

        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMinWidth(720);
        textArea.setMinHeight(600);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);

        alert.getDialogPane().setContent(textArea);

        alert.showAndWait();
    }
}

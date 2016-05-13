package helloworld;

import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.fxml.*;
import md.leonis.tivi.admin.model.*;
import md.leonis.tivi.admin.utils.JsonUtils;
import md.leonis.tivi.admin.view.MainStageController;
import md.leonis.tivi.admin.view.video.AddVideo2Controller;
import md.leonis.tivi.admin.view.video.AddVideoController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    public List<Category> categories = new ArrayList<>();

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
            Scene scene = new Scene(rootLayout, 1024, 768);
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
            categories = readVideoCategories();
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

    private String readFromUrl(String urlAddress) throws IOException {
        StringBuilder  stringBuilder = new StringBuilder();
        URL url = new URL(urlAddress);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("AuthToken", "_da token");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        try {
            while((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            return stringBuilder.toString();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                System.out.println("Can't close reader...");
            }
        }
    }

    private List<Category> readVideoCategories() {
        List<Category> videoCategories = new ArrayList<>();
        try {
            String jsonString = readFromUrl("http://wap.tv-games.ru/video.php?to=cat");
            videoCategories = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<Category>>(){}.getType());
        } catch (IOException e) {
            System.out.println("Error in readVideoCategories");
        }
        return videoCategories;
    }
}
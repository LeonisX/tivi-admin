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
import md.leonis.tivi.admin.utils.VideoUtils;
import md.leonis.tivi.admin.view.MainStageController;
import md.leonis.tivi.admin.view.video.AddVideo2Controller;
import md.leonis.tivi.admin.view.video.AddVideo3Controller;
import md.leonis.tivi.admin.view.video.AddVideoController;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
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

            showVoid();

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseVideoPage(String url) {
        VideoUtils.parseUrl(url, addVideo);
        VideoUtils.parsePage(addVideo);
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
            AddVideo2Controller controller = loader.getController();
            controller.setMainApp(this);
            rootLayout.setCenter(addVideo2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showProcessImage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/md/leonis/tivi/admin/view/video/AddVideo3.fxml"));
            Parent addVideo3 = loader.load();
            AddVideo3Controller controller = loader.getController();
            controller.setMainApp(this);
            rootLayout.setCenter(addVideo3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showVoid() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/md/leonis/tivi/admin/view/void.fxml"));
            Parent voidz = loader.load();
            rootLayout.setCenter(voidz);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromUrl(String urlAddress) throws IOException {
        StringBuilder  stringBuilder = new StringBuilder();
        URL url = new URL(urlAddress);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("AuthToken", "_da token");
        conn.setRequestProperty("User-Agent", "TiVi's admin client");

        int responseCode = conn.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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

    private void postData(String urlAddress, String fileName) throws IOException {
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.
        String boundary = Long.toHexString(System.currentTimeMillis());
        File fileToUpload = new File(fileName);
        URL url = new URL(urlAddress);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("AuthToken", "_da token");
        conn.setRequestProperty("User-Agent", "TiVi's admin client");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        PrintWriter writer = null;
        try {
            OutputStream output = conn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(conn.getOutputStream()));

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=UTF-8").append(CRLF);
            writer.append(CRLF).append("value").append(CRLF).flush();

            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + fileToUpload.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileToUpload.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(fileToUpload.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

        } finally {
            if (writer != null) { writer.flush(); writer.close(); }
        }
        // Request is lazily fired whenever you need to obtain information about response.
        int responseCode = conn.getResponseCode();
        System.out.println(responseCode); // Should be 200
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
package helloworld;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.fxml.*;
import md.leonis.tivi.admin.utils.Translit;
import md.leonis.tivi.admin.view.MainStageController;
import md.leonis.tivi.admin.view.video.AddVideoController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloWorldFXML extends Application {

    private String url;
    private String yid = null;
    private WebEngine webEngine;

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
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HelloWorldFXML.class.getResource("/md/leonis/tivi/admin/view/video/AddVideo.fxml"));
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
            //TODO
            loader.setLocation(HelloWorldFXML.class.getResource("/md/leonis/tivi/admin/view/video/AllVideo.fxml"));
            Parent allVideo = loader.load();
            rootLayout.setCenter(allVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseUrl(String url) {
        this.url = url;
        this.url = "https://www.youtube.com/watch?v=TUAy0WKsqOs";
        yid = getYoutubeVideoId(this.url);
        System.out.println(yid);
    }

    public void parsePage() {
        System.out.println("-------------------------");
        //load, parse
        try {
            Document doc = Jsoup.connect(url).get();
            String title = doc.getElementById("eow-title").text();
            String description = doc.getElementById("eow-description").text();
            Element div = doc.getElementById("watch7-user-header");
            String author = div.select("a.yt-uix-sessionlink").get(1).text();
            URL aURL = new URL(url);
            String authorUrl = aURL.getProtocol() +"://" + aURL.getAuthority() + div.select("a.yt-uix-sessionlink").get(1).attr("href");
            // TODO проверять, есть ли такой тайтл в базе
            // TODO Посмотреть как хранится в базе, обрезать длинные названия
            String cpu = Translit.toTranslit(title).toLowerCase().replace(' ', '_').replaceAll("[^\\w\\s]","").replace("__", "_");
            // TODO keywords
            // TODO description
            System.out.println(yid);
            System.out.println(title);
            System.out.println(cpu);
            System.out.println(author);
            System.out.println(authorUrl);
            System.out.println(description);

        } catch (IOException e) {
            System.out.println(e);
        }
        //get images
        //http://i3.ytimg.com/vi/Tuy1UQjZYCQ/0.jpg
        //1-3: 120x90
    }


    public static String getYoutubeVideoId(String youtubeUrl)
    {
        String video_id="";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http"))
        {

            String expression = "^.*((youtu.be"+ "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches())
            {
                String groupIndex1 = matcher.group(7);
                if(groupIndex1!=null && groupIndex1.length()==11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }
}



/*

    String[] keywords = title.toLowerCase().split(" ");
System.out.println(keywords);
        Map<String, Integer> kw = new HashMap<>();
        for (String w: keywords) {
        if (kw.containsKey(w)) {
        Integer d = kw.get(w);
        kw.replace(w, d + 1);
        } else kw.put(w, 1);
        }
        for (String w: kw.keySet()) { System.out.println(w);}*/

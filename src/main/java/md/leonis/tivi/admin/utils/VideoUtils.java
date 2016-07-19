package md.leonis.tivi.admin.utils;

import com.google.gson.reflect.TypeToken;
import md.leonis.tivi.admin.model.Category;
import md.leonis.tivi.admin.model.Video;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoUtils {
    public static List<Category> categories = new ArrayList<>();

    public static Video video;

    public static void showAddVideo() {
        video = new Video();
        JavaFxUtils.showPane("video/AddVideo.fxml");
    }

    public static void showAddVideo2() {
        categories = VideoUtils.readCategories();
        JavaFxUtils.showPane("video/AddVideo2.fxml");
    }

    public static void showAddVideo3() {
        JavaFxUtils.showPane("video/AddVideo3.fxml");
    }

    public static void parseVideoPage() {
        parseUrl(video);
        parsePage(video);
    }

    public static void addVideo() {
        String json = JsonUtils.gson.toJson(video);
        try {
            String res = VideoUtils.addVideo(json, video.getImage(), null, video.getPreviousImage());
            System.out.println("OK Add Video");
            System.out.println(res);
        } catch (Throwable e) {
            System.out.println("Error Add Video");
            System.out.println(e.getMessage());
            //TODO window with error
        }
    }
    private static void parseUrl(Video video) {
        if (video.getUrl().isEmpty())
          video.setUrl(Config.sampleVideo);
        video.setYid(getYoutubeVideoId(video.getUrl()));
    }

    private static void parsePage(Video video) {
        try {
            Document doc = Jsoup.connect(video.getUrl()).get();
            Element div = doc.getElementById("watch7-user-header");
            URL aURL = new URL(video.getUrl());

            video.setTitle(doc.getElementById("eow-title").text());
            video.setText(doc.getElementById("eow-description").html());
            video.setAuthor(div.select("a.yt-uix-sessionlink").get(1).text());
            video.setAuthorSite(aURL.getProtocol() + "://" + aURL.getAuthority() + div.select("a.yt-uix-sessionlink").get(1).attr("href"));
            // TODO резать длинные названия
            video.setCpu(Translit.toTranslit(video.getTitle()).toLowerCase().replace(' ', '_').replaceAll("[^\\w\\s]", "").replace("__", "_"));
            // TODO keywords - add own, generate
            video.setKeywords(doc.select("meta[name=keywords]").first().attr("content"));
            video.setDescription(doc.select("meta[name=description]").get(0).attr("content"));
            video.setText(doc.getElementById("eow-description").html());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getYoutubeVideoId(String youtubeUrl) {
        String video_id = "";
        if (youtubeUrl != null && youtubeUrl.startsWith("http")) {
            String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(youtubeUrl);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        return video_id;
    }

    private static List<Category> readCategories() {
        List<Category> videoCategories = new ArrayList<>();
        String requestURL = Config.apiPath + "video.php?to=cat";
        try {
            String jsonString = HttpUtils.readFromUrl(requestURL);
            videoCategories = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<Category>>(){}.getType());
        } catch (IOException e) {
            System.out.println("Error in readVideoCategories");
        }
        return videoCategories;
    }

    public static String addVideo(String json, String imageName, InputStream inputStream, String deleteName) throws IOException {
        String requestURL = Config.apiPath + "video.php?to=add";
        MultipartUtility multipart;
        try {
            multipart = new MultipartUtility(requestURL, "UTF-8");
            multipart.addHeaderField("User-Agent", "TiVi's admin client");
            if (!deleteName.isEmpty()) {
                multipart.addFormField("delete", deleteName);
            }
            multipart.addJson("json", json);
            if (inputStream != null) {
                multipart.addInputStream("image", imageName, inputStream);
            }
        } catch (IOException ex) {
            return ex.getMessage();
        }
        return multipart.finish();
    }
}


/* //keywords processing. unfinished.

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

package md.leonis.tivi.admin.utils;

import com.google.gson.reflect.TypeToken;
import md.leonis.tivi.admin.model.*;
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
import java.util.stream.Collectors;

public class VideoUtils {

    public enum Actions {ADD, EDIT, CLONE}

    public static Actions action;

    public static List<Category> categories = new ArrayList<>();

    public static Video video;

    public static List<VideoView> videous;

    public static ListVideousSettings listVideousSettings = new ListVideousSettings();

    public static int videousCount;

    public static void showAddVideo() {
        JavaFxUtils.showPane("video/AddVideo.fxml");
    }

    public static void showAddVideo2() {
        categories = VideoUtils.readCategories();
        JavaFxUtils.showPane("video/AddVideo2.fxml");
    }

    public static void showAddVideo3() {
        JavaFxUtils.showPane("video/AddVideo3.fxml");
    }

    public static void showListVideous() {
        categories = VideoUtils.readCategories();
        countVideos();
        //listVideos();
        JavaFxUtils.showPane("video/ListVideos.fxml");
    }

    public static void parseVideoPage() {
        parseUrl(video);
        parsePage(video);
    }

    public static void addVideo() {
        if (!video.getYid().isEmpty()) video.setUrl(video.getYid());
        String json = JsonUtils.gson.toJson(video);
        try {
            VideoUtils.addVideo(json, video.getImage(), null, video.getPreviousImage());
            System.out.println("OK Add/Edit/Clone Video");
        } catch (Throwable e) {
            System.out.println("Error Add/Edit/Clone Video");
            System.out.println(e.getMessage());
            //TODO window with error
        }
    }

    public static void parseUrl(Video video) {
        if (video.getUrl().isEmpty() && action == Actions.ADD) video.setUrl(Config.sampleVideo);
        if (!video.getUrl().startsWith("http")) video.setUrl("https://www.youtube.com/watch?v=" + video.getUrl());
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
            String jsonString = WebUtils.readFromUrl(requestURL);
            videoCategories = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<Category>>(){}.getType());
        } catch (IOException e) {
            System.out.println("Error in readVideoCategories");
        }
        return videoCategories;
    }

    public static void listVideos() {
        //$count,$page,$cat,$sort,$order;
        List<Video> videos = new ArrayList<>();
        String cat = "";
        if (listVideousSettings.catId != -1) cat = "&cat=" + listVideousSettings.catId;
        String requestURL = Config.apiPath + "video.php?to=list&count=" + listVideousSettings.count +"&page=" + listVideousSettings.page + cat + "&sort=" + listVideousSettings.sort + "&order=" + listVideousSettings.order;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            videos = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<Video>>(){}.getType());
        } catch (IOException e) {
            System.out.println("Error in listVideos");
        }
        videous = videos.stream().map(VideoView::new).collect(Collectors.toList());
    }

    public static void countVideos() {
        String cat = "";
        if (listVideousSettings.catId != -1) cat = "&cat=" + listVideousSettings.catId;
        String requestURL = Config.apiPath + "video.php?to=count" + cat;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            videousCount = JsonUtils.gson.fromJson(jsonString, Count.class).getCount();
        } catch (IOException e) {
            System.out.println("Error in countVideos");
        }
    }

    public static void getVideo(int id) {
        String requestURL = Config.apiPath + "video.php?to=get&id=" + id;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            video = JsonUtils.gson.fromJson(jsonString, Video.class);
        } catch (IOException e) {
            System.out.println("Error in getVideo");
        }
    }

    public static void deleteVideo(int id) {
        String requestURL = Config.apiPath + "video.php?to=delete&id=" + id;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            System.out.println(jsonString);
        } catch (IOException e) {
            System.out.println("Error in deleteVideo");
        }
    }

    static boolean checkCpuExist(String cpu) {
        String requestURL = Config.apiPath + "video.php?to=getByCpu&cpu=" + cpu;
        Video vid = null;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            vid = JsonUtils.gson.fromJson(jsonString, Video.class);
        } catch (IOException e) {
            System.out.println("Error in getVideo");
        }
        return vid != null && vid.getCpu().equals(cpu);
    }

    public static String addVideo(String json, String imageName, InputStream inputStream, String deleteName) throws IOException {
        if (!imageName.isEmpty()) deleteName = "";
        String requestURL = Config.apiPath + "video.php?to=add";
        if (action == Actions.EDIT) {
            requestURL = Config.apiPath + "video.php?to=save";
        }
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

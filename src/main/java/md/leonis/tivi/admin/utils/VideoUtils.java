package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.model.Video;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VideoUtils {
    public static void parseUrl(String url, Video video) {
        video.setUrl(url);
        video.setUrl("https://www.youtube.com/watch?v=3q7QIdI6O-8");
        video.setYid(getYoutubeVideoId(video.getUrl()));
        System.out.println(video.getYid());
    }

    public static void parsePage(Video video) {
        System.out.println("-------------------------");
        //load, parse
        try {
            Document doc = Jsoup.connect(video.getUrl()).get();
            Element div = doc.getElementById("watch7-user-header");
            URL aURL = new URL(video.getUrl());

            video.setTitle(doc.getElementById("eow-title").text());
            video.setText(doc.getElementById("eow-description").html());
            video.setAuthor(div.select("a.yt-uix-sessionlink").get(1).text());
            video.setAuthorUrl(aURL.getProtocol() +"://" + aURL.getAuthority() + div.select("a.yt-uix-sessionlink").get(1).attr("href"));
            // TODO проверять, есть ли такой тайтл в базе
            // TODO резать длинные названия
            video.setCpu(Translit.toTranslit(video.getTitle()).toLowerCase().replace(' ', '_').replaceAll("[^\\w\\s]","").replace("__", "_"));
            // TODO keywords - add own, generate
            video.setKeywords(doc.select("meta[name=keywords]").first().attr("content"));
            video.setDescription(doc.select("meta[name=description]").get(0).attr("content"));


            System.out.println(video);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //get images
        //http://i3.ytimg.com/vi/Tuy1UQjZYCQ/0.jpg
        //1-3: 120x90
    }

    private static String getYoutubeVideoId(String youtubeUrl) {
        String video_id = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {
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

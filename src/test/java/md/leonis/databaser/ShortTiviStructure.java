package md.leonis.databaser;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"n", "id", "sys", "created", "modified", "sid", "cpu", "title", "name", "descript", "keywords", "region", "publisher", "developer",
        "god", "god1", "ngamers", "type", "genre", "image1", "image2", "image3", "image4", "image5", "image6", "image7", "image8",
        "image9", "image10", "image11", "image12", "image13", "image14", "game", "downloaded", "music", "music_downloaded", "rom",
        "playable", "played", "comment", "text1", "text2", "analog", "drname", "cros", "serie", "rating", "userrating", "totalrating", "viewes", "comments",
        "mgamers", "gen", "serial", "yrating", "price", "rarity", "barcode,"})
public class ShortTiviStructure implements Cloneable {

    private String sys;
    private String sid;
    @JsonAlias("title")
    private String name;

    private String region;
    private String publisher;
    private String developer;
    private String god;
    private String god1;
    private int ngamers;
    private String type;
    private String genre;

    private String image1;
    private String image2;
    private String image3;
    private String image4;
    private String image5;
    private String image6;
    private String image7;
    private String image8;
    private String image9;
    private String image10;
    private String image11;
    private String image12;
    private String image13;
    private String image14;

    @JsonAlias("comment")
    private String text1;
    private String text2;
    private String analog;
    private String drname;
    private String cros;
    private String serie;

    private int rating;

    @JsonIgnore
    public ShortTiviStructure corrected() {
        return this;
    }

    @JsonIgnore
    private String notNull(String str) {
        return str = str == null ? "" : str;
    }

    @JsonIgnore
    public List<String> getImages() {
        List<String> images = new ArrayList<>();
        if (image1 != null && !image1.isEmpty()) {
            images.add(image1);
        }
        if (image2 != null && !image2.isEmpty()) {
            images.add(image2);
        }
        if (image3 != null && !image3.isEmpty()) {
            images.add(image3);
        }
        if (image4 != null && !image4.isEmpty()) {
            images.add(image4);
        }
        if (image5 != null && !image5.isEmpty()) {
            images.add(image5);
        }
        if (image6 != null && !image6.isEmpty()) {
            images.add(image6);
        }
        if (image7 != null && !image7.isEmpty()) {
            images.add(image7);
        }
        if (image8 != null && !image8.isEmpty()) {
            images.add(image8);
        }
        if (image9 != null && !image9.isEmpty()) {
            images.add(image9);
        }
        if (image10 != null && !image10.isEmpty()) {
            images.add(image10);
        }
        if (image11 != null && !image11.isEmpty()) {
            images.add(image11);
        }
        if (image12 != null && !image12.isEmpty()) {
            images.add(image12);
        }
        if (image13 != null && !image13.isEmpty()) {
            images.add(image13);
        }
        if (image14 != null && !image14.isEmpty()) {
            images.add(image14);
        }

        return images;
    }
}

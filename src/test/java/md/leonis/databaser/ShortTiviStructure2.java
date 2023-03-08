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
public class ShortTiviStructure2 implements Cloneable {

    private String sys;
    private String sid;
    @JsonAlias("title")
    private String name;

    private String region;

    // DataBaser
    private int mgamers;
    private String gen;

    private String serial;
    private String yrating;
    private String price;
    private String rarity;
    private String barcode;

    @JsonIgnore
    public ShortTiviStructure2 corrected() {
        gen = notNull(gen);
        serial = notNull(serial);
        yrating = notNull(yrating);
        price = notNull(price);
        rarity = notNull(rarity);
        barcode = notNull(barcode);
        return this;
    }

    @JsonIgnore
    private String notNull(String str) {
        return str == null ? "" : str;
    }
}

package md.leonis.tivi.admin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.text.DecimalFormat;

@Getter
@Setter
public class Video {
    // Rename to id
    @SerializedName("downid")
    private int id;

    @SerializedName("catid")
    private int categoryId = 0;

    // Rename to date
    @SerializedName("public")
    private long date = System.currentTimeMillis() / 1000L;

    // Not need???
    @SerializedName("stpublic")
    private long startDate = 0;

    // Not need???
    @SerializedName("unpublic")
    private long endDatedate = 0;

    private String cpu = "";

    // Rename
    @SerializedName("locurl")
    private String url;

    @Expose(serialize = false, deserialize = false)
    private String yid = "";

    // Not need
    @SerializedName("exturl")
    private String mirror = "";

    // Rename to age, int
    @SerializedName("extsize")
    private String age = "";

    // Rename to description
    @SerializedName("descript")
    private String description = "";

    @SerializedName("keywords")
    private String keywords = "";

    private String title = "";

    @SerializedName("textshort")
    private String text = "";

    // Not need???
    @SerializedName("textmore")
    private String fullText = "";

    @SerializedName("textnotice")
    private String userText = "";

    // Not need
    @SerializedName("mirrorsname")
    private String mirrorsname = "";

    // Not need
    @SerializedName("mirrorsurl")
    private String mirrorsurl = "";

    // Rename to platforms
    @SerializedName("relisdown")
    private String platforms = "";

    // Rename to author
    @SerializedName("authdown")
    private String author = "";

    @SerializedName("sitedown")
    private String authorSite = "";

    @SerializedName("maildown")
    private String authorEmail = "";

    @SerializedName("image")
    private String image = "";

    @Expose(serialize = false, deserialize = false)
    private String previousImage = "";

    @SerializedName("image_alt")
    private String imageAlt = "";

    // Rename to og_image
    @SerializedName("image_thumb")
    private String OpenGraphImage = "";

    // Not need
    @SerializedName("image_align")
    private String image_align = "left";

    // Rename to views
    @SerializedName("hits")
    private int views = 0;

    // Not need
    @SerializedName("trans")
    private int loads = 0;

    @SerializedName("lastdown")
    private long lastdown = 0;

    // Rename
    @SerializedName("rating")
    private int rated_count = 0;

    // Rename
    @SerializedName("totalrating")
    private int total_rating = 0;

    // Rename
    @SerializedName("act")
    private YesNo active = YesNo.yes;

    // Rename
    @SerializedName("acc")
    private Access access = Access.all;

    // Not need
    @SerializedName("listid")
    private int listid = 0;

    @SerializedName("comments")
    private int comments = 0;

    @SerializedName("tags")
    private String tags = "";

    public String getRate() {
        if (rated_count == 0) return "нет";
        return new DecimalFormat("#0.00").format(total_rating * 1.0 / rated_count);
    }
}

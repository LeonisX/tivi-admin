package md.leonis.tivi.admin.model.danneo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.text.DecimalFormat;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class Video {
    // Rename to id
    @SerializedName("downid")
    private Integer id;

    @SerializedName("catid")
    private Integer categoryId = 0;

    // Rename to date
    @SerializedName("public")
    private Long date = System.currentTimeMillis() / 1000L;

    // Not need???
    @SerializedName("stpublic")
    private Long startDate = 0L;

    // Not need???
    @SerializedName("unpublic")
    private Long endDatedate = 0L;

    private String cpu = "";

    // Rename
    @SerializedName("locurl")
    private String url;

    @Expose(serialize = false, deserialize = false)
    transient private String yid = "";

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
    transient private String previousImage = "";

    // Rename to og_image
    @SerializedName("image_thumb")
    private String OpenGraphImage = "";

    // Not need
    @SerializedName("image_align")
    private String image_align = "left";

    @SerializedName("image_alt")
    private String imageAlt = "";

    // Rename to views
    @SerializedName("hits")
    private Integer views = 0;

    // Not need
    @SerializedName("trans")
    private Integer loads = 0;

    @SerializedName("lastdown")
    private Long lastdown = 0L;

    // Rename
    @SerializedName("rating")
    private Integer rated_count = 0;

    // Rename
    @SerializedName("totalrating")
    private Integer total_rating = 0;

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

    public Video(Video other) {
        this.id = other.id;
        this.categoryId = other.categoryId;
        this.date = other.date;
        this.startDate = other.startDate;
        this.endDatedate = other.endDatedate;
        this.cpu = other.cpu;
        this.url = other.url;
        this.yid = other.yid;
        this.mirror = other.mirror;
        this.age = other.age;
        this.description = other.description;
        this.keywords = other.keywords;
        this.title = other.title;
        this.text = other.text;
        this.fullText = other.fullText;
        this.userText = other.userText;
        this.mirrorsname = other.mirrorsname;
        this.mirrorsurl = other.mirrorsurl;
        this.platforms = other.platforms;
        this.author = other.author;
        this.authorSite = other.authorSite;
        this.authorEmail = other.authorEmail;
        this.image = other.image;
        this.previousImage = other.previousImage;
        this.OpenGraphImage = other.OpenGraphImage;
        this.image_align = other.image_align;
        this.imageAlt = other.imageAlt;
        this.views = other.views;
        this.loads = other.loads;
        this.lastdown = other.lastdown;
        this.rated_count = other.rated_count;
        this.total_rating = other.total_rating;
        this.active = other.active;
        this.access = other.access;
        this.listid = other.listid;
        this.comments = other.comments;
        this.tags = other.tags;
    }

    public String getRate() {
        if (rated_count == 0) return "нет";
        return new DecimalFormat("#0.00").format(total_rating * 1.0 / rated_count);
    }
}

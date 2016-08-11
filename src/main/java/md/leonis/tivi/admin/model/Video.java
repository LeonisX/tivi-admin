package md.leonis.tivi.admin.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDatedate() {
        return endDatedate;
    }

    public void setEndDatedate(long endDatedate) {
        this.endDatedate = endDatedate;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getYid() {
        return yid;
    }

    public void setYid(String yid) {
        this.yid = yid;
    }

    public String getMirror() {
        return mirror;
    }

    public void setMirror(String mirror) {
        this.mirror = mirror;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public String getMirrorsname() {
        return mirrorsname;
    }

    public void setMirrorsname(String mirrorsname) {
        this.mirrorsname = mirrorsname;
    }

    public String getMirrorsurl() {
        return mirrorsurl;
    }

    public void setMirrorsurl(String mirrorsurl) {
        this.mirrorsurl = mirrorsurl;
    }

    public String getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String platforms) {
        this.platforms = platforms;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorSite() {
        return authorSite;
    }

    public void setAuthorSite(String authorSite) {
        this.authorSite = authorSite;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public String getOpenGraphImage() {
        return OpenGraphImage;
    }

    public void setOpenGraphImage(String openGraphImage) {
        OpenGraphImage = openGraphImage;
    }

    public String getImage_align() {
        return image_align;
    }

    public void setImage_align(String image_align) {
        this.image_align = image_align;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getLoads() {
        return loads;
    }

    public void setLoads(int loads) {
        this.loads = loads;
    }

    public long getLastdown() {
        return lastdown;
    }

    public void setLastdown(long lastdown) {
        this.lastdown = lastdown;
    }

    public int getRated_count() {
        return rated_count;
    }

    public String getRate() {
        if (rated_count == 0) return "нет";
        return new DecimalFormat("#0.00").format(total_rating * 1.0 / rated_count);
    }

    public void setRated_count(int rated_count) {
        this.rated_count = rated_count;
    }

    public int getTotal_rating() {
        return total_rating;
    }

    public void setTotal_rating(int total_rating) {
        this.total_rating = total_rating;
    }

    public YesNo getActive() {
        return active;
    }

    public void setActive(YesNo active) {
        this.active = active;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public int getComments() {
        return comments;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", date=" + date +
                ", startDate=" + startDate +
                ", endDatedate=" + endDatedate +
                ", cpu='" + cpu + '\'' +
                ", url='" + url + '\'' +
                ", yid='" + yid + '\'' +
                ", mirror='" + mirror + '\'' +
                ", age='" + age + '\'' +
                ", description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", fullText='" + fullText + '\'' +
                ", userText='" + userText + '\'' +
                ", mirrorsname='" + mirrorsname + '\'' +
                ", mirrorsurl='" + mirrorsurl + '\'' +
                ", platforms='" + platforms + '\'' +
                ", author='" + author + '\'' +
                ", authorSite='" + authorSite + '\'' +
                ", authorEmail='" + authorEmail + '\'' +
                ", image='" + image + '\'' +
                ", imageAlt='" + imageAlt + '\'' +
                ", OpenGraphImage='" + OpenGraphImage + '\'' +
                ", image_align='" + image_align + '\'' +
                ", views=" + views +
                ", loads=" + loads +
                ", lastdown=" + lastdown +
                ", rated_count=" + rated_count +
                ", total_rating=" + total_rating +
                ", active=" + active +
                ", access=" + access +
                ", listid=" + listid +
                ", comments=" + comments +
                ", tags='" + tags + '\'' +
                '}';
    }

    public String getPreviousImage() {
        return previousImage;
    }

    public void setPreviousImage(String previousImage) {
        this.previousImage = previousImage;
    }
}

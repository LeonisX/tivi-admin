package md.leonis.tivi.admin.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Video {
    //public StringProperty title = new SimpleStringProperty("");
    //public StringProperty cpu = new SimpleStringProperty("");
    public String title = "";
    public String cpu = "";
    public Long data = System.currentTimeMillis() / 1000L;
    public String category = "";
    public String description = "";
    public String keywords = "";
    public String year = "";
    public String url = "";
    public String originUrl = "";
    public String yid = "";
    public String text = "";
    public String fullText = "";
    public String author = "";
    public String authorUrl = "";
    public String authorEmail = "";
    public int views = 0;
    public int loads = 0;
    public String image = "";
    public String imageAlt = "";
    public Boolean state = true;


    @Override
    public String toString() {
        return "Video{\n" +
                "title='" + title + '\'' + '\n' +
                ", cpu='" + cpu + '\'' + '\n' +
                ", data='" + data + '\'' + '\n' +
                ", category='" + category + '\'' + '\n' +
                ", description='" + description + '\'' + '\n' +
                ", keywords='" + keywords + '\'' + '\n' +
                ", year=" + year + '\n' +
                ", url='" + url + '\'' + '\n' +
                ", originUrl='" + originUrl + '\'' + '\n' +
                ", yid='" + yid + '\'' + '\n' +
                ", text='" + text + '\'' + '\n' +
                ", fullText='" + fullText + '\'' + '\n' +
                ", author='" + author + '\'' + '\n' +
                ", authorUrl='" + authorUrl + '\'' + '\n' +
                ", authorEmail='" + authorEmail + '\'' + '\n' +
                ", views=" + views + '\n' +
                ", loads=" + loads + '\n' +
                ", image='" + image + '\'' + '\n' +
                ", imageAlt='" + imageAlt + '\'' + '\n' +
                ", state=" + state + '\n' +
                '}';
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getYid() {
        return yid;
    }

    public void setYid(String yid) {
        this.yid = yid;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorUrl() {
        return authorUrl;
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
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

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
}

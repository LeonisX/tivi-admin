package md.leonis.tivi.admin.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Video {
    //public StringProperty title = new SimpleStringProperty("");
    //public StringProperty cpu = new SimpleStringProperty("");
    public String title = "";
    public String cpu = "";
    public StringProperty data = new SimpleStringProperty("");
    public String category = "";
    public StringProperty description = new SimpleStringProperty("");
    public StringProperty keywords = new SimpleStringProperty("");
    public int year = -1;
    public StringProperty url = new SimpleStringProperty("");
    public StringProperty originUrl = new SimpleStringProperty("");
    public StringProperty yid = new SimpleStringProperty("");
    public StringProperty text = new SimpleStringProperty("");
    public StringProperty fullText = new SimpleStringProperty("");
    public StringProperty author = new SimpleStringProperty("");
    public StringProperty authorUrl = new SimpleStringProperty("");
    public StringProperty authorEmail = new SimpleStringProperty("");
    public int views = 0;
    public int loads = 0;
    public StringProperty image = new SimpleStringProperty("");
    public StringProperty imageAlt = new SimpleStringProperty("");
    public Boolean state = true;

    public String getUrl() {
        return url.get();
    }

    public void setUrl(String url) {
        this.url.setValue(url);
    }

    public String getYid() {
        return yid.get();
    }

    public void setYid(String yid) {
        this.yid.set(yid);
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

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public String getAuthorUrl() {
        return authorUrl.get();
    }

    public void setAuthorUrl(String authorUrl) {
        this.authorUrl.set(authorUrl);
    }

    public String getData() {
        return data.get();
    }

    public void setData(String data) {
        this.data.set(data);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getKeywords() {
        return keywords.get();
    }

    public void setKeywords(String keywords) {
        this.keywords.set(keywords);
    }

    public String getOriginUrl() {
        return originUrl.get();
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl.set(originUrl);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public String getFullText() {
        return fullText.get();
    }

    public void setFullText(String fullText) {
        this.fullText.set(fullText);
    }

    public String getAuthorEmail() {
        return authorEmail.get();
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail.set(authorEmail);
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
        return image.get();
    }

    public void setImage(String image) {
        this.image.set(image);
    }

    public String getImageAlt() {
        return imageAlt.get();
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt.set(imageAlt);
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

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
}

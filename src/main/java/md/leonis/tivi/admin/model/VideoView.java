package md.leonis.tivi.admin.model;

import javafx.beans.property.*;

public class VideoView {
    public IntegerProperty id;
    public StringProperty title;
    //private StringProperty category;
    private LongProperty published;
    private IntegerProperty views;
    private IntegerProperty comments;
    private BooleanProperty v1;
    private BooleanProperty checked;

    public VideoView(Video video) {
        this.id = new SimpleIntegerProperty(video.getId());
        this.title = new SimpleStringProperty(video.getTitle());
        //private String category;
        this.published = new SimpleLongProperty(video.getDate());
        this.views = new SimpleIntegerProperty(video.getViews());
        this.comments = new SimpleIntegerProperty(video.getComments());
        this.v1 = new SimpleBooleanProperty(false);
        this.checked = new SimpleBooleanProperty(false);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public LongProperty publishedProperty() {
        return published;
    }

    public IntegerProperty viewsProperty() {
        return views;
    }

    public IntegerProperty commentsProperty() {
        return comments;
    }

    public BooleanProperty v1Property() {
        return v1;
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public long getPublished() {
        return published.get();
    }

    public void setPublished(long published) {
        this.published.set(published);
    }

    public int getViews() {
        return views.get();
    }

    public void setViews(int views) {
        this.views.set(views);
    }

    public int getComments() {
        return comments.get();
    }

    public void setComments(int comments) {
        this.comments.set(comments);
    }

    public boolean isV1() {
        return v1.get();
    }

    public void setV1(boolean v1) {
        this.v1.set(v1);
    }

    public boolean isChecked() {
        return checked.get();
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }

    @Override
    public String toString() {
        return "VideoView{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", views=" + views +
                ", comments=" + comments +
                ", v1=" + v1 +
                ", checked=" + checked +
                '}';
    }
}

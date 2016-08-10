package md.leonis.tivi.admin.model;

import javafx.beans.property.*;

public class VideoView {
    public IntegerProperty id;
    public StringProperty title;
    private StringProperty category;
    private LongProperty published;
    private IntegerProperty views;
    private IntegerProperty comments;
    private BooleanProperty checked;

    public VideoView(Video video) {
        this.id = new SimpleIntegerProperty(video.getId());
        this.title = new SimpleStringProperty(video.getTitle());
        this.category = new SimpleStringProperty(Integer.toString(video.getCategoryId()));
        this.published = new SimpleLongProperty(video.getDate());
        this.views = new SimpleIntegerProperty(video.getViews());
        this.comments = new SimpleIntegerProperty(video.getComments());
        this.checked = new SimpleBooleanProperty(false);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty categoryProperty() {
        return category;
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

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public boolean isChecked() {
        return checked.get();
    }

    public void setChecked(boolean checked) {
        this.checked.set(checked);
    }
}

package md.leonis.tivi.admin.model.gui;

import javafx.beans.property.*;
import md.leonis.tivi.admin.model.danneo.Category;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.VideoUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class VideoView {
    public IntegerProperty id;
    private final StringProperty published;
    private final StringProperty cpu;
    private final IntegerProperty views;
    private final IntegerProperty comments;
    private final StringProperty rating;
    private final BooleanProperty checked;
    private final ObjectProperty<MixedTitle> mixedTitle;

    public VideoView(Video video) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.id = new SimpleIntegerProperty(video.getId());
        this.published = new SimpleStringProperty(dateFormat.format(new Date(video.getDate() * 1000)));
        this.cpu = new SimpleStringProperty(video.getCpu());
        this.views = new SimpleIntegerProperty(video.getViews());
        this.comments = new SimpleIntegerProperty(video.getComments());
        this.rating = new SimpleStringProperty(video.getRate() + " (" + video.getRated_count() + ")");
        this.checked = new SimpleBooleanProperty(false);
        Optional<Category> cats = VideoUtils.categories.stream().filter(cat -> cat.getCatid().equals(video.getCategoryId())).findFirst();
        String cat = Integer.toString(video.getCategoryId());
        if (cats.isPresent()) cat = cats.get().getCatname();
        this.mixedTitle = new SimpleObjectProperty<>(new MixedTitle(video.getTitle(), cat));
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty publishedProperty() {
        return published;
    }

    public StringProperty cpuProperty() {
        return cpu;
    }

    public IntegerProperty viewsProperty() {
        return views;
    }

    public IntegerProperty commentsProperty() {
        return comments;
    }

    public StringProperty ratingProperty() {
        return rating;
    }

    public BooleanProperty checkedProperty() {
        return checked;
    }

    public ObjectProperty<MixedTitle> mixedTitleProperty() {
        return mixedTitle;
    }
}

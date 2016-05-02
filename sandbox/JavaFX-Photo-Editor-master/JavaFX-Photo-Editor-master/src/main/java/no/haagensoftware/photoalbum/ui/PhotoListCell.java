package no.haagensoftware.photoalbum.ui;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import no.haagensoftware.photoalbum.data.Photo;


public class PhotoListCell extends ListCell<Photo> {

    @Override
    protected void updateItem(Photo photo, boolean b) {
        super.updateItem(photo, b);

        if (photo != null) {
            HBox hBox = new HBox(2);

            Image image = new Image(photo.getPhotoFilename());
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setFitWidth(75);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);

            hBox.getChildren().addAll(imageView, new Label(photo.getPhotoName()));

            setGraphic(hBox);
        }
    }
}

package no.haagensoftware.photoalbum.controllers;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import no.haagensoftware.photoalbum.data.Photo;
import no.haagensoftware.photoalbum.exec.Application;
import no.haagensoftware.photoalbum.ui.PhotoListCell;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {
    private static Logger logger = Logger.getLogger(MainController.class.getName());
    private Stage primaryStage;
    private ObservableList<Photo> photos = FXCollections.observableArrayList();

    private DoubleProperty brightnessProperty = new SimpleDoubleProperty(1);
    private DoubleProperty saturationProperty = new SimpleDoubleProperty(0);
    private DoubleProperty sepiaProperty = new SimpleDoubleProperty(0);

    @FXML private ListView<Photo> photoListView;
    @FXML private ImageView imageView;
    @FXML private VBox vBox;
    @FXML private Label photoNameLabel;
    @FXML private HBox brightnessBox;
    @FXML private Slider brightnessSlider;
    @FXML private HBox sepiaBox;
    @FXML private Slider sepiaSlider;
    @FXML private HBox buttonBox;

    @FXML private HBox saturationBox;
    @FXML private Slider saturationSlider;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setupUi() {
        photoListView.setItems(photos);

        photoListView.setCellFactory(new Callback<ListView<Photo>, ListCell<Photo>>() {
            @Override
            public ListCell<Photo> call(ListView<Photo> list) {
                return new PhotoListCell();
            }
        });

        photoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        photoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Photo>() {
            @Override
            public void changed(ObservableValue<? extends Photo> observableValue, Photo oldPhoto, Photo newPhoto) {
                if (newPhoto != null) {
                    imageView.setImage(new Image(newPhoto.getPhotoFilename()));
                    photoNameLabel.setText(newPhoto.getPhotoName());
                }
            }
        });

        photos.add(new Photo("Bird", "bird.jpg"));
        photos.add(new Photo("Dragonfly", "dragonfly.jpg"));
        photos.add(new Photo("Fly", "fly.jpg"));
        photos.add(new Photo("Frog", "frog.jpg"));
        photos.add(new Photo("Lizard", "lizard.jpg"));
        photos.add(new Photo("mountain", "mountain.jpg"));
        photos.add(new Photo("mountain2", "mountain2.jpg"));
        photos.add(new Photo("panorama", "panorama.jpg"));
        photos.add(new Photo("sheep", "sheep.jpg"));
        photos.add(new Photo("waterfall", "waterfall.jpg"));

        primaryStage.getScene().widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                vBox.setMaxWidth(primaryStage.getScene().getWidth() - photoListView.getWidth());
                imageView.setFitWidth(vBox.getMaxWidth());
            }
        });

        primaryStage.getScene().heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                vBox.setMaxHeight(primaryStage.getScene().getHeight());
                imageView.setFitHeight(calculateMaxImageHeight());
            }
        });

        vBox.setMaxWidth(primaryStage.getScene().getWidth() - photoListView.getWidth());
        imageView.setFitWidth(vBox.getMaxWidth());

        vBox.setMaxHeight(primaryStage.getScene().getHeight());
        imageView.setFitHeight(calculateMaxImageHeight());

        brightnessSlider.valueProperty().bindBidirectional(brightnessProperty);
        brightnessProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setImageEffect();
            }
        });

        saturationSlider.valueProperty().bindBidirectional(saturationProperty);
        saturationProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setImageEffect();
            }
        });

        sepiaSlider.valueProperty().bindBidirectional(sepiaProperty);
        sepiaProperty.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                setImageEffect();
            }
        });
    }

    @FXML
    public void normalAction() {
        brightnessProperty.set(1);
        saturationProperty.set(0);
        sepiaProperty.set(0);
    }

    @FXML
    public void brightAction() {
        brightnessProperty.set(1.5);
        saturationProperty.set(0);
        sepiaProperty.set(0);
    }

    @FXML
    public void dimAction() {
        brightnessProperty.set(0.5);
        saturationProperty.set(0.2);
        sepiaProperty.set(0);
    }

    @FXML
    public void oldSchoolLook() {
        brightnessProperty.set(1.3);
        saturationProperty.set(0.2);
        sepiaProperty.set(0.7);
        Image image1 = new Image("file:///home/leonis/Изображения/Leonardo-2003-cartoon.jpg", 200, 150, true, true);

        //Image image1 = new Image(new File("a.jpg").toURI().toString(), 100, 150, false, false);

        imageView.setImage(image1);
        saveToFile(imageView.getImage());
    }

    public static void saveToFile(Image image) {
        File outputFile = new File("/home/leonis/eee2.png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setImageEffect() {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness((1 - brightnessProperty.get()) * -1);
        colorAdjust.setSaturation(saturationProperty.get());

        SepiaTone st = new SepiaTone();
        st.setLevel(sepiaProperty.get());

        colorAdjust.setInput(st);

        imageView.setEffect(colorAdjust);
    }

    private double calculateMaxImageHeight() {
        return vBox.getMaxHeight() - photoNameLabel.getHeight() - brightnessBox.getHeight() - saturationBox.getHeight() - buttonBox.getHeight() - sepiaSlider.getHeight();
    }

    @FXML
    public void onListChange() {
        logger.info("onListChange");
    }



    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}

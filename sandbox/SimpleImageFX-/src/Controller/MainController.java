package Controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends Control implements Initializable{
    private Stage stage;
    private Model.Image image;
    @FXML
    private ImageView imageView;
    @FXML
    private Slider sliderBrightness;
    @FXML
    private Slider sliderContrast;
    @FXML
    private Slider sliderRed;
    @FXML
    private Slider sliderGreen;
    @FXML
    private Slider sliderBlue;
    @FXML
    private CheckBox checkBoxNegative;
    @FXML
    private CheckBox checkBoxSharp;
    @FXML
    private CheckBox checkBoxHE;
    @FXML
    private CheckBox checkBoxDistorting;
    @FXML
    private Slider sliderX;
    @FXML
    private Slider sliderY;
    @FXML
    private Slider sliderRadius;
    @FXML
    private Slider sliderMultiple;

    public void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    private void openPic(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
        try {
            image = new Model.Image(ImageIO.read(file));
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(),null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveEffect(){
        image.setSrcImage(image.getImage());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sliderBrightness.setMin(-50);
        sliderBrightness.setMax(50);
        sliderBrightness.setValue(0);
        sliderContrast.setMin(-50);
        sliderContrast.setMax(50);
        sliderContrast.setValue(0);
        sliderRed.setMin(-50);
        sliderRed.setMax(50);
        sliderRed.setValue(0);
        sliderGreen.setMin(-50);
        sliderGreen.setMax(50);
        sliderGreen.setValue(0);
        sliderBlue.setMin(-50);
        sliderBlue.setMax(50);
        sliderBlue.setValue(0);
        sliderX.setMin(0);
        sliderX.setMax(100);
        sliderX.setValue(50);
        sliderY.setMin(0);
        sliderY.setMax(100);
        sliderY.setValue(50);
        sliderRadius.setMin(0);
        sliderRadius.setMax(100);
        sliderRadius.setValue(50);
        sliderMultiple.setMin(1);
        sliderMultiple.setMax(2);
        sliderMultiple.setValue(1.5);

        sliderBrightness.valueProperty().addListener((observable, oldValue, newValue) -> {
            image.setBrightness(newValue.intValue());
            image.setMultiple(1);
            image.adjustImage();
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        });
        sliderContrast.valueProperty().addListener((observable, oldValue, newValue) -> {
            image.setContrast(newValue.intValue());
            image.adjustImage();
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        });
        sliderRed.valueProperty().addListener((observable, oldValue, newValue) -> {
            image.adjustColor(newValue.intValue(), (int)sliderGreen.getValue(), (int)sliderBlue.getValue());
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        });
        sliderGreen.valueProperty().addListener((observable, oldValue, newValue) -> {
            image.adjustColor((int)sliderRed.getValue(), newValue.intValue(), (int)sliderBlue.getValue());
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        });
        sliderBlue.valueProperty().addListener((observable, oldValue, newValue) -> {
            image.adjustColor((int)sliderRed.getValue(), (int)sliderGreen.getValue(), newValue.intValue());
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        });
        checkBoxSharp.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                image.sharpening();
            } else{
                image.setImage(image.getSrcImage());
            }
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        });
        checkBoxNegative.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                image.negative();
            } else{
                image.setImage(image.getSrcImage());
            }
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        });
        checkBoxHE.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                image.applyHE();
            } else{
                image.setImage(image.getSrcImage());
            }
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        });
        checkBoxDistorting.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue){
                image.distorting();
            } else{
                image.setImage(image.getSrcImage());
            }
            imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
        });
        sliderX.valueProperty().addListener((observable, oldValue, newValue) -> {
            image.setCenterX(newValue.intValue());
            if(checkBoxDistorting.isSelected()){
                image.distorting();
                imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
            }
        });
        sliderY.valueProperty().addListener((observable, oldValue, newValue) -> {
            image.setCenterY(newValue.intValue());
            if(checkBoxDistorting.isSelected()){
                image.distorting();
                imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
            }
        });
        sliderRadius.valueProperty().addListener((observable, oldValue, newValue) -> {
            image.setRadius(newValue.doubleValue());
            if(checkBoxDistorting.isSelected()){
                image.distorting();
                imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
            }
        });
        sliderMultiple.valueProperty().addListener((observable, oldValue, newValue) -> {
            image.setMultiple(newValue.doubleValue());
            if(checkBoxDistorting.isSelected()){
                image.distorting();
                imageView.setImage(SwingFXUtils.toFXImage(image.getImage(), null));
            }
        });
    }




}

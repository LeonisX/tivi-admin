package sample;


import java.io.File;

import application.Main;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ImageEditorUsingFX  extends Application
{
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage)
    {

        Image image = new Image(Main.class.getResourceAsStream(""));
        ImageView iv1 = new ImageView();
        iv1.setImage(image);

        primaryStage.setTitle("Image Editor Using JavaFX");

        Reflection r = new Reflection();
        r.setFraction(1);

        BoxBlur bb = new BoxBlur();
        bb.setWidth(5);
        bb.setHeight(5);
        bb.setIterations(3);

        Distant light = new Distant();
        light.setAzimuth(-135.0f);
        Lighting l = new Lighting();
        l.setLight(light);
        l.setSurfaceScale(5.0f);

        DropShadow ds = new DropShadow();
        ds.setOffsetY(30.0);
        ds.setOffsetX(3.0);
        ds.setColor(Color.BLACK);

        InnerShadow is = new InnerShadow();
        is.setOffsetX(20.0);
        is.setOffsetY(2.0);
        is.setColor(Color.RED);


        Button btnChooseImage = new Button();
        btnChooseImage.setText("Choose Image");

        btnChooseImage.setOnAction(new EventHandler<ActionEvent>()
                                   {
                                       @Override
                                       public void handle(ActionEvent event)
                                       {
                                           FileChooser fileChooser = new FileChooser();
                                           // FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");

                                           // fileChooser.getExtensionFilters().add(extFilter);

                                           File file = fileChooser.showOpenDialog(primaryStage);

                                           Image ima = new Image(file.toURI().toString());
                                           iv1.setImage(ima);

                                       }
                                   }
        );


        Button btnLightEffect = new Button();
        btnLightEffect.setText("Light Effect");

        btnLightEffect.setOnAction(new EventHandler<ActionEvent>()
                                   {
                                       @Override
                                       public void handle(ActionEvent event)
                                       {
                                           iv1.setEffect(l);

                                       }
                                   }
        );


        Button btnOuterShadow = new Button();
        btnOuterShadow.setText("Drop Shadow");

        btnOuterShadow.setOnAction(new EventHandler<ActionEvent>()
                                   {
                                       @Override
                                       public void handle(ActionEvent event)
                                       {

                                           iv1.setEffect(ds);

                                       }
                                   }
        );

        Button btnInnerShadow = new Button();
        btnInnerShadow.setText("Inner Shadow");

        btnInnerShadow.setOnAction(new EventHandler<ActionEvent>()
                                   {
                                       @Override
                                       public void handle(ActionEvent event)
                                       {

                                           iv1.setEffect(is);
                                       }
                                   }
        );

        Button btnBlur = new Button();
        btnBlur.setText("Blur");

        btnBlur.setOnAction(new EventHandler<ActionEvent>()
                            {
                                @Override
                                public void handle(ActionEvent event)
                                {
                                    iv1.setEffect(bb);
                                }
                            }
        );

        Button btnReflection = new Button();
        btnReflection.setText("Reflection");

        btnReflection.setOnAction(new EventHandler<ActionEvent>()
                                  {
                                      @Override
                                      public void handle(ActionEvent event)
                                      {
                                          iv1.setEffect(r);
                                      }
                                  }
        );


        VBox root = new VBox(5);

        HBox hbEffects=new HBox(5);
        HBox hbEffectsSlider=new HBox(5);
        HBox hbImage=new HBox();

        hbEffects.getChildren().add(btnChooseImage);
        hbEffects.getChildren().add(btnLightEffect);
        hbEffects.getChildren().add(btnOuterShadow);
        hbEffects.getChildren().add(btnInnerShadow);
        hbEffects.getChildren().add(btnBlur);
        hbEffects.getChildren().add(btnReflection);
        hbImage.getChildren().add(iv1);

        final Label opacityCaption = new Label("Opacity Level:");

        final Slider opacityLevel = new Slider(0, 1, 1);
        opacityLevel.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                iv1.setOpacity(new_val.doubleValue());

            }
        });

        final Label sepiaCaption = new Label("Sepia Tone:");
        final Slider sepiaTone = new Slider(0, 1, 1);
        final SepiaTone sepiaEffect = new SepiaTone();

        sepiaTone.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                sepiaEffect.setLevel(new_val.doubleValue());

                iv1.setEffect(sepiaEffect);
            }
        });

        final Label scalingCaption = new Label("Zoom :");
        final Slider scaling = new Slider (0.5, 1, 1);

        scaling.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                iv1.setScaleX(new_val.doubleValue());
                iv1.setScaleY(new_val.doubleValue());

            }
        });


        hbEffectsSlider.getChildren().add(opacityCaption);
        hbEffectsSlider.getChildren().add(opacityLevel);
        hbEffectsSlider.getChildren().add(sepiaCaption);
        hbEffectsSlider.getChildren().add(sepiaTone);
        hbEffectsSlider.getChildren().add(scalingCaption);
        hbEffectsSlider.getChildren().add(scaling);

        root.getChildren().add(hbEffects);
        root.getChildren().add(hbEffectsSlider);
        root.getChildren().add(hbImage);

        primaryStage.setScene(new Scene(root, 800, 500,Color.BLANCHEDALMOND));
        primaryStage.show();
    }
}
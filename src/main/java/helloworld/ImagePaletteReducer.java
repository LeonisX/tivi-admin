package helloworld;

import javafx.application.Application;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

//https://gist.github.com/jewelsea/5068499
// displays a button with a 64 color image palette and a full color palette when pressed.
public class ImagePaletteReducer extends Application {
    @Override public void start(final Stage stage) throws Exception {
        final Label response = new Label("");
        final Image originalImage = new Image("http://tv-games.ru/forum/customavatars/avatar3_14.gif");
        final Image resampledImage = resample(originalImage);
        final ImageView imageView = new ImageView(resampledImage);
        final Button button = new Button("I love you", imageView);
        button.setStyle("-fx-base: coral;");
        button.setContentDisplay(ContentDisplay.TOP);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                if ("".equals(response.getText())) {
                    response.setText("I love you too!");
                    imageView.setImage(originalImage);
                } else {
                    response.setText("");
                    imageView.setImage(resampledImage);
                }
            }
        });

        final VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(button, response);
        layout.setStyle("-fx-background-color: cornsilk; -fx-padding: 10; -fx-font-size: 20;");
        stage.setTitle("Heart");
        stage.getIcons().add(originalImage);
        stage.setScene(new Scene(layout));
        stage.show();
    }
    public static void main(String[] args) { launch(args); }

    // downsamples an image to a 64 color palette by only
    // using the 2 most significant bits of color to represent
    // each of the image's pixels.
    private Image resample(Image inputImage) {
        int W = (int) inputImage.getWidth();
        int H = (int) inputImage.getHeight();
        WritableImage outputImage = new WritableImage(W, H);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int argb = reader.getArgb(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >>  8) & 0xFF;
                int b =  argb        & 0xFF;

                r = r & 0xC0;
                g = g & 0xC0;
                b = b & 0xC0;

                argb = (a << 24) | (r << 16) | (g << 8) | b;
                writer.setArgb(x, y, argb);
            }
        }

        return outputImage;
    }
}
// icon license: (creative commons with attribution) http://creativecommons.org/licenses/by-nc-nd/3.0/
// icon artist attribution page: (eponas-deeway) http://eponas-deeway.deviantart.com/gallery/#/d1s7uih
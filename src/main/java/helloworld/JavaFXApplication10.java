package helloworld;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class JavaFXApplication10 extends Application {

    private Model model = new Model();

    @Override
    public void start(Stage primaryStage) {

        final TextField textField = new TextField();

        Bindings.bindBidirectional(textField.textProperty(), model.firstProperty());

        // Track the changes
        model.firstProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                System.out.println("model old val: " + arg1);
                System.out.println("model new val: " + arg2);
                System.out.println();
            }
        });

        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                System.out.println("textField old val: " + arg1);
                System.out.println("textField new val: " + arg2);
                System.out.println();
            }
        });

        Button btn = new Button();
        btn.setText("Change the model's text");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                model.setFirst("changed from button action");
                System.out.println("Done.");
            }
        });

        BorderPane root = new BorderPane();
        root.setTop(textField);
        root.setBottom(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Data Model
    public static class Model {

        StringProperty first = new SimpleStringProperty();
        //getter

        public String getFirst() {
            return first.get();
        }
        //setter

        public void setFirst(String first) {
            this.first.set(first);
        }
        //new "property" accessor

        public StringProperty firstProperty() {
            return first;
        }
    }
}
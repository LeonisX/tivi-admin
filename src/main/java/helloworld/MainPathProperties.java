package helloworld;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import unneeded.PathProperty;

public class MainPathProperties extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextField myTextField = new TextField("123");
        Person person = new Person();
        PathProperty<Person, String> prop = new PathProperty<>(person, "address", String.class);
// bind it to a JavaFX control
        Bindings.bindBidirectional(prop, myTextField.textProperty());

// apply value to the JavaFX property and verify the same value exists in the model
        prop.set("123 1st Street");
        System.out.println("Address Property: " + prop.get());
        System.out.println("Address POJO: " + person.getAddress());
// apply value to the POJO field and verify the same value exists in the JavaFX property
        person.setAddress("456 2nd Street");
        System.out.println("Address Property: " + prop.get());
        System.out.println("Address POJO: " + person.getAddress());
    }

    public static class Person {

        private String firstName;
        private String lastName;
        private String address;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

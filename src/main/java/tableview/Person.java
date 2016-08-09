package tableview;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {
  private StringProperty firstName;
  private StringProperty lastName;

  public Person(String firstName, String lastName) {
    setFirstName(firstName);
    setLastName(lastName);
  }

  public final void setFirstName(String value) { firstNameProperty().set(value); }
  public final void setLastName(String value) { lastNameProperty().set(value); }
  public String getFirstName() { return firstNameProperty().get(); }
  public String getLastName() { return lastNameProperty().get(); }

  public StringProperty firstNameProperty() {
    if (firstName == null) firstName = new SimpleStringProperty(this, "firstName");
    return firstName;
  }
  public StringProperty lastNameProperty() {
    if (lastName == null) lastName = new SimpleStringProperty(this, "lastName");
    return lastName;
  }
}
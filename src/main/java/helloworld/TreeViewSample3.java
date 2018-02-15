package helloworld;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class TreeViewSample3 extends Application {

    private final Node rootIcon =
            new ImageView(new Image(getClass().getResourceAsStream("root.png")));
    private final Image depIcon =
            new Image(getClass().getResourceAsStream("department.png"));
    private List<Employee> employees = Arrays.asList(
            new Employee("Ethan Williams", "Sales Department"),
            new Employee("Emma Jones", "Sales Department"),
            new Employee("Michael Brown", "Sales Department"),
            new Employee("Anna Black", "Sales Department"),
            new Employee("Rodger York", "Sales Department"),
            new Employee("Susan Collins", "Sales Department"),
            new Employee("Mike Graham", "IT Support"),
            new Employee("Judy Mayer", "IT Support"),
            new Employee("Gregory Smith", "IT Support"),
            new Employee("Jacob Smith", "Accounts Department"),
            new Employee("Isabella Johnson", "Accounts Department"));
    private TreeItem<String> rootNode =
            new TreeItem<>("MyCompany Human Resources", rootIcon);

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        rootNode.setExpanded(true);
        for (Employee employee : employees) {
            TreeItem<String> empLeaf = new TreeItem<>(employee.getName());
            boolean found = false;
            for (TreeItem<String> depNode : rootNode.getChildren()) {
                if (depNode.getValue().contentEquals(employee.getDepartment())){
                    depNode.getChildren().add(empLeaf);
                    found = true;
                    break;
                }
            }
            if (!found) {
                TreeItem<String> depNode = new TreeItem<>(employee.getDepartment(),
                        new ImageView(depIcon)
                );
                rootNode.getChildren().add(depNode);
                depNode.getChildren().add(empLeaf);
            }
        }

        stage.setTitle("Tree View Sample");
        VBox box = new VBox();
        final Scene scene = new Scene(box, 400, 300);
        scene.setFill(Color.LIGHTGRAY);

        TreeView<String> treeView = new TreeView<>(rootNode);
        treeView.setEditable(true);
        treeView.setCellFactory(p -> new TextFieldTreeCellImpl());

        box.getChildren().add(treeView);
        stage.setScene(scene);
        stage.show();
    }

    private final class TextFieldTreeCellImpl extends TreeCell<String> {

        private TextField textField;
        private ContextMenu addMenu = new ContextMenu();

        TextFieldTreeCellImpl() {
            MenuItem addMenuItem = new MenuItem("Add Employee");
            addMenu.getItems().add(addMenuItem);
            addMenuItem.setOnAction(new EventHandler() {
                public void handle(Event t) {
                    TreeItem<String> newEmployee =
                            new TreeItem<>("New Employee");
                    getTreeItem().getChildren().add(newEmployee);
                }
            });
        }

        @Override
        public void startEdit() {
            super.startEdit();

            if (textField == null) {
                createTextField();
            }
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(getItem());
            setGraphic(getTreeItem().getGraphic());
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(getTreeItem().getGraphic());
                    if (
                            !getTreeItem().isLeaf()&&getTreeItem().getParent()!= null
                            ){
                        setContextMenu(addMenu);
                    }
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setOnKeyReleased(t -> {
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(textField.getText());
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            });

        }

        private String getString() {
            return getItem() == null ? "" : getItem();
        }
    }

    public static class Employee {

        private final SimpleStringProperty name;
        private final SimpleStringProperty department;

        private Employee(String name, String department) {
            this.name = new SimpleStringProperty(name);
            this.department = new SimpleStringProperty(department);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String fName) {
            name.set(fName);
        }

        String getDepartment() {
            return department.get();
        }

        public void setDepartment(String fName) {
            department.set(fName);
        }
    }
}
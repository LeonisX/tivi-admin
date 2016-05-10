package md.leonis.tivi.admin.view.video;

import helloworld.MainApp;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.NumberStringConverter;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.utils.PathProperty;
import md.leonis.tivi.admin.utils.VideoUtils;

import java.util.*;

public class AddVideo2Controller {
    @FXML
    private TextField title;

    @FXML
    private TextField cpu;

    @FXML
    private TextField data;

    @FXML
    private ComboBox<String> category;

    @FXML
    private TextField description;

    @FXML
    private TextField keywords;

    @FXML
    private TextField year;

    @FXML
    private TextField originalUrl;

    @FXML
    private TextArea text;

    @FXML
    private TextArea fullText;

    @FXML
    private TextField author;

    @FXML
    private TextField authorSite;

    @FXML
    private TextField authorEmail;

    @FXML
    private TextField loads;

    @FXML
    private TextField views;

    @FXML
    private ToggleButton state;

    @FXML
    private Button nextButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Button reloadButton;

    @FXML
    private Button helpButton;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public AddVideo2Controller() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        //firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        //lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
    }

    @FXML
    private void processVideo() {
        mainApp.addVideo = new Video();
        //VideoUtils.parseUrl(urlTextField.getText(), mainApp.addVideo);
        VideoUtils.parsePage(mainApp.addVideo);
        mainApp.showProcessVideo();
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        PathProperty prop = new PathProperty(mainApp.addVideo, "title", String.class);
        PathProperty prop2 = new PathProperty(mainApp.addVideo, "cpu", String.class);
        PathProperty prop3 = new PathProperty(mainApp.addVideo, "year", Integer.class);
        PathProperty prop4 = new PathProperty(mainApp.addVideo, "category", String.class);
        Bindings.bindBidirectional(title.textProperty(), prop);
        Bindings.bindBidirectional(cpu.textProperty(), prop2);
        Bindings.bindBidirectional(year.textProperty(), prop3, new IntegerStringConverter());
        Bindings.bindBidirectional(category.valueProperty(), prop4);

        List<String> list = new ArrayList<>();
        ObservableList<String> oList = FXCollections.observableList(list);
        list.add("One");
        list.add("Two");
        list.add("Three");

        category.setItems(oList);
        //category.setValue("Выберите одну категорию");
        category.setPromptText("123");

        System.out.println(category.getValue());
        System.out.println(category.getSelectionModel().getSelectedIndex());
        System.out.println(category.getSelectionModel().getSelectedItem());

        // эксперименты с String, Integer, Combobox завершились успешно.
        // надо подумать что делать с датами, логическими значениями
        // по категориям - хранить именно строку и потом сопоставлять её с индексами из списка категорий



        //Bindings.bindBidirectional(title.textProperty(), mainApp.addVideo.title);
        //Bindings.bindBidirectional(cpu.textProperty(), mainApp.addVideo.cpu);

        // Add observable list data to the table
        //personTable.setItems(mainApp.getPersonData());
    }

    public void next() {
        System.out.println(mainApp.addVideo);
        System.out.println(category.getSelectionModel().getSelectedIndex());
        System.out.println(category.getSelectionModel().getSelectedItem());
    }

}
package md.leonis.tivi.admin.view.video;

import helloworld.MainApp;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.web.HTMLEditor;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import md.leonis.tivi.admin.model.Video;
import unneeded.PathProperty;
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
    private HTMLEditor textHtml;

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
        Video video = mainApp.addVideo;

        title.setText(video.title);
        cpu.setText(video.cpu);
        year.setText(video.year.toString());
        category.setValue(video.category.toString());

        textHtml.setHtmlText(mainApp.addVideo.text);

        List<String> list = new ArrayList<>();
        ObservableList<String> oList = FXCollections.observableList(list);
        list.add("One High");
        list.add("Two Low");
        list.add("Three");

        category.setItems(oList);
        //category.setValue("Выберите одну категорию");
        category.setPromptText("123");

        // TODO experiment
/*        category.setButtonCell(new ListCell(){

            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item==null){
                    // styled like -fx-prompt-text-fill:
                    setStyle("-fx-text-fill: derive(-fx-control-inner-background,-30%)");
                } else {
                    //setStyle("-fx-text-fill: -fx-text-inner-color");
                    System.out.println(item.toString());
                    if (item.toString().equals("Two")) setStyle("-fx-font-size: 32px");
                    setText(item.toString());
                }
            }

        });*/

        category.setCellFactory(
                new Callback<ListView<String>, ListCell<String>>() {
                    @Override public ListCell<String> call(ListView<String> param) {
                        final ListCell<String> cell = new ListCell<String>() {
                            {
                                super.setPrefWidth(100);
                            }
                            @Override public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item != null) {
                                    setText(item);
                                    if (item.contains("High")) {
                                        setTextFill(Color.RED);
                                    }
                                    else if (item.contains("Low")){
                                        setStyle("-fx-background-color: lavender;  -fx-margin-left: 10px;");
                                        setTextFill(Color.GREEN);
                                    }
                                    else {
                                        setTextFill(Color.BLACK);
                                    }
                                }
                                else {
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                });

        System.out.println(category.getValue());
        System.out.println(category.getSelectionModel().getSelectedIndex());
        System.out.println(category.getSelectionModel().getSelectedItem());

        // Add observable list data to the table
        //personTable.setItems(mainApp.getPersonData());
    }

    public void next() {
        System.out.println(mainApp.addVideo);
        System.out.println(category.getSelectionModel().getSelectedIndex());
        System.out.println(category.getSelectionModel().getSelectedItem());
    }

}
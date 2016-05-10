package md.leonis.tivi.admin.view.video;

import helloworld.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import md.leonis.tivi.admin.utils.VideoUtils;

public class AddVideoController {
    @FXML
    private Button nextButton;

    @FXML
    private TextField urlTextField;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public AddVideoController() {
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
        VideoUtils.parseUrl(urlTextField.getText(), mainApp.addVideo);
        VideoUtils.parsePage(mainApp.addVideo);
        mainApp.showProcessVideo();
    }

    public String getUrl() { return urlTextField.getText(); }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        //personTable.setItems(mainApp.getPersonData());
    }

}
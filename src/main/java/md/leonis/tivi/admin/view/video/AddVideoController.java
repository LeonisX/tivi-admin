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

    private MainApp mainApp;

    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.
        //firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        //lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
    }

    @FXML
    private void processVideo() {
        mainApp.parseVideoPage(urlTextField.getText());
        mainApp.showProcessVideo();
    }

    //public String getUrl() { return urlTextField.getText(); }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}
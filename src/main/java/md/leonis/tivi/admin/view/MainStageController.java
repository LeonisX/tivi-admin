package md.leonis.tivi.admin.view;

import helloworld.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Hyperlink;

public class MainStageController {
    @FXML
    private Accordion accordion;
    @FXML
    private Hyperlink settingsHyperlink;
    @FXML
    private Hyperlink allVideoHyperlink;
    @FXML
    private Hyperlink addVideoHyperlink;
    @FXML
    private Hyperlink categoriesHyperlink;
    @FXML
    private Hyperlink addCategoryHyperlink;
    @FXML
    private Hyperlink inaccesibleHyperlink;
    @FXML
    private Hyperlink tagsHyperlink;
    @FXML
    private Hyperlink commentsHyperlink;

    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public MainStageController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        accordion.setExpandedPane(accordion.getPanes().get(1));
        // Initialize the person table with the two columns.
        //firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        //lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
    }

    @FXML
    private void addVideo() {
        mainApp.showAddVideo();
    }

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
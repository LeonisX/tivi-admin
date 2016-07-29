package md.leonis.tivi.admin.view.video;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import md.leonis.tivi.admin.utils.CatUtils;
import md.leonis.tivi.admin.utils.SubPane;
import md.leonis.tivi.admin.utils.VideoUtils;

public class ListVideosController extends SubPane {
    @FXML
    private ComboBox<String> category;

    @FXML
    private Button categorySelectButton;

    @FXML
    private ToggleGroup countToggleGroup;

    private ToggleGroup pagesToggleGroup;

    @FXML
    private TableView videous;

    @FXML
    private ComboBox<String> operations;

    @FXML
    private Button runBatchOperationButton;

    private CatUtils cat;

    @FXML
    private void selectCategory() {
        VideoUtils.listVideousSettings.catId = category.getSelectionModel().getSelectedIndex();
        System.out.println(VideoUtils.listVideousSettings.catId);
    }

    @FXML
    private void selectPageCount() {
        Toggle toggle = countToggleGroup.getSelectedToggle();
        if (toggle != null) {
            int count = Integer.parseInt(((ToggleButton) toggle).getText());
            VideoUtils.listVideousSettings.count = count;
            //TODO reload videous list
        }
    }

    @FXML
    private void runBatchOperation() {
    }

    @FXML
    private void initialize() {
        CatUtils.setCellFactory(category);
    }

    @Override
    public void init() {
        cat = new CatUtils(VideoUtils.categories);
        category.setItems(FXCollections.observableList(cat.getCatList()));
        cat.setCategoryTextValue(category, VideoUtils.listVideousSettings.catId);
    }

    public void showPageButtons() {

    }


}
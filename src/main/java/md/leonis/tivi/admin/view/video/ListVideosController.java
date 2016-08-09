package md.leonis.tivi.admin.view.video;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.model.VideoView;
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
    public TableView<VideoView> videousTableView;

    @FXML
    private TableColumn<VideoView, Integer> idColumn;
    @FXML
    private TableColumn<VideoView, String> titleColumn;
    @FXML
    private TableColumn<VideoView, Long> publishedColumn;
    @FXML
    private TableColumn<VideoView, Integer> viewsColumn;
    @FXML
    private TableColumn<VideoView, Integer> commentsColumn;
    @FXML
    private TableColumn<VideoView, Boolean> v1Column;
    @FXML
    private TableColumn<VideoView, Boolean> checkedColumn;

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


        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        publishedColumn.setCellValueFactory(cellData -> cellData.getValue().publishedProperty().asObject());
        viewsColumn.setCellValueFactory(cellData -> cellData.getValue().viewsProperty().asObject());
        commentsColumn.setCellValueFactory(cellData -> cellData.getValue().commentsProperty().asObject());
        //v1Column.setCellValueFactory(cellData -> cellData.getValue().v1Property());
        checkedColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());

        v1Column.setCellFactory(new Callback<TableColumn<VideoView, Boolean>, TableCell<VideoView, Boolean>>() {
            @Override public TableCell<VideoView, Boolean> call(TableColumn<VideoView, Boolean> videoBooleanTableColumn) {
                return new AddPersonCell(videousTableView);
            }
        });
    }

    private class AddPersonCell extends TableCell<VideoView, Boolean> {
        // a button for adding a new person.
        final Button addButton       = new Button("Add");
        // pads and centers the add button in the cell.
        final StackPane paddedButton = new StackPane();
        // records the y pos of the last button press so that the add person dialog can be shown next to the cell.
        final DoubleProperty buttonY = new SimpleDoubleProperty();


        AddPersonCell(TableView table) {
            paddedButton.setPadding(new Insets(3));
            paddedButton.getChildren().add(addButton);
            addButton.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent mouseEvent) {
                    buttonY.set(mouseEvent.getScreenY());
                }
            });
            addButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent actionEvent) {
                    //showAddPersonDialog(stage, table, buttonY.get());
                    table.getSelectionModel().select(getTableRow().getIndex());
                    int index = getTableRow().getIndex();
                    System.out.println(index);
                }
            });
        }

        /** places an add button in the row only if the row is not empty. */
        @Override protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(paddedButton);
            } else {
                setGraphic(null);
            }
        }
    }


    @Override
    public void init() {
        cat = new CatUtils(VideoUtils.categories);
        category.setItems(FXCollections.observableList(cat.getCatList()));
        cat.setCategoryTextValue(category, VideoUtils.listVideousSettings.catId);
        fillFields();
    }

    private void fillFields() {
        if (VideoUtils.videous != null) {
            videousTableView.setItems(FXCollections.observableList(VideoUtils.videous));
        }
    }


}
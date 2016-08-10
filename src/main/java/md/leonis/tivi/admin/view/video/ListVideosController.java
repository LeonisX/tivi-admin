package md.leonis.tivi.admin.view.video;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import md.leonis.tivi.admin.model.VideoView;
import md.leonis.tivi.admin.utils.CatUtils;
import md.leonis.tivi.admin.utils.SubPane;
import md.leonis.tivi.admin.utils.VideoUtils;


public class ListVideosController extends SubPane {
    @FXML
    private ComboBox<String> category;

    @FXML
    private ToggleGroup countToggleGroup;

    private ToggleGroup pagesToggleGroup = new ToggleGroup();

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

    @FXML
    private HBox pagesHBox;

    private CatUtils cat;

    private ChangeListener pagesChangeListener;

    @FXML
    private void selectCategory() {
        int index = category.getSelectionModel().getSelectedIndex();
        VideoUtils.listVideousSettings.catId = index;
        VideoUtils.countVideos();
        if (index != -1) {
            VideoUtils.listVideousSettings.catId = cat.getCatIds().get(index);
        }
        fillFields();
    }

    @FXML
    private void selectPageCount() {
        Toggle toggle = countToggleGroup.getSelectedToggle();
        if (toggle != null) {
            VideoUtils.listVideousSettings.count = Integer.parseInt(((ToggleButton) toggle).getText());
            fillFields();
        }
    }

    @FXML
    private void runBatchOperation() {
    }

    @FXML
    private void initialize() {
        CatUtils.setCellFactory(category);
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        idColumn.setStyle("-fx-alignment: CENTER;");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        titleColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        publishedColumn.setCellValueFactory(cellData -> cellData.getValue().publishedProperty().asObject());
        publishedColumn.setStyle("-fx-alignment: CENTER;");
        viewsColumn.setCellValueFactory(cellData -> cellData.getValue().viewsProperty().asObject());
        viewsColumn.setGraphic(new ImageView(new Image("view.png")));
        viewsColumn.setStyle("-fx-alignment: CENTER;");
        commentsColumn.setCellValueFactory(cellData -> cellData.getValue().commentsProperty().asObject());
        commentsColumn.setGraphic(new ImageView(new Image("comment.png")));
        commentsColumn.setStyle("-fx-alignment: CENTER;");
        //checkedColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
        //checkedColumn.setStyle("-fx-alignment: CENTER;");

        checkedColumn.setCellValueFactory((TableColumn.CellDataFeatures<VideoView, Boolean> param) -> param.getValue().checkedProperty());
        checkedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkedColumn));
        checkedColumn.setGraphic(new ImageView(new Image("select.png")));

        v1Column.setCellFactory((TableColumn<VideoView, Boolean> videoBooleanTableColumn) -> new ButtonsCell(videousTableView));

        CatUtils.setCellFactory(category);

        pagesChangeListener = new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                if (old_toggle != null && !old_toggle.getUserData().equals(new_toggle.getUserData())) {
                    VideoUtils.listVideousSettings.page = Integer.parseInt(new_toggle.getUserData().toString());
                    fillFields();
                }
            }
        };
        pagesToggleGroup.selectedToggleProperty().addListener(pagesChangeListener);
    }

    private enum Reaction {VIEW, EDIT, DELETE}

    private class ButtonsCell extends TableCell<VideoView, Boolean> {
        final Button viewButton = new Button("", new ImageView(new Image("view2.png")));
        final Button editButton = new Button("", new ImageView(new Image("edit.gif")));
        final Button deleteButton = new Button("", new ImageView(new Image("del.gif")));
        final HBox box = new HBox();

        ButtonsCell(TableView table) {
            box.setSpacing(5);
            box.getChildren().addAll(viewButton, editButton, deleteButton);

            viewButton.setOnAction((ActionEvent actionEvent) -> action(table, Reaction.VIEW));
            editButton.setOnAction((ActionEvent actionEvent) -> action(table, Reaction.EDIT));
            deleteButton.setOnAction((ActionEvent actionEvent) -> action(table, Reaction.DELETE));
        }

        private void action(TableView table, Reaction reaction) {
            table.getSelectionModel().select(getTableRow().getIndex());
            int index = getTableRow().getIndex();
            //TODO reaction on click
            switch (reaction) {
                case VIEW:
                    break;
                case EDIT:
                    break;
                case DELETE:
                    break;
            }
            System.out.println(reaction + " " + VideoUtils.videous.get(index).id.get());
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setGraphic(box);
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
        //TODO sort for query
        ObservableList<TableColumn<VideoView,?>> order = videousTableView.getSortOrder();
        pagesToggleGroup.selectedToggleProperty().removeListener(pagesChangeListener);
        pagesHBox.getChildren().removeAll(pagesHBox.getChildren());
        int begin = 1;
        int end = 1;
        if (VideoUtils.videousCount > 1) {
            end = VideoUtils.videousCount / VideoUtils.listVideousSettings.count + 1;
        }
        if (VideoUtils.listVideousSettings.page > end) VideoUtils.listVideousSettings.page = end;
        if (begin + 4 < VideoUtils.listVideousSettings.page) begin = VideoUtils.listVideousSettings.page - 4;
        if (end - 4 > VideoUtils.listVideousSettings.page) end = VideoUtils.listVideousSettings.page + 4;

        VideoUtils.listVideos();
        videousTableView.setItems(FXCollections.observableList(VideoUtils.videous));

        Button firstPageButton = new Button("#");
        Button lastPageButton = new Button("#");
        Button prevPageButton = new Button("<<");
        Button nextPageButton = new Button(">>");

        pagesHBox.getChildren().add(firstPageButton);
        pagesHBox.getChildren().add(prevPageButton);

        for (int i = begin; i <= end; i++) {
            ToggleButton pageButton = new ToggleButton(Integer.toString(i));
            pageButton.setUserData(pageButton.getText());
            pageButton.setToggleGroup(pagesToggleGroup);
            if (VideoUtils.listVideousSettings.page == i) pageButton.setSelected(true);
            pagesHBox.getChildren().add(pageButton);
        }

        pagesHBox.getChildren().add(nextPageButton);
        pagesHBox.getChildren().add(lastPageButton);
        pagesToggleGroup.selectedToggleProperty().addListener(pagesChangeListener);
    }
}
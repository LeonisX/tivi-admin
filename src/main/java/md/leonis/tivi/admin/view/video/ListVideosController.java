package md.leonis.tivi.admin.view.video;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import md.leonis.tivi.admin.model.MixedTitle;
import md.leonis.tivi.admin.model.VideoView;
import md.leonis.tivi.admin.utils.CatUtils;
import md.leonis.tivi.admin.utils.SubPane;
import md.leonis.tivi.admin.utils.VideoUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static md.leonis.tivi.admin.utils.VideoUtils.listVideousSettings;


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
    //private TableColumn<VideoView, String> titleColumn;
    private TableColumn<VideoView, MixedTitle> titleColumn;
    @FXML
    private TableColumn<VideoView, String> publishedColumn;
    @FXML
    private TableColumn<VideoView, Integer> viewsColumn;
    @FXML
    private TableColumn<VideoView, Integer> commentsColumn;
    @FXML
    private TableColumn<VideoView, String> ratingColumn;
    @FXML
    private TableColumn<VideoView, Boolean> v1Column;
    @FXML
    private TableColumn<VideoView, Boolean> checkedColumn;

    @FXML
    private ComboBox<String> sort;

    @FXML
    private CheckBox order;

    @FXML
    private ComboBox<String> operations;

    @FXML
    private Button runBatchOperationButton;

    @FXML
    private HBox pagesHBox;

    private CatUtils cat;

    private ChangeListener<Toggle> pagesChangeListener;

    @FXML
    private void selectCategory() {
        int index = category.getSelectionModel().getSelectedIndex();
        listVideousSettings.catId = index;
        VideoUtils.countVideos();
        if (index != -1) {
            listVideousSettings.catId = cat.getCatIds().get(index);
        }
        fillFields();
    }

    private ObservableList<String> sortOptions =
            FXCollections.observableArrayList(
                    "ID", "Категория", "Дата публикации", "Название", "Картинка", "Количество просмотров",
                    "Количество комментариев", "Рейтинг"
            );

    private List<String> sortFields = Arrays.asList(
            "downid", "catid", "public", "title", "image", "hits", "comments", "rate"
    );

    @FXML
    private void selectSortOrder() {

        int index = sort.getSelectionModel().getSelectedIndex();
        System.out.println(sortFields.get(index));
        listVideousSettings.sort = sortFields.get(index);
        fillFields();
    }

    @FXML
    private void selectOrder() {
        if (order.isSelected()) listVideousSettings.order = "desc";
        else listVideousSettings.order = "asc";
        fillFields();
    }

    @FXML
    private void selectPageCount() {
        Toggle toggle = countToggleGroup.getSelectedToggle();
        if (toggle != null) {
            listVideousSettings.count = Integer.parseInt(((ToggleButton) toggle).getText());
            fillFields();
        }
    }

    @FXML
    private void runBatchOperation() {
    }

    @FXML
    private void initialize() {
        System.out.println("initialize()");
        //CatUtils.setCellFactory(category);
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        idColumn.setStyle("-fx-alignment: CENTER;");
        //titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().mixedTitleProperty());
        titleColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        titleColumn.setCellValueFactory((TableColumn.CellDataFeatures<VideoView, MixedTitle> param) -> param.getValue().mixedTitleProperty());
        titleColumn.setCellFactory((TableColumn<VideoView, MixedTitle> mixedTitleTableColumn) -> new MixedTitleCell());

        publishedColumn.setCellValueFactory(cellData -> cellData.getValue().publishedProperty());
        publishedColumn.setStyle("-fx-alignment: CENTER;");
        viewsColumn.setCellValueFactory(cellData -> cellData.getValue().viewsProperty().asObject());
        viewsColumn.setGraphic(new ImageView(new Image("view.png")));
        viewsColumn.setStyle("-fx-alignment: CENTER;");
        commentsColumn.setCellValueFactory(cellData -> cellData.getValue().commentsProperty().asObject());
        commentsColumn.setGraphic(new ImageView(new Image("comment.png")));
        commentsColumn.setStyle("-fx-alignment: CENTER;");
        ratingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty());
        ratingColumn.setGraphic(new ImageView(new Image("rating.png")));
        ratingColumn.setStyle("-fx-alignment: CENTER;");
        //checkedColumn.setCellValueFactory(cellData -> cellData.getValue().checkedProperty());
        //checkedColumn.setStyle("-fx-alignment: CENTER;");

        checkedColumn.setCellValueFactory((TableColumn.CellDataFeatures<VideoView, Boolean> param) -> param.getValue().checkedProperty());
        checkedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkedColumn));
        checkedColumn.setGraphic(new ImageView(new Image("select.png")));

        v1Column.setCellFactory((TableColumn<VideoView, Boolean> videoBooleanTableColumn) -> new ButtonsCell(videousTableView));

        CatUtils.setCellFactory(category);

        pagesChangeListener = (ov, old_toggle, new_toggle) -> {
            if (old_toggle != null && !old_toggle.getUserData().equals(new_toggle.getUserData())) {
                listVideousSettings.page = Integer.parseInt(new_toggle.getUserData().toString());
                fillFields();
            }
        };
        pagesToggleGroup.selectedToggleProperty().addListener(pagesChangeListener);

        sort.setItems(sortOptions);
        sort.setValue(sort.getItems().get(0));

    }


    private class MixedTitleCell extends TableCell<VideoView, MixedTitle> {
        final Label title = new Label();
        final Label category = new Label();
        final VBox box = new VBox();

        MixedTitleCell() {
            //box.setSpacing(5);
            box.getChildren().addAll(category, title);
        }

        @Override
        protected void updateItem(MixedTitle item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty) {
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                title.setText(item.getTitle());
                category.setText(item.getCategory());
                category.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
                setGraphic(box);
            } else {
                setGraphic(null);
            }
        }
    }

    private enum Reaction {VIEW, EDIT, DELETE}

    private class ButtonsCell extends TableCell<VideoView, Boolean> {
        int size = 30;
        List<String> icons = Arrays.asList("view2.png", "edit.gif", "del.gif");
        List<Button> buttons = icons.stream().map(icon -> new Button("", new ImageView(new Image(icon)))).collect(Collectors.toList());
        final HBox box = new HBox();

        ButtonsCell(TableView table) {
            box.setSpacing(5);
            box.setAlignment(Pos.CENTER);
            box.getChildren().addAll(buttons);

            IntStream.range(0, buttons.size())
                    .forEach(idx ->
                            {
                                Button button = buttons.get(idx);
                                button.setMinSize(size, size);
                                button.setMaxSize(size, size);
                                button.setOnAction((ActionEvent actionEvent) -> action(table, Reaction.values()[idx]));
                            }
                    );
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
        System.out.println("init()");
        cat = new CatUtils(VideoUtils.categories);
        category.setItems(FXCollections.observableList(cat.getCatList()));
        cat.setCategoryTextValue(category, listVideousSettings.catId);
        fillFields();
    }

    private void fillFields() {
        //TODO sort for query

        pagesToggleGroup.selectedToggleProperty().removeListener(pagesChangeListener);
        pagesHBox.getChildren().removeAll(pagesHBox.getChildren());
        int begin = 1;
        int end = 1;
        if (VideoUtils.videousCount > 1) {
            end = VideoUtils.videousCount / listVideousSettings.count + 1;
        }
        final int pages = end;
        if (listVideousSettings.page > end) listVideousSettings.page = end;
        if (begin + 4 < listVideousSettings.page) begin = listVideousSettings.page - 4;
        if (end - 4 > listVideousSettings.page) end = listVideousSettings.page + 4;

        VideoUtils.listVideos();
        videousTableView.setItems(FXCollections.observableList(VideoUtils.videous));

        Button firstPageButton = new Button("#");
        firstPageButton.setOnAction((ActionEvent e) -> {
            listVideousSettings.page = 1;
            fillFields();
        });
        Button lastPageButton = new Button("#");
        lastPageButton.setOnAction((ActionEvent e) -> {
            listVideousSettings.page = pages;
            fillFields();
        });
        Button prevPageButton = new Button("<");
        prevPageButton.setOnAction((ActionEvent e) -> {
            if (listVideousSettings.page != 1) listVideousSettings.page--;
            fillFields();
        });
        Button nextPageButton = new Button(">");
        nextPageButton.setOnAction((ActionEvent e) -> {
            if (listVideousSettings.page < pages) listVideousSettings.page++;
            fillFields();
        });

        pagesHBox.getChildren().add(firstPageButton);
        pagesHBox.getChildren().add(prevPageButton);

        for (int i = begin; i <= end; i++) {
            ToggleButton pageButton = new ToggleButton(Integer.toString(i));
            pageButton.setUserData(pageButton.getText());
            pageButton.setToggleGroup(pagesToggleGroup);
            if (listVideousSettings.page == i) pageButton.setSelected(true);
            pagesHBox.getChildren().add(pageButton);
        }

        pagesHBox.getChildren().add(nextPageButton);
        pagesHBox.getChildren().add(lastPageButton);
        pagesToggleGroup.selectedToggleProperty().addListener(pagesChangeListener);
    }
}
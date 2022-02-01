package md.leonis.tivi.admin.view.media;

import com.github.junrar.exception.RarException;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.Pair;
import javafx.util.StringConverter;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.ComparisionResult;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.model.View;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.CalibreUtils;
import md.leonis.tivi.admin.utils.Config;
import md.leonis.tivi.admin.utils.SubPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.BookUtils.*;

public class SiteCompareController extends SubPane {

    @FXML
    public GridPane gridPane;
    public TextField calibreDir;
    public Label calibreTotals;
    public Label siteTotals;
    public ComboBox<BookCategory> categoryComboBox;
    public TreeTableView<View> treeTableView;
    public TextField cloudStorageLink;
    public Label categoriesTotals;

    @FXML
    private void initialize() {
        reloadCalibreData();
        reloadSiteData();
        reloadCategories();

        setupCategoryComboBox();
        calibreDir.setText(Config.calibreDbPath);
        System.out.println("initialize()");
    }

    @Override
    public void init() {
        System.out.println("init()");
    }

    public void selectCalibreDir() {
        DirectoryChooser directoryChooser = getDirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);
        Config.workPath = selectedDirectory.getAbsolutePath();
        calibreDir.setText(Config.workPath);
        reloadCalibreData();
    }

    public void reloadSiteData() {
        BookUtils.getSiteBooks();
        siteTotals.setText("" + siteBooks.size());
    }

    public void reloadCalibreData() {
        calibreBooks = CalibreUtils.readBooks();
        calibreTotals.setText("" + calibreBooks.size());
    }

    public void compare() throws IOException {
        setupTreeTableView();
        BookUtils.cloudStorageLink = cloudStorageLink.getText();
        fillTreeTableView(BookUtils.compare(categoryComboBox.getValue().getCatcpu()));
    }

    public void generate() throws IOException {
        BookUtils.cloudStorageLink = cloudStorageLink.getText();
        ComparisionResult<Video> comparisionResult = BookUtils.compare(categoryComboBox.getValue().getCatcpu());
        BookUtils.syncDataWithSite(comparisionResult, calibreDir.getText(), categoryComboBox.getValue().getCatcpu());
        reloadSiteData();
    }

    public void dumpCalibreDB() {
        CalibreUtils.dumpDB();
    }

    public void dumpSiteDB() throws FileNotFoundException {
        BookUtils.dumpDB();
    }

    public void dumpImages() throws IOException {
        CalibreUtils.dumpImages();
    }

    public void dumpBooks() throws IOException, RarException {
        CalibreUtils.dumpBooks();
    }

    public void reloadCategories() {
        //reloadSiteData();
        BookUtils.readCategories();
        categoriesTotals.setText(BookUtils.categories.size() + "");
        System.out.println("Reloaded");
    }

    public void recalcCategories() {
        //reloadSiteData();
        List<Pair<BookCategory, BookCategory>> comparisionResult = BookUtils.compareCategories();
        setupTreeTableView();
        fillTreeTableView(comparisionResult);
    }

    public void updateCategories() {
        BookUtils.compareCategories().forEach(b -> BookUtils.updateCategoryTotals(b.getValue()));
        reloadCategories();
    }

    public void onSelectCategory() {
        //TODO ??
    }

    public void uploadImages() {
        //TODO
    }

    public void uploadBooks() {
        //TODO
    }


    private void setupCategoryComboBox() {
        List<BookCategory> observableCategories = new ArrayList<>(categories);
        observableCategories.add(0, new BookCategory());
        ObservableList<BookCategory> options = FXCollections.observableArrayList(observableCategories);
        categoryComboBox.setItems(options);
        categoryComboBox.setValue(observableCategories.get(0));
        categoryComboBox.setVisibleRowCount(24);

        categoryComboBox.setCellFactory(new Callback<ListView<BookCategory>, ListCell<BookCategory>>() {
            @Override
            public ListCell<BookCategory> call(ListView<BookCategory> p) {
                return new ListCell<BookCategory>() {
                    @Override
                    protected void updateItem(BookCategory item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getCatcpu());
                        }
                    }
                };
            }
        });

        categoryComboBox.setConverter(new StringConverter<BookCategory>() {
            @Override
            public String toString(BookCategory category) {
                return category == null ? null : category.getCatcpu();
            }
            @Override
            public BookCategory fromString(String catName) {
                return null;
            }
        });
    }

    private DirectoryChooser getDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Config.workPath));
        directoryChooser.setTitle("Select directory with Calibre DB");
        return directoryChooser;
    }

    @SuppressWarnings("unchecked")
    private void setupTreeTableView() {
        TreeTableColumn<View, String> titleCol = new TreeTableColumn<>("Title");
        TreeTableColumn<View, String> leftCol = new TreeTableColumn<>("Left");
        TreeTableColumn<View, String> rightCol = new TreeTableColumn<>("Right");
        titleCol.setPrefWidth(250);
        leftCol.setPrefWidth(400);
        leftCol.setEditable(true);
        rightCol.setEditable(true);
        rightCol.setPrefWidth(400);
        titleCol.setMaxWidth(500);
        leftCol.setMaxWidth(900);
        rightCol.setMaxWidth(900);

        titleCol.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue().getTitle()));
        leftCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("leftValue"));
        rightCol.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().getValue().getRightValue()));

        treeTableView.setEditable(true);
        TreeTableView.TreeTableViewSelectionModel<View> selection = treeTableView.getSelectionModel();
        selection.setSelectionMode(SelectionMode.MULTIPLE);

        selection.setCellSelectionEnabled(true);

        //leftCol.setEditable(true);
        //rightCol.setEditable(true);

        leftCol.setCellFactory(item -> {
            TreeTableCell<View, String> treeCell = new TreeTableCell<View, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty)
                        setText(item);
                    else
                        setText("");
                }
            };
            //TODO generic
            treeCell.addEventFilter(MouseEvent.MOUSE_CLICKED, t -> {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(((TreeTableCell) t.getSource()).getText());
                clipboard.setContent(content);
            });
            treeCell.setEditable(true);
            treeCell.prefWidthProperty().bind(leftCol.widthProperty());
            //treeCell.setTextOverrun(OverrunStyle.CLIP);
            return treeCell;
        });

        rightCol.setCellFactory(item -> {
            TreeTableCell<View, String> treeCell = new TreeTableCell<View, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && !empty)
                        setText(item);
                    else
                        setText("");
                }
            };
            treeCell.addEventFilter(MouseEvent.MOUSE_CLICKED, t -> {
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(((TreeTableCell) t.getSource()).getText());
                clipboard.setContent(content);
            });
            treeCell.setEditable(true);
            //treeCell.setTextOverrun(OverrunStyle.CLIP);
            treeCell.prefWidthProperty().bind(leftCol.widthProperty());
            return treeCell;
        });

        //treeTableView.setColumnResizePolicy(p -> true);

        /*treeTableView.setRowFactory(treeTable -> {
            TreeTableRow<View> row = new TreeTableRow<>();
            row.setMaxHeight(1500);
            row.setPrefWidth(500);
            //row.setMinHeight(500);
            row.prefHeightProperty().bind(leftCol.widthProperty());
            return row ;
        });*/

        treeTableView.getColumns().setAll(titleCol, leftCol, rightCol);
    }

    @SuppressWarnings("unchecked")
    private void fillTreeTableView(ComparisionResult<Video> comparisionResult) {
        TreeItem<View> addedItem = new TreeItem<>(new View("Added"));
        TreeItem<View> deletedItem = new TreeItem<>(new View("Deleted"));
        TreeItem<View> changedItem = new TreeItem<>(new View("Changed"));

        addedItem.setExpanded(true);
        deletedItem.setExpanded(true);
        changedItem.setExpanded(true);

        TreeItem<View> rootItem = new TreeItem<>(new View("R00T"));
        rootItem.getChildren().addAll(addedItem, deletedItem, changedItem);

        addedItem.getChildren().addAll(comparisionResult.getAddedBooks().stream().map(b -> {
            TreeItem<View> treeItem = new TreeItem<>(new View(b.getTitle())); //TODO may be add more properties
            treeItem.setExpanded(true);
            return treeItem;
        }).collect(toList()));

        deletedItem.getChildren().addAll(comparisionResult.getDeletedBooks().stream().map(b -> {
            TreeItem<View> treeItem = new TreeItem<>(new View(b.getTitle())); //TODO may be add more properties
            treeItem.setExpanded(true);
            return treeItem;
        }).collect(toList()));

        changedItem.getChildren().addAll(comparisionResult.getChangedBooks().entrySet().stream().map(b -> {
            TreeItem<View> treeItem = new TreeItem<>(new View(b.getKey().getTitle()));
            treeItem.setExpanded(true);
            b.getValue().forEach(c -> treeItem.getChildren().add(new TreeItem<>(
                    new View(c.getKey(), c.getValue().getKey(), c.getValue().getValue())
            )));
            return treeItem;
        }).collect(toList()));

        treeTableView.setRoot(rootItem);
        //treeTableView.setShowRoot(false);
        rootItem.setExpanded(true);
        //treeTableView.setEditable(true);
    }

    private void fillTreeTableView(List<Pair<BookCategory, BookCategory>> comparisionResult) {
        TreeItem<View> changedItem = new TreeItem<>(new View("Changed"));

        changedItem.setExpanded(true);

        TreeItem<View> rootItem = new TreeItem<>(new View("R00T"));
        rootItem.getChildren().add(changedItem);

        changedItem.getChildren().addAll(comparisionResult.stream()
                .map(b -> new TreeItem<>(new View(b.getKey().getCatname(), b.getKey().getTotal() + "", b.getValue().getTotal() + ""))).collect(toList()));

        treeTableView.setRoot(rootItem);
        //treeTableView.setShowRoot(false);
        rootItem.setExpanded(true);
        //treeTableView.setEditable(true);
    }

    /*private static class CategoryComboBoxCellFactory implements Callback<ListView<BookCategory>, ListCell<BookCategory>> {
        @Override
        public ListCell<BookCategory> call(ListView<BookCategory> listView) {
            return new ListCell<BookCategory>() {
                @Override
                protected void updateItem(BookCategory item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        setText(item.getCatcpu());
                    }
                }
            };
        }
    }

    private static class CategoryComboBoxConverter extends StringConverter<BookCategory> {

        @Override
        public String toString(BookCategory category) {
            return category == null ? null : category.getCatcpu();
        }

        @Override
        public BookCategory fromString(String catName) {
            return null;
        }
    }*/
}

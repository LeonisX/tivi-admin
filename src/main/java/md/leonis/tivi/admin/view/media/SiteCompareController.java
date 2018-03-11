package md.leonis.tivi.admin.view.media;

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
import javafx.util.StringConverter;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.ComparisionResult;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.model.View;
import md.leonis.tivi.admin.model.media.CalibreBook;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.CalibreUtils;
import md.leonis.tivi.admin.utils.Config;
import md.leonis.tivi.admin.utils.SubPane;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class SiteCompareController extends SubPane {

    @FXML
    public GridPane gridPane;
    public TextField calibreDir;
    public Label calibreTotals;
    public Label siteTotals;
    public ComboBox<BookCategory> categoryCombobox;
    public TreeTableView<View> treeTableView;

    //TODO move all lists and code to to BookUtils
    private List<Video> siteBooks;

    private List<CalibreBook> allCalibreBooks;

    private List<BookCategory> categories;

    @FXML
    private void initialize() {
        calibreDir.setText(Config.calibreDbPath);
        categories = BookUtils.readCategories().stream().sorted(Comparator.comparing(BookCategory::getCatcpu)).collect(toList());
        reloadCalibreData();
        reloadSiteData();

        ObservableList<BookCategory> options = FXCollections.observableArrayList(categories);
        categoryCombobox.setItems(options);
        categoryCombobox.setCellFactory(new Callback<ListView<BookCategory>, ListCell<BookCategory>>() {
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
        categoryCombobox.setConverter(new StringConverter<BookCategory>() {
            @Override
            public String toString(BookCategory category) {
                if (category == null){
                    return null;
                } else {
                    return category.getCatcpu();
                }
            }
            @Override
            public BookCategory fromString(String catName) {
                return null;
            }
        });
        categoryCombobox.setValue(categories.stream().filter(c -> c.getCatcpu().equals("magazines")).findFirst().get());

        System.out.println("initialize()");
    }


    @Override
    public void init() {
        System.out.println("init()");
    }


    public void selectCalibreDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Config.workPath));
        directoryChooser.setTitle("Select directory with Calibre DB");
        File selectedDirectory = directoryChooser.showDialog(null);
        calibreDir.setText(selectedDirectory.getAbsolutePath());
        reloadCalibreData();
    }

    public void compare() {
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

        ComparisionResult<Video> comparisionResult = BookUtils.compare(allCalibreBooks, siteBooks, categories, categoryCombobox.getValue().getCatcpu());
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

    public void reloadSiteData() {
        siteBooks = BookUtils.getAllBooks();
        siteTotals.setText("" + siteBooks.size());
    }

    public void reloadCalibreData() {
        String configUrl = Config.sqliteUrl;
        Config.sqliteUrl = BookUtils.getJdbcString(calibreDir.getText());
        allCalibreBooks = CalibreUtils.readBooks();
        Config.sqliteUrl = configUrl;
        calibreTotals.setText("" + allCalibreBooks.size());
    }

    public void generate() {
        if (allCalibreBooks == null) {
            reloadCalibreData();
        }
        if (siteBooks == null) {
            reloadSiteData();
        }
        ComparisionResult<Video> comparisionResult = BookUtils.compare(allCalibreBooks, siteBooks, categories, categoryCombobox.getValue().getCatcpu());

        BookUtils.syncDataWithSite(comparisionResult, allCalibreBooks, calibreDir.getText(), categoryCombobox.getValue().getCatcpu());
        reloadSiteData();
    }

    public void dumpCalibreDB() {
        CalibreUtils.dumpDB();
    }

    public void dumpSiteDB() throws FileNotFoundException {
        BookUtils.dumpDB();
    }

    public void onSelectCategory() {
        //TODO ??
    }

    public void dumpImages() throws IOException {
        CalibreUtils.dumpImages();
    }

    public void dumpBooks() throws IOException {
        CalibreUtils.dumpBooks();
    }

    public void uploadImages() {
        //TODO
    }

    public void uploadBooks() {
        //TODO
    }

}

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
import javafx.util.Pair;
import javafx.util.StringConverter;
import md.leonis.tivi.admin.model.ComparisionResult;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.danneo.BookCategory;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.model.gui.View;
import md.leonis.tivi.admin.renderer.ForumGuidesRenderer;
import md.leonis.tivi.admin.utils.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.BookUtils.siteBooks;

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
    public CheckBox reloadSiteCheckBox;

    private List<CalibreBook> calibreBooks = new ArrayList<>();

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
        Config.outputPath = selectedDirectory.getAbsolutePath();
        calibreDir.setText(Config.outputPath);
        reloadCalibreData();
    }

    public void reloadSiteData() {
        BookUtils.getSiteBooks();
        siteTotals.setText("" + siteBooks.size());
    }

    public void reloadCalibreData() {
        calibreBooks = new CalibreUtils().readBooks();
        calibreTotals.setText("" + calibreBooks.size());
    }

    public void reloadBooks() {
        if (reloadSiteCheckBox.isSelected()) {
            reloadSiteData();
            reloadCalibreData();
        }
    }

    public void compare() {
        setupTreeTableView();
        BookUtils.cloudStorageLink = cloudStorageLink.getText();
        SiteRenderer.cloudStorageLink = cloudStorageLink.getText();
        fillTreeTableView(BookUtils.compare(calibreBooks, categoryComboBox.getValue().getCatcpu()));
    }

    public void updateOnSite() throws IOException {
        BookUtils.cloudStorageLink = cloudStorageLink.getText();
        SiteRenderer.cloudStorageLink = cloudStorageLink.getText();
        List<ComparisionResult<Video>> comparisionResults = BookUtils.compare(calibreBooks, categoryComboBox.getValue().getCatcpu());
        List<String> sql = BookUtils.syncDataWithSite(comparisionResults, true);
        BookUtils.loadTiviIds(calibreBooks, comparisionResults, calibreDir.getText() + Config.calibreDbName);

        String fileName = comparisionResults.size() <= 1 ? String.format("update-queries-%s.sql", categoryComboBox.getValue().getCatcpu()) : "update-queries.sql";
        Path path = Paths.get(Config.outputPath).resolve(fileName);
        FileUtils.backupFile(path);
        Files.write(path, sql);

        reloadBooks();
    }

    public void generateForumGuides() throws IOException {
        SiteRenderer.cloudStorageLink = cloudStorageLink.getText();
        new ForumGuidesRenderer(calibreBooks).generateForumGuides();
    }

    public void dumpCalibreDB() {
        CalibreUtils.dumpDB();
    }

    public void dumpSiteDB() throws FileNotFoundException {
        SiteDbUtils.dumpDB();
    }

    public void dumpImages() {
        new CalibreUtils().dumpImages();
    }

    public void dumpBooks() throws IOException {
        new CalibreUtils().dumpBooks();
    }

    public void reloadCategories() {
        //reloadSiteData();
        BookUtils.loadCategories();
        categoriesTotals.setText(BookUtils.getCategories().size() + "");
        System.out.println("Reloaded");
    }

    public void recalcCategories() {
        //reloadSiteData();
        List<Pair<BookCategory, BookCategory>> comparisionResult = BookUtils.compareCategories();
        setupTreeTableView();
        fillTreeTableViewCategories(comparisionResult);
    }

    public void updateCategories() {
        BookUtils.compareCategories().forEach(b -> SiteDbUtils.updateCategoryTotals(b.getValue()));
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
        List<BookCategory> observableCategories = new ArrayList<>(BookUtils.getCategories());
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
        directoryChooser.setInitialDirectory(new File(Config.outputPath));
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
                content.putString(((TreeTableCell<View, String>) t.getSource()).getText());
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
                content.putString(((TreeTableCell<View, String>) t.getSource()).getText());
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
    private void fillTreeTableView(List<ComparisionResult<Video>> comparisionResults) {
        TreeItem<View> addedItem = new TreeItem<>(new View("Added"));
        TreeItem<View> deletedItem = new TreeItem<>(new View("Deleted"));
        TreeItem<View> changedItem = new TreeItem<>(new View("Changed"));

        addedItem.setExpanded(true);
        deletedItem.setExpanded(true);
        changedItem.setExpanded(true);

        TreeItem<View> rootItem = new TreeItem<>(new View("R00T"));
        rootItem.getChildren().addAll(addedItem, deletedItem, changedItem);

        addedItem.getChildren().addAll(comparisionResults.stream().flatMap(c -> c.getAddedBooks().stream()).map(b -> {
            TreeItem<View> treeItem = new TreeItem<>(new View(groupTitle(b))); //TODO may be add more properties
            treeItem.setExpanded(true);
            return treeItem;
        }).collect(toList()));

        deletedItem.getChildren().addAll(comparisionResults.stream().flatMap(c -> c.getDeletedBooks().stream()).map(b -> {
            TreeItem<View> treeItem = new TreeItem<>(new View(groupTitle(b))); //TODO may be add more properties
            treeItem.setExpanded(true);
            return treeItem;
        }).collect(toList()));

        changedItem.getChildren().addAll(comparisionResults.stream().flatMap(c -> c.getChangedBooks().entrySet().stream()).map(b -> {
            TreeItem<View> treeItem = new TreeItem<>(new View(groupTitle(b.getKey())));
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

    // Добавить платформу к названию: # Книги в розыске (Arcade)
    private String groupTitle(Video book) {
        String title = book.getTitle();
        if (title.startsWith("#") && book.getCpu().endsWith("_search")) {
            title = String.format("%s (%s)", title, BookUtils.getCategoryName(book.getCpu().split("_")[0]));
        }
        return title;
    }

    private void fillTreeTableViewCategories(List<Pair<BookCategory, BookCategory>> comparisionResult) {
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

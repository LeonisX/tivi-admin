package md.leonis.tivi.admin.view.media;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.ComparisionResult;
import md.leonis.tivi.admin.model.Video;
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
    public TreeView<String> treeView;
    public TextField calibreDir;
    public Label calibreTotals;
    public Label siteTotals;
    public ComboBox<BookCategory> categoryCombobox;

    //TODO move all lists and code to to BookUtils
    private List<Video> siteBooks;

    private List<CalibreBook> allCalibreBooks;

    private List<BookCategory> categories;


    @FXML
    private void initialize() {
        //TODO show totals

        categories = BookUtils.readCategories().stream().sorted(Comparator.comparing(BookCategory::getCatcpu)).collect(toList());
        //reloadSiteData();
        reloadCalibreData();

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
        categoryCombobox.setValue(categories.stream().filter(c -> c.getCatcpu().equals("3do")).findFirst().get());

        System.out.println("initialize()");
    }


    @Override
    public void init() {
        System.out.println("init()");
    }


    public void selectCalibreDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("E:\\"));
        directoryChooser.setTitle("Select directory with Calibre DB");
        File selectedDirectory = directoryChooser.showDialog(null);
        calibreDir.setText(selectedDirectory.getAbsolutePath());
        reloadCalibreData();
    }

    public void compare() {
        if (allCalibreBooks == null) {
            reloadCalibreData();
        }
        if (siteBooks == null) {
            reloadSiteData();
        }
        ComparisionResult<Video> comparisionResult = BookUtils.compare(allCalibreBooks, siteBooks, categories, categoryCombobox.getValue().getCatcpu());
        TreeItem<String> addedItem = new TreeItem<>("Added");
        TreeItem<String> deletedItem = new TreeItem<>("Deleted");
        TreeItem<String> changedItem = new TreeItem<>("Changed");

        addedItem.setExpanded(true);
        deletedItem.setExpanded(true);
        changedItem.setExpanded(true);

        TreeItem<String> rootItem = new TreeItem<>("R00T");
        rootItem.getChildren().addAll(addedItem, deletedItem, changedItem);

        addedItem.getChildren().addAll(comparisionResult.getAddedBooks().stream().map(b -> {
            TreeItem<String> treeItem = new TreeItem<>(b.getTitle());
            treeItem.setExpanded(true);
            return treeItem;
        }).collect(toList()));

        deletedItem.getChildren().addAll(comparisionResult.getDeletedBooks().stream().map(b -> {
            TreeItem<String> treeItem = new TreeItem<>(b.getTitle());
            treeItem.setExpanded(true);
            return treeItem;
        }).collect(toList()));

        changedItem.getChildren().addAll(comparisionResult.getChangedBooks().entrySet().stream().map(b -> {
            TreeItem<String> treeItem = new TreeItem<>(b.getKey().getTitle());
            treeItem.setExpanded(true);
            // changes
            b.getValue().forEach(c -> treeItem.getChildren().add(new TreeItem<>(
                    String.format("%1$-15s %2$1s  ->  %3$1s", c.getKey(), c.getValue().getKey(), c.getValue().getValue())
            )));
            return treeItem;
        }).collect(toList()));

        treeView.setRoot(rootItem);
        treeView.setShowRoot(false);
        rootItem.setExpanded(true);
    }

    public void reloadSiteData() {
        siteBooks = BookUtils.getAllBooks();
        siteTotals.setText("" + siteBooks.size());
    }

    private void reloadCalibreData() {
        String configUrl = Config.sqliteUrl;
        Config.sqliteUrl = BookUtils.getJdbcString(calibreDir.getText());
        // TODO журналы в отдельную категорию. потом строить упоминания
        // TODO каталоги кидать в одну тему (island, ...)
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

        BookUtils.syncDataWithSite(comparisionResult, allCalibreBooks, calibreDir.getText());
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

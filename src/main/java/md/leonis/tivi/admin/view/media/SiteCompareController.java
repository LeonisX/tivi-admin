package md.leonis.tivi.admin.view.media;

import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import javafx.util.StringConverter;
import md.leonis.tivi.admin.model.BookCategory;
import md.leonis.tivi.admin.model.ComparisionResult;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.model.media.CalibreBook;
import md.leonis.tivi.admin.model.media.CustomColumn;
import md.leonis.tivi.admin.model.mysql.TableStatus;
import md.leonis.tivi.admin.utils.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class SiteCompareController extends SubPane {

    @FXML
    public GridPane gridPane;
    public TreeView<String> treeView;
    public TextField calibreDir;
    public Label calibreTotals;
    public Label siteTotals;
    public ComboBox<BookCategory> categoryCombobox;

    List<Video> siteBooks;

    List<CalibreBook> allСalibreBooks;

    List<BookCategory> categories;


    @FXML
    private void initialize() {
        //TODO show totals

        categories = BookUtils.readCategories().stream().sorted(Comparator.comparing(BookCategory::getCatcpu)).collect(toList());
        reloadSiteData();
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
        directoryChooser.setTitle("dassdasd");
        File selectedDirectory = directoryChooser.showDialog(null);
        calibreDir.setText(selectedDirectory.getAbsolutePath());
    }

    public void compare() {
        if (allСalibreBooks == null) {
            reloadCalibreData();
        }
        if (siteBooks == null) {
            reloadSiteData();
        }
        ComparisionResult<Video> comparisionResult = BookUtils.compare(allСalibreBooks, siteBooks, categories, categoryCombobox.getValue().getCatcpu());
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

    private String getJdbcString(String path) {
        return String.format("jdbc:sqlite:%s%smetadata.db", path, File.separatorChar);
    }

    public void reloadSiteData() {
        siteBooks = BookUtils.getAllBooks();
        siteTotals.setText("" + siteBooks.size());
    }


    private void reloadCalibreData() {
        String configUrl = Config.sqliteUrl;
        Config.sqliteUrl = getJdbcString(calibreDir.getText());
        // TODO журналы в отдельную категорию. потом строить упоминания
        // TODO каталоги кидать в одну тему (island, ...)
        allСalibreBooks = CalibreUtils.readBooks();
        Config.sqliteUrl = configUrl;
        calibreTotals.setText("" + allСalibreBooks.size());
    }

    public void generate() throws IOException {
        if (allСalibreBooks == null) {
            reloadCalibreData();
        }
        if (siteBooks == null) {
            reloadSiteData();
        }
        ComparisionResult<Video> comparisionResult = BookUtils.compare(allСalibreBooks, siteBooks, categories, categoryCombobox.getValue().getCatcpu());

        String insertQueries = comparisionResult.getAddedBooks().stream().map(b -> BookUtils.objectToSqlInsertQuery(b, Video.class, "danny_media")).collect(Collectors.joining("\n"));
        String deleteQueries = comparisionResult.getDeletedBooks().stream().map(b -> "DELETE FROM danny_media WHERE id=" + b.getId()).collect(Collectors.joining("\n"));
        String updateQueries = comparisionResult.getChangedBooks().entrySet().stream().map(b -> BookUtils.comparisionResultToSqlUpdateQuery(b, "danny_media")).collect(Collectors.joining("\n"));

        String query = insertQueries + "\n\n" + deleteQueries+ "\n\n" + updateQueries;
        String fileName = UUID.randomUUID().toString() + ".sql";
        String result = BookUtils.upload("api2d/backup", fileName, new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
        System.out.println(result);
        result = WebUtils.readFromUrl(Config.apiPath + "dumper.php?to=restore&file=" + fileName);
        System.out.println(result);

        //TODO "IN" QUERY ??
        String configUrl = Config.sqliteUrl;
        Config.sqliteUrl = getJdbcString(calibreDir.getText());
        comparisionResult.getAddedBooks().forEach(b -> {
            Type type = new TypeToken<List<Video>>() {
            }.getType();
            List<Video> videoList = JsonUtils.gson.fromJson(BookUtils.queryRequest("SELECT * FROM danny_media WHERE cpu='" + b.getCpu() + "' AND catid=" + b.getCategoryId()), type);
            Integer tiviId = videoList.get(0).getId();
            Long bookId = allСalibreBooks.stream().filter(cb -> cb.getCpu() != null && cb.getCpu().equals(b.getCpu())).findFirst().get().getId();

            CustomColumn cb = CalibreUtils.readObject("SELECT * FROM `custom_column_17` WHERE book=" + bookId, CustomColumn.class);
            if (cb == null) {
                String q = String.format("INSERT INTO `custom_column_17` VALUES (null, %d, %d)", bookId, tiviId);
                Integer newId = CalibreUtils.executeInsertQuery(q);
                System.out.println(newId);
            } else {
                String q = String.format("UPDATE `custom_column_17` SET value=%d WHERE book=%d", tiviId, bookId);
                Integer newId = CalibreUtils.executeUpdateQuery(q);
                System.out.println(newId);
            }
        });
        Config.sqliteUrl = configUrl;
    }

    public void onSelectCategory() {
    }
}

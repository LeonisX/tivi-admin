package md.leonis.tivi.admin.view.media;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import md.leonis.tivi.admin.model.ComparisionResult;
import md.leonis.tivi.admin.model.media.CalibreBook;
import md.leonis.tivi.admin.utils.CalibreUtils;
import md.leonis.tivi.admin.utils.Config;
import md.leonis.tivi.admin.utils.SubPane;

import java.io.File;

import static java.util.stream.Collectors.toList;

public class CalibreCompareController extends SubPane {

    @FXML
    public GridPane gridPane;
    public TextField newDir;
    public TextField oldDir;
    public TreeView<String> treeView;

    @FXML
    private void initialize() {
        System.out.println("initialize()");
    }

    @Override
    public void init() {
        System.out.println("init()");
    }


    public void selectOldDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Config.workPath));
        directoryChooser.setTitle("dassdasd"); //TODO
        File selectedDirectory = directoryChooser.showDialog(null);
        oldDir.setText(selectedDirectory.getAbsolutePath());
    }

    public void selectNewDir() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(Config.workPath));
        directoryChooser.setTitle("dassdasd");//TODO
        File selectedDirectory = directoryChooser.showDialog(null);
        newDir.setText(selectedDirectory.getAbsolutePath());
    }

    public void compareDbs() {
        ComparisionResult<CalibreBook> comparisionResult = CalibreUtils.compare(getJdbcString(oldDir.getText()), getJdbcString(newDir.getText()));

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
        //return String.format("jdbc:sqlite:E:\\metadata.db");
        return String.format("jdbc:sqlite:%s%smetadata.db", path, File.separatorChar);
    }
}

package helloworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeItem.TreeModificationEvent;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TreeViewSample4 extends Application {

    private EventHandler<TreeModificationEvent<DynamicTreeNodeModel>> branchExpandedEventHandler;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(TreeViewSample4.class, args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Example Dynamic Tree");
        primaryStage.setResizable(true);
        final VBox box = new VBox();
        box.setFillWidth(false);
        Scene scene = new Scene(box);
        primaryStage.setScene(scene);
        box.getChildren().add(this.getExampleTree());
        primaryStage.show();
    }

    private TreeView<DynamicTreeNodeModel> getExampleTree() {
        DynamicTreeNodeModel rootNode = new RandomDynamicTreeNodeModel(null, "Root Node");

        TreeView<DynamicTreeNodeModel> treeView = new TreeView<DynamicTreeNodeModel>();

        treeView.setCellFactory(new Callback<TreeView<DynamicTreeNodeModel>, TreeCell<DynamicTreeNodeModel>>() {
            @Override
            public TreeCell call(TreeView<DynamicTreeNodeModel> param) {
                return new DnDCell(param);
            }
        });

        treeView.setPrefSize(1000, 750);
        TreeItem rootItem = new TreeItem(rootNode);
        branchExpandedEventHandler = new EventHandler<TreeModificationEvent<DynamicTreeNodeModel>>() {
            public void handle(TreeModificationEvent<DynamicTreeNodeModel> event) {
//                System.out.println("handling event " + event);
                TreeItem<DynamicTreeNodeModel> item = event.getTreeItem();
                populateTreeItem(item);
            }
        };

        rootItem.addEventHandler(TreeItem.branchExpandedEvent(), branchExpandedEventHandler);
        treeView.setShowRoot(true);
        treeView.setRoot(rootItem);
        populateTreeItem(rootItem);
        rootItem.setExpanded(true);
//        treeView.setCellFactory(new LearningTreeCellFactory());
        return treeView;
    }

    private void populateTreeItem(TreeItem<DynamicTreeNodeModel> item) {
        DynamicTreeNodeModel node = item.getValue();
        boolean isPopulated = node.isPopulated();
        boolean areGrandChildrenPopulated = node.areChildenPopulated();
        node.populateToDepth(2);
        if (!isPopulated) {
            for (DynamicTreeNodeModel childNode : node.getChildren()) {
                TreeItem childItem = new TreeItem(childNode);
                childItem.addEventHandler(TreeItem.branchExpandedEvent(), branchExpandedEventHandler);
                item.getChildren().add(childItem);
            }
        }
        if (!areGrandChildrenPopulated) {
            int i = 0;
            int size = node.getChildren().size();
            for (TreeItem childItem : item.getChildren()) {
                // get cooresponding node in the model
                if (i < size) {
                    DynamicTreeNodeModel childNode = node.getChildren().get(i);
                    i++;
                    for (DynamicTreeNodeModel grandChildNode : childNode.getChildren()) {
                        TreeItem grandChildItem = new TreeItem(grandChildNode);
                        grandChildItem.addEventHandler(TreeItem.branchExpandedEvent(), branchExpandedEventHandler);
                        childItem.getChildren().add(grandChildItem);
                    }
                }
            }
        }
    }

    private static interface DynamicTreeNodeModel {

        public String getName();

        public void setName(String name);

        public boolean isPopulated();

        public boolean areChildenPopulated();

        public List<DynamicTreeNodeModel> getChildren();

        public void setChildren(List<DynamicTreeNodeModel> children);

        public DynamicTreeNodeModel getParent();

        public void setParent(DynamicTreeNodeModel parent);

        public void populateToDepth(int depth);

        @Override
        public String toString();
    }

    private static class RandomDynamicTreeNodeModel implements DynamicTreeNodeModel {

        private DynamicTreeNodeModel parent;
        private String name;
        private List<DynamicTreeNodeModel> children = null;

        public RandomDynamicTreeNodeModel(DynamicTreeNodeModel parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean isPopulated() {
            if (children == null) {
                return false;
            }
            return true;
        }

        @Override
        public boolean areChildenPopulated() {
            if (!this.isPopulated()) {
                return false;
            }
            for (DynamicTreeNodeModel child : this.children) {
                if (!child.isPopulated()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public List<DynamicTreeNodeModel> getChildren() {
            return children;
        }

        @Override
        public void setChildren(List<DynamicTreeNodeModel> children) {
            this.children = children;
        }

        @Override
        public DynamicTreeNodeModel getParent() {
            return parent;
        }

        @Override
        public void setParent(DynamicTreeNodeModel parent) {
            this.parent = parent;
        }
        private static Random random = new Random();

        @Override
        public void populateToDepth(int depth) {
            if (depth <= 0) {
                return;
            }
            if (children == null) {
                int num = random.nextInt(5);
                System.out.println("got a random number " + num);
                children = new ArrayList(num);
                for (int i = 0; i < num; i++) {
//                    children.add(new RandomDynamicTreeNodeModel(this, "child " + i));

                    children.add(new RandomDynamicTreeNodeModel(this, "child " + System.currentTimeMillis()));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            int childDepth = depth - 1;
            for (DynamicTreeNodeModel child : children) {
                child.populateToDepth(childDepth);
            }
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public class DnDCell extends TreeCell<DynamicTreeNodeModel> {

        private TreeView<DynamicTreeNodeModel> parentTree;

        public DnDCell(final TreeView<DynamicTreeNodeModel> parentTree) {
            this.parentTree = parentTree;
            // ON SOURCE NODE.
            setOnDragDetected(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    System.out.println("Drag detected on " + item);
                    if (item == null) {
                        return;
                    }
                    Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.put(DataFormat.PLAIN_TEXT, item.toString());
                    dragBoard.setContent(content);
                    event.consume();
                }
            });
            setOnDragDone(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent dragEvent) {
                    System.out.println("Drag done on " + item);
                    dragEvent.consume();
                }
            });
            // ON TARGET NODE.
//            setOnDragEntered(new EventHandler<DragEvent>() {
//                @Override
//                public void handle(DragEvent dragEvent) {
//                    System.out.println("Drag entered on " + item);
//                    dragEvent.consume();
//                }
//            });
            setOnDragOver(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent dragEvent) {
                    System.out.println("Drag over on " + item);
                    if (dragEvent.getDragboard().hasString()) {
                        String valueToMove = dragEvent.getDragboard().getString();
                        if (!valueToMove.matches(item.getName())) {
                            // We accept the transfer!!!!!
                            dragEvent.acceptTransferModes(TransferMode.MOVE);
                        }
                    }
                    dragEvent.consume();
                }
            });
//            setOnDragExited(new EventHandler<DragEvent>() {
//                @Override
//                public void handle(DragEvent dragEvent) {
//                    System.out.println("Drag exited on " + item);
//                    dragEvent.consume();
//                }
//            });
            setOnDragDropped(new EventHandler<DragEvent>() {
                @Override
                public void handle(DragEvent dragEvent) {
                    System.out.println("Drag dropped on " + item);
                    String valueToMove = dragEvent.getDragboard().getString();
                    TreeItem<DynamicTreeNodeModel> itemToMove = search(parentTree.getRoot(), valueToMove);
                    TreeItem<DynamicTreeNodeModel> newParent = search(parentTree.getRoot(), item.getName());
                    // Remove from former parent.
                    itemToMove.getParent().getChildren().remove(itemToMove);
                    // Add to new parent.
                    newParent.getChildren().add(itemToMove);
                    newParent.setExpanded(true);
                    dragEvent.consume();
                }
            });
        }

        private TreeItem<DynamicTreeNodeModel> search(final TreeItem<DynamicTreeNodeModel> currentNode, final String valueToSearch) {
            TreeItem<DynamicTreeNodeModel> result = null;
            if (currentNode.getValue().getName().matches(valueToSearch)) {
                result = currentNode;
            } else if (!currentNode.isLeaf()) {
                for (TreeItem<DynamicTreeNodeModel> child : currentNode.getChildren()) {
                    result = search(child, valueToSearch);
                    if (result != null) {
                        break;
                    }
                }
            }
            return result;
        }
        private DynamicTreeNodeModel item;

        @Override
        protected void updateItem(DynamicTreeNodeModel item, boolean empty) {
            super.updateItem(item, empty);
            this.item = item;
            String text = (item == null) ? null : item.toString();
            setText(text);
        }
    }
}
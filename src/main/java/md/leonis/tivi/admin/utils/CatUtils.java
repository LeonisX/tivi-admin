package md.leonis.tivi.admin.utils;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import md.leonis.tivi.admin.model.Category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CatUtils {

    private final List<String> catList;
    private final List<Integer> catIds;

    public CatUtils(List<Category> categories) {
        catList = new ArrayList<>();
        catIds = new ArrayList<>();

        categories.sort(Comparator.comparing(Category::getParentid));

        for (Category category : categories) {
            String name = "";
            for (Category cat : categories) if (cat.getCatid().equals(category.getParentid())) name = cat.getCatname();
            int k = name.isEmpty() ? (catList.size() - 1) : catList.indexOf(name);
            catList.add(k + 1, category.getCatname());
            catIds.add(k + 1, category.getCatid());
        }
    }

    public List<String> getCatList() {
        return catList;
    }

    public List<Integer> getCatIds() {
        return catIds;
    }

    public void setCategoryTextValue(ComboBox<String> category, int catId) {
        String catName = "";
        if (catId > 0) {
            for (Category cat : VideoUtils.categories) if (cat.getCatid() == catId) catName = cat.getCatname();
        }
        catName = (catName.isEmpty()) ? "Выберите категорию" : catName;
        category.setValue(catName);
    }

    public static int getParentId(String catname) {
        for (Category category : VideoUtils.categories)
            if (category.getCatname().equals(catname)) return category.getParentid();
        return -1;
    }

    public static void setCellFactory(ComboBox<String> category) {
        category.setCellFactory((ListView<String> param) ->
                new ListCell<String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            if (CatUtils.getParentId(item) == 0) {
                                setStyle("-fx-background-color: lavender;");
                            } else {
                                setStyle("-fx-padding: 5px 10px;");
                            }
                        } else {
                            setText(null);
                        }
                    }
                }

        );
    }
}

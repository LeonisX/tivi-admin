package md.leonis.tivi.admin.utils;


import md.leonis.tivi.admin.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CatUtils {
    private List<String> catList = null;
    private List<Integer> catIds = null;

    public CatUtils(List<Category> categories) {
        catList = new ArrayList<>();
        catIds = new ArrayList<>();

        categories.sort((o1, o2) -> o1.getParentid().compareTo(o2.getParentid()));

        for (Category category: categories) {
            String name = "";
            for (Category cat: categories) if (cat.getCatid().equals(category.getParentid())) name = cat.getCatname();
            Integer k = name.isEmpty() ? (catList.size() - 1) : catList.indexOf(name);
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
}

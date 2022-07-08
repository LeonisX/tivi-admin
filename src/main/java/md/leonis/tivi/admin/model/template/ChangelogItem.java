package md.leonis.tivi.admin.model.template;

import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.CalibreBook;

import java.util.List;
import java.util.Set;

public class ChangelogItem {

    private String title;
    private Long count;
    private Long diff;

    public ChangelogItem(String title, List<CalibreBook> books, List<CalibreBook> oldBooks, Set<Long> deletedBookIds, Type type) {
        this.title = title;
        this.count = books.stream().filter(b -> b.getType().equals(type)).count();
        this.diff = count - oldBooks.stream().filter(b -> b.getType().equals(type)).filter(b -> !deletedBookIds.contains(b.getId())).count();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getDiff() {
        return diff;
    }

    public void setDiff(Long diff) {
        this.diff = diff;
    }
}

package md.leonis.tivi.admin.model.template;

import lombok.Data;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.CalibreBook;

import java.util.List;
import java.util.Set;

@Data
public class ChangelogItem {

    private String title;
    private Long count;
    private Long diff;
    private String stringDiff;

    public ChangelogItem(String title, List<CalibreBook> books, List<CalibreBook> oldBooks, Set<Long> deletedBookIds, Type type) {
        this.title = title;
        this.count = books.stream().filter(b -> b.getType().equals(type)).count();
        this.diff = count - oldBooks.stream().filter(b -> b.getType().equals(type)).filter(b -> !deletedBookIds.contains(b.getId())).count();
        this.stringDiff = diff <= 0 ? Long.toString(diff) : "+" + diff;
    }
}

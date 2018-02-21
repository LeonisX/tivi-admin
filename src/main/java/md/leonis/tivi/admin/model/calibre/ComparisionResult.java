package md.leonis.tivi.admin.model.calibre;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import md.leonis.tivi.admin.model.media.CalibreBook;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ComparisionResult {

    private final Collection<CalibreBook> addedBooks;
    private final Collection<CalibreBook> deletedBooks;
    private final Map<CalibreBook, List<Pair<String, Pair<String, String>>>> changedBooks;

}

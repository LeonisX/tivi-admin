package md.leonis.tivi.admin.model;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class ComparisionResult<T> {

    private String category;
    private final Collection<T> addedBooks;
    private final Collection<T> deletedBooks;
    private final Map<T, List<Pair<String, Pair<String, String>>>> changedBooks;
}

package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.calibre.Book;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;
import md.leonis.tivi.admin.utils.StringUtils;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.model.Type.BOOK;
import static md.leonis.tivi.admin.utils.Config.sitePath;

public class SearchPageRenderer extends SiteRenderer {

    private final List<CalibreBook> calibreBooks;
    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;

    // Упоминания в других книгах
    public SearchPageRenderer(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        this.filteredSiteBooks = filteredSiteBooks;
        this.category = category;
        this.addedBooks = addedBooks;
        this.oldBooks = oldBooks;

        this.calibreBooks = allCalibreBooks.stream()
                .filter(b -> b.getType().equals(BOOK))
                .filter(b -> b.getOwn() == null || !b.getOwn())
                .filter(b -> b.belongsToCategory(category) || (b.mentionedInCategory(category)) /*||
                        (b.getReleaseNote() != null && !b.getReleaseNote().isEmpty())*/).sorted(Comparator.comparing(Book::getSort))
                .collect(toList());
    }

    // Книги в розыске
    public void generateSearchPage() {
        if (calibreBooks.isEmpty()) {
            return;
        }
        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(category + "_search")).findFirst();
        if (manual.isPresent()) {
            // change
            Video newManual = new Video(manual.get());
            renderTexts(newManual);
            oldBooks.add(newManual);
        } else {
            //add
            Video newManual = new Video();
            renderTexts(newManual);
            newManual.setCpu(category + "_search");
            newManual.setCategoryId(BookUtils.getCategoryId(category));
            newManual.setUrl("");
            newManual.setMirror(sitePath);
            addedBooks.add(newManual);
        }
    }

    private void renderTexts(Video manual) {
        manual.setTitle(StringUtils.PR + "Книги в розыске");
        manual.setText(generateText());
        manual.setFullText(SiteRenderer.generateTableView(calibreBooks));
    }

    private String generateText() {
        return SiteRenderer.generateHeaderImage(BOOK, category, "Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже книг.", "wanted");
    }
}

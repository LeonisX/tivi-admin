package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.calibre.Book;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.Config.sitePath;
import static md.leonis.tivi.admin.utils.StringUtils.BOOK;

public class CitationsRenderer extends SiteRenderer {

    private final List<CalibreBook> calibreBooks;
    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;

    // Упоминания в других книгах
    public CitationsRenderer(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        this.filteredSiteBooks = filteredSiteBooks;
        this.category = category;
        this.addedBooks = addedBooks;
        this.oldBooks = oldBooks;

        this.calibreBooks = allCalibreBooks.stream()
                .filter(b -> b.getType().equals(BOOK))
                .filter(b -> b.getOwn() != null && b.getOwn())
                .filter(b -> b.mentionedInCategory(category))
                .sorted(Comparator.comparing(Book::getSort))
                .collect(toList());
    }

    public void generateCitationsPage() {
        if (calibreBooks.isEmpty()) {
            return;
        }
        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(category + "_citation")).findFirst();
        if (manual.isPresent()) {
            // change
            Video newManual = new Video(manual.get());
            renderTexts(newManual);
            oldBooks.add(newManual);
        } else {
            //add
            Video newManual = new Video();
            renderTexts(newManual);
            newManual.setTitle("Упоминания в других книгах");
            newManual.setCpu(category + "_citation");
            newManual.setCategoryId(BookUtils.getCategoryByCpu(category).getCatid());
            newManual.setUrl("");
            newManual.setMirror(sitePath);
            addedBooks.add(newManual);
        }
    }

    private void renderTexts(Video manual) {
        manual.setText(generateText());
        manual.setFullText(generateFullText());
    }


    private String generateText() {
        return String.format("<p>В этих книгах так же можно найти информацию об играх для %s.</p>", BookUtils.getCategoryName(category));
    }

    private String generateFullText() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul class=\"file-info\">\n");
        calibreBooks.forEach(b -> sb.append(String.format("<li><a href=\"%s\">%s</a></li>", generateBookViewUri(b.getCpu()), b.getTitle())));
        sb.append("</ul>\n");
        return sb.toString();
    }
}

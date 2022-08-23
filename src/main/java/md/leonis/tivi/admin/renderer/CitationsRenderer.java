package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.calibre.Book;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.calibre.TypeTranslation;
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
import static md.leonis.tivi.admin.utils.StringUtils.typeTranslationMap;

public class CitationsRenderer extends SiteRenderer {

    private final List<CalibreBook> calibreBooks;
    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;

    private final TypeTranslation translation;
    private final Declension declension;

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

        this.translation = typeTranslationMap.get(BOOK);
        this.declension = StringUtils.getDeclension(BookUtils.getCategoryName(category));
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
            newManual.setCpu(category + "_citation");
            newManual.setCategoryId(BookUtils.getCategoryId(category));
            newManual.setUrl("");
            newManual.setMirror(sitePath);
            addedBooks.add(newManual);
        }
    }

    private void renderTexts(Video manual) {
        manual.setTitle(generateTitle());
        manual.setText(generateText());
        manual.setFullText("");
    }

    private String generateTitle() {
        return String.format(translation.getShortText(), declension.getRod());
    }

    //TODO вероятно сгруппированные тоже надо генерить
    private String generateText() {
        StringBuilder sb = new StringBuilder();
        //sb.append(String.format("<p>В этих книгах так же можно найти информацию об играх для %s:</p>\n", BookUtils.getCategoryName(category)));
        sb.append("<ul class=\"file-info\">\n");
        calibreBooks.forEach(b -> sb.append(String.format("<li><a href=\"%s\">%s</a></li>\n", generateBookViewUri(b.getCpu()), b.getTitle())));
        sb.append("</ul>\n");
        return SiteRenderer.generateHeaderImage(BOOK, category, sb.toString(), "citation");
    }
}

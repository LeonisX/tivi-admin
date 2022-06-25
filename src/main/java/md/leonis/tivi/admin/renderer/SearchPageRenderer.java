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
                .filter(b -> b.belongsToCategory(category) || (b.mentionedInCategory(category)) ||
                        (b.getReleaseNote() != null && !b.getReleaseNote().isEmpty())).sorted(Comparator.comparing(Book::getSort))
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
            newManual.setCategoryId(BookUtils.getCategoryByCpu(category).getCatid());
            newManual.setUrl("");
            newManual.setMirror(sitePath);
            addedBooks.add(newManual);
        }
    }

    private void renderTexts(Video manual) {
        manual.setTitle("Книги в розыске");
        manual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже книг.</p>");
        manual.setFullText(generateTableView(calibreBooks));
    }

    //TODO html
    private String generateTableView(List<CalibreBook> books) {
        int counter = 1;
        StringBuilder sb = new StringBuilder();
        StringBuilder imageBuilder = new StringBuilder();
        StringBuilder titleBuilder = new StringBuilder();
        sb.append("<p><table style=\"width:600px;\">");
        for (CalibreBook book : books) {
            if (counter == 1) {
                sb.append("<tr>");
            }
            imageBuilder.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\">");
            titleBuilder.append("<td style=\"text-align:center; padding-top: 5px; padding-bottom: 10px;\">");
            if (book.getHasCover() != 0) {
                String imageLink = generateBookCoverUri(BookUtils.getCategoryByTags(book), book.getCpu());
                String imageThumb = generateBookThumbUri(BookUtils.getCategoryByTags(book), book.getCpu());
                String imageTitle = book.getOfficialTitle() == null ? book.getTitle() : book.getOfficialTitle();
                String imageAlt = book.getFileName() == null ? book.getTitle() : book.getFileName();
                imageBuilder.append(String.format("<a href=\"%s\"><img style=\"border: 1px solid #aaaaaa;\" title=\"%s\" src=\"%s\" alt=\"%s\" /></a>", imageLink, imageTitle, imageThumb, imageAlt));
            } else {
                String imageThumb = "images/books/nocover.png";
                String imageTitle = book.getOfficialTitle() == null ? book.getTitle() : book.getOfficialTitle();
                String imageAlt = book.getFileName() == null ? book.getTitle() : book.getFileName();
                imageBuilder.append(String.format("<img style=\"border: 1px solid #aaaaaa;\" title=\"%s\" src=\"%s\" alt=\"%s\" />", imageTitle, imageThumb, imageAlt));
            }
            titleBuilder.append(book.getTitle());
            imageBuilder.append("</td>");
            titleBuilder.append("</td>");
            counter++;
            if (counter > 3) {
                imageBuilder.append("</tr><tr>");
                titleBuilder.append("</tr>");
                sb.append(imageBuilder).append(titleBuilder);
                imageBuilder = new StringBuilder();
                titleBuilder = new StringBuilder();
                counter = 1;
            }
        }
        if (counter != 1) {
            for (int i = counter - 1; i <= 3; i++) {
                imageBuilder.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\"></td>");
                titleBuilder.append("<td style=\"text-align:center;\"></td>");
            }
        }
        if (!imageBuilder.toString().isEmpty()) {
            sb.append(imageBuilder).append("</tr>").append(titleBuilder).append("</tr>");
        }
        sb.append("</table></p>");
        return sb.toString();
    }
}

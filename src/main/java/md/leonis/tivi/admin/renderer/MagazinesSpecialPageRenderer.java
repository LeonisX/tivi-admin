package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.calibre.Book;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;
import md.leonis.tivi.admin.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static md.leonis.tivi.admin.utils.Config.sitePath;

public class MagazinesSpecialPageRenderer extends SiteRenderer {

    public static final String CPU = "magazines_special";

    private final Map<CalibreBook, List<CalibreBook>> groupedMagazines;
    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;

    // Упоминания в других книгах
    public MagazinesSpecialPageRenderer(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        this.filteredSiteBooks = filteredSiteBooks;
        this.category = category;
        this.addedBooks = addedBooks;
        this.oldBooks = oldBooks;

        this.groupedMagazines = allCalibreBooks.stream()
                .sorted(Comparator.comparing(Book::getSort))
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()))
                .entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue().get(0), Map.Entry::getValue))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Специализированные журналы
    public void generateMagazinesSpecialPage() {
        if (groupedMagazines.isEmpty()) {
            return;
        }
        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(CPU)).findFirst();
        if (manual.isPresent()) {
            // change
            Video newManual = new Video(manual.get());
            renderTexts(newManual);
            oldBooks.add(newManual);
        } else {
            //add
            Video newManual = new Video();
            renderTexts(newManual);
            newManual.setCpu(CPU);
            newManual.setCategoryId(BookUtils.getCategoryId(category));
            newManual.setUrl("");
            newManual.setMirror(sitePath);
            addedBooks.add(newManual);
        }
    }

    private void renderTexts(Video manual) {
        manual.setTitle(StringUtils.PR + "Специализированные периодические издания");
        manual.setText(generateText());
        manual.setFullText("");
    }

    private String generateText() {
        StringBuilder sb = new StringBuilder();

        CalibreBook book = groupedMagazines.entrySet().stream().filter(e -> MagazinesRenderer.isSpecific(e.getValue()))
                .min(Comparator.comparing(b -> b.getKey().getSeries().getName())).orElseThrow(RuntimeException::new).getKey();
        String imageLink = generateBookThumbUri(BookUtils.getCategoryByTags(book), book.getCpu());

        sb.append(String.format("<p><img style=\"border: 1px solid #aaaaaa; float: right; margin: 5px;\" title=\"%s\" src=\"%s\" alt=\"%s\" />\n",
                "Специализированные периодические издания", imageLink, "Специализированные журналы"));

        groupedMagazines.entrySet().stream().filter(e -> MagazinesRenderer.isSpecific(e.getValue()))
                .sorted(Comparator.comparing(b -> b.getKey().getSeries().getName()))
                .collect(Collectors.groupingBy(b -> BookUtils.getCategoryName(MagazinesRenderer.getSpecificTag(b.getValue()))))
                .entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    sb.append(String.format("<p  style=\"color:gray\"><strong><i>:: %s</i></strong></p>\n", e.getKey()));
                    sb.append("<ul class=\"file-info\">\n");
                    e.getValue().forEach(b -> sb.append(String.format("<li><a href=\"%s\">%s</a></li>\n", generateBookViewUri(b.getKey().getSiteCpu()), b.getKey().getSeries().getName())));
                    sb.append("</ul>\n");
                });

        sb.append("</p>\n");

        return sb.toString();
    }
}

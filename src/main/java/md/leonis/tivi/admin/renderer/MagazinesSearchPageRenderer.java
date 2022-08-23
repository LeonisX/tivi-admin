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
import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.model.Type.BOOK;
import static md.leonis.tivi.admin.model.Type.MAGAZINE;
import static md.leonis.tivi.admin.utils.Config.sitePath;

public class MagazinesSearchPageRenderer extends SiteRenderer {

    public static final String CPU = "magazines_in_search";

    private final Map<CalibreBook, List<CalibreBook>> groupedMagazines;
    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;

    // Упоминания в других книгах
    public MagazinesSearchPageRenderer(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        this.filteredSiteBooks = filteredSiteBooks;
        this.category = category;
        this.addedBooks = addedBooks;
        this.oldBooks = oldBooks;

        List<CalibreBook> magazines = allCalibreBooks.stream().filter(b -> b.getType().equals(MAGAZINE) && !category.equals("gd"))
                .filter(b -> b.getOwn() == null || !b.getOwn()).sorted(Comparator.comparing(Book::getSort)).collect(toList());

        this.groupedMagazines = magazines.stream()
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()))
                .entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue().get(0), Map.Entry::getValue))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Разыскиваемые журналы
    public void generateMagazinesSearchPage() {
        if (groupedMagazines.isEmpty()) {
            return;
        }
        Optional<Video> magazine = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(CPU)).findFirst();
        if (magazine.isPresent()) {
            // change
            Video newMagazine = new Video(magazine.get());
            renderTexts(newMagazine);
            oldBooks.add(newMagazine);
        } else {
            //add
            Video newMagazine = new Video();
            renderTexts(newMagazine);
            newMagazine.setCpu(CPU);
            newMagazine.setCategoryId(BookUtils.getCategoryId(category));
            newMagazine.setUrl("");
            newMagazine.setMirror(sitePath);
            addedBooks.add(newMagazine);
        }
    }

    private void renderTexts(Video magazine) {
        magazine.setTitle(StringUtils.PR + "Разыскиваемые периодические издания");
        magazine.setText(SiteRenderer.generateHeaderImage(BOOK, category, "Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже газет и журналов.", "wanted"));
        magazine.setFullText(generateFullText());
    }

    private String generateFullText() {
        StringBuilder sb = new StringBuilder();
        groupedMagazines.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getSeries().getName())).forEach(e -> {
            sb.append(String.format("<h3>%s</h3>\n", e.getKey().getSeries().getName()));
            sb.append("<ul class=\"file-info\">\n");
            e.getValue().forEach(c -> sb.append(String.format("<li>%s</li>\n", c.getTitle())));
            sb.append("</ul>\n");
        });

        sb.append(SiteRenderer.generateMissedImagesList(groupedMagazines.values().stream().flatMap(Collection::stream).collect(toList())));
        return sb.toString();
    }
}

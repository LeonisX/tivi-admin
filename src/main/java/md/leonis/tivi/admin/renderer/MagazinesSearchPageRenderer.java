package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.calibre.Book;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.model.Type.MAGAZINE;
import static md.leonis.tivi.admin.utils.Config.sitePath;

public class MagazinesSearchPageRenderer extends SiteRenderer {

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
                //.filter(b -> b.belongsToCategory(category))
                .filter(b -> b.getOwn() == null || !b.getOwn()).sorted(Comparator.comparing(Book::getSort)).collect(toList());

        this.groupedMagazines = magazines.stream()//.filter(b ->
                //b.belongsToCategory(category) || (b.mentionedInCategory(category)))
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()))
                .entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue().get(0), Map.Entry::getValue))
                .entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Разыскиваемые журналы
    public void generateMagazinesSearchPage() {
        if (groupedMagazines.isEmpty()) {
            return;
        }
        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals("magazines_in_search")).findFirst();
        if (manual.isPresent()) {
            // change
            Video newManual = new Video(manual.get());
            renderTexts(newManual);
            oldBooks.add(newManual);
        } else {
            //add
            Video newManual = new Video();
            renderTexts(newManual);
            newManual.setCpu("magazines_in_search");
            newManual.setCategoryId(BookUtils.getCategoryId(category));
            newManual.setUrl("");
            newManual.setMirror(sitePath);
            addedBooks.add(newManual);
        }
    }

    private void renderTexts(Video manual) {
        manual.setTitle("Разыскиваемые журналы");
        manual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже журналов.</p>");
        manual.setFullText(generateFullText());
    }

    private String generateFullText() {
        StringBuilder sb = new StringBuilder();
        //TODO link
        //TODO table with images
        groupedMagazines.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getSeries().getName())).forEach(e -> {
            sb.append(String.format("<h3>%s</h3>", e.getKey().getSeries().getName()));
            sb.append("<ul class=\"file-info\">\n");
            e.getValue().forEach(c -> sb.append(String.format("<li>%s</li>", c.getTitle())));
            sb.append("</ul>\n");
        });
        return sb.toString();
    }
}

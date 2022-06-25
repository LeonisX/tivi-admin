package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.calibre.PublisherSeries;
import md.leonis.tivi.admin.model.calibre.TypeTranslation;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;
import md.leonis.tivi.admin.utils.StringUtils;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static md.leonis.tivi.admin.utils.Config.sitePath;
import static md.leonis.tivi.admin.utils.StringUtils.COMICS;
import static md.leonis.tivi.admin.utils.StringUtils.viewTypeTranslationMap;

public class MagazinesRenderer extends SiteRenderer {

    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;

    private final Map<String, List<CalibreBook>> groupedBooks;
    private final TypeTranslation translation;
    private final Declension declension;

    public MagazinesRenderer(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks, String type) {
        this.filteredSiteBooks = filteredSiteBooks;
        this.category = category;
        this.addedBooks = addedBooks;
        this.oldBooks = oldBooks;

        this.groupedBooks = allCalibreBooks.stream()
                .filter(b -> b.getType().equals(type))
                .filter(b -> b.getOwn() != null && b.getOwn())
                .filter(b -> b.belongsToCategory(category) || b.mentionedInCategory(category))
                .peek(b -> {
                    if (b.getSeries() == null) {
                        b.setSeries(new PublisherSeries(0L, b.getTitle(), ""));
                    }
                })
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()));
        this.translation = viewTypeTranslationMap.get(type);
        this.declension = StringUtils.getDeclension(BookUtils.getCategoryName(category));
    }

    public void generateMagazinesPage() {
        if (groupedBooks.isEmpty()) {
            return;
        }
        String cpu = generateMagazinesCpu();
        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(cpu)).findFirst();
        if (manual.isPresent()) {
            // change
            Video newManual = new Video(manual.get());
            renderTexts(newManual);
            oldBooks.add(newManual);
        } else {
            //add
            Video newManual = new Video();
            renderTexts(newManual);
            newManual.setCpu(cpu);
            newManual.setCategoryId(BookUtils.getCategoryId(category));
            newManual.setUrl("");
            newManual.setMirror(sitePath);
            addedBooks.add(newManual);
        }
    }

    private String generateMagazinesCpu() {
        return category + "_" + translation.getPlural();
    }

    private void renderTexts(Video manual) {
        manual.setTitle(generateTitle());
        manual.setText(generateText());
        manual.setFullText(generateFullText());
    }

    private String generateTitle() {
        return String.format(translation.getShortText(), declension.getRod());
    }

    private String generateText() {
        return String.format(translation.getText(), declension.getRod());
    }

    //TODO html
    private String generateFullText() {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul class=\"file-info\">\n");
        //new TreeMap<>(books).forEach((key, value) -> sb.append(String.format("<li><a href=\"%s\">%s</a></li>", generateBookViewUri(value.get(0).getCpu()), key)));
        new TreeMap<>(groupedBooks).forEach((key, value) -> {
            if (value.get(0).getType().equals(COMICS)) {
                sb.append(String.format("<li><a href=\"%s\">%s</a></li>", generateBookViewUri(value.get(0).getCpu()), key));
            } else {
                sb.append(String.format("<li><a href=\"%s\">%s</a></li>", generateBookViewUri(BookUtils.generateCpu(value.get(0).getSeries().getName())), key));
            }
        });
        sb.append("</ul>\n");
        return sb.toString();
    }
}

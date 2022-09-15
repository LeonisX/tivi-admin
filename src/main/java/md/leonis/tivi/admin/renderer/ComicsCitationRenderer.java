package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.*;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;
import md.leonis.tivi.admin.utils.StringUtils;

import java.util.*;

import static md.leonis.tivi.admin.model.Type.COMICS;
import static md.leonis.tivi.admin.utils.Config.sitePath;
import static md.leonis.tivi.admin.utils.StringUtils.typeTranslationMap;

public class ComicsCitationRenderer extends SiteRenderer {

    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;
    private final Type type = COMICS;

    private final Map<String, List<CalibreBook>> groupedBooks;
    private final TypeTranslation translation;
    private final Declension declension;

    public ComicsCitationRenderer(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        this.filteredSiteBooks = filteredSiteBooks;
        this.category = category;
        this.addedBooks = addedBooks;
        this.oldBooks = oldBooks;

        this.groupedBooks = MagazinesCitationRenderer.getGroupedBooks(allCalibreBooks, type, category);
        this.translation = typeTranslationMap.get(type);
        this.declension = StringUtils.getDeclension(BookUtils.getCategoryName(category));
    }

    public void generateCitationPage() {
        if (groupedBooks.isEmpty()) {
            return;
        }
        String cpu = generateMagazinesCpu();
        Optional<Video> magazine = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(cpu)).findFirst();
        if (magazine.isPresent()) {
            // change
            Video newMagazine = new Video(magazine.get());
            renderTexts(newMagazine);
            oldBooks.add(newMagazine);
        } else {
            //add
            Video newMagazine = new Video();
            renderTexts(newMagazine);
            newMagazine.setCpu(cpu);
            newMagazine.setCategoryId(BookUtils.getCategoryId(category));
            newMagazine.setUrl("");
            newMagazine.setMirror(sitePath);
            addedBooks.add(newMagazine);
        }
    }

    private String generateMagazinesCpu() {
        return category + "_" + translation.getPlural();
    }

    private void renderTexts(Video magazine) {
        magazine.setTitle(generateTitle());
        magazine.setText(generateText());
        magazine.setFullText("");
    }

    private String generateTitle() {
        return String.format(translation.getShortText(), declension.getRod());
    }

    //TODO html
    private String generateText() {
        StringBuilder sb = new StringBuilder();
        sb.append(SiteRenderer.generateHeaderImage(type, category, String.format(translation.getText(), declension.getTv()), "comics"));
        sb.append("<ul class=\"file-info\">\n");
        new TreeMap<>(groupedBooks).forEach((key, value) ->
                sb.append(String.format("<li><a href=\"%s\">%s</a></li>\n", generateBookViewUri(value.get(0).getCpu()), key))
        );
        sb.append("</ul>\n");
        return sb.toString();

    }
}

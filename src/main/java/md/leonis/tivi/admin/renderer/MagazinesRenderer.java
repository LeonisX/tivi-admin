package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.*;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.CalibreUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;
import md.leonis.tivi.admin.utils.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static md.leonis.tivi.admin.model.Type.COMICS;
import static md.leonis.tivi.admin.utils.Config.sitePath;
import static md.leonis.tivi.admin.utils.StringUtils.typeTranslationMap;

public class MagazinesRenderer extends SiteRenderer {

    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;
    private final Type type;

    private final Map<String, List<CalibreBook>> groupedBooks;
    private final TypeTranslation translation;
    private final Declension declension;

    public MagazinesRenderer(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks, Type type) {
        this.filteredSiteBooks = filteredSiteBooks;
        this.category = category;
        this.addedBooks = addedBooks;
        this.oldBooks = oldBooks;
        this.type = type;

        this.groupedBooks = getGroupedBooks(allCalibreBooks, type, category);
        this.translation = typeTranslationMap.get(type);
        this.declension = StringUtils.getDeclension(BookUtils.getCategoryName(category));
    }

    public void generateMagazinesPage() {
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
        if (type.equals(COMICS)) {
            sb.append(SiteRenderer.generateHeaderImage(type, category, String.format(translation.getText(), declension.getRod()), "comics"));
            sb.append("<ul class=\"file-info\">\n");
            new TreeMap<>(groupedBooks).forEach((key, value) ->
                    sb.append(String.format("<li><a href=\"%s\">%s</a></li>\n", generateBookViewUri(value.get(0).getCpu()), key))
            );
            sb.append("</ul>\n");
            return sb.toString();
        } else {
            List<String> specificMags = getMagsHtml(true);
            List<String> commonMags = getMagsHtml(false);
            List<String> citations = new TreeMap<>(groupedBooks).values().stream()
                    .filter(calibreBooks -> calibreBooks.stream().flatMap(v -> v.getAltTags().stream()).map(CustomColumn::getValue).collect(Collectors.toSet()).contains(category))
                    .map(calibreBooks -> String.format("<li><a href=\"%s\">%s</a></li>\n", generateBookViewUri(CalibreUtils.getMagazineCpu(calibreBooks.get(0))), CalibreUtils.getMagazineTitle(calibreBooks.get(0))))
                    .collect(Collectors.toList());
            citations.removeAll(specificMags);
            citations.removeAll(commonMags);
            if (!commonMags.isEmpty()) {
                sb.append("<ul class=\"file-info\">\n");
                commonMags.forEach(sb::append);
                sb.append("</ul>\n");
            }
            if (!citations.isEmpty()) {
                if (!specificMags.isEmpty()) {
                    sb.append("<p>А так же:</p>\n");
                }
                sb.append("<ul class=\"file-info\">\n");
                citations.forEach(sb::append);
                sb.append("</ul>\n");

            }
            return SiteRenderer.generateHeaderImage(type, category, sb.toString(), "mention");
        }
    }

    private List<String> getMagsHtml(boolean specific) {
        return getMags(groupedBooks, category, specific).entrySet().stream()
                .map(e -> String.format("<li><a href=\"%s\">%s</a></li>\n", generateBookViewUri(StringUtils.generateCpu(e.getValue().get(0).getSeries().getName())), e.getKey()))
                .collect(Collectors.toList());
    }

    private static final List<String> multi = Arrays.asList("consoles", "computers");

    public static Map<String, List<CalibreBook>> getMags(Map<String, List<CalibreBook>> groupedBooks, String category, boolean specific) {
        return new TreeMap<>(groupedBooks).entrySet().stream()
                .filter(e -> {
                    System.out.println(e.getKey());
                    if (specific) {
                        return !multi.contains(category) && isSpecific(e.getValue(), category);
                    } else {
                        return !isSpecific(e.getValue());
                    }
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static boolean isSpecific(List<CalibreBook> books) {
        String tag = getSpecificTag(books);
        return !multi.contains(tag) && isSpecific(books, tag);
    }

    public static boolean isSpecific(List<CalibreBook> books, String category) {
        List<String> tags = books.stream().flatMap(b -> b.getTags().stream()).map(Tag::getName).collect(Collectors.toList());
        return tags.stream().filter(t -> t.equals(category)).count() * 100.0 / tags.size() >= 50;
    }

    public static String getSpecificTag(List<CalibreBook> books) {
        Set<Map.Entry<String, List<String>>> set = books.stream().flatMap(b -> b.getTags().stream()).map(Tag::getName)
                .collect(groupingBy(Function.identity())).entrySet();
        return Collections.max(set, Comparator.comparingInt((Map.Entry<String, List<String>> e2) -> e2.getValue().size())).getKey();
    }

    //тут забираются не все журналы. сначала вытаскивать серии, потом искать по ним.
    //так же посмотреть как испоьзуется. тут журналы, макстмум еще комиксы.
    public static Map<String, List<CalibreBook>> getGroupedBooks(List<CalibreBook> books, Type type, String category) {
        Set<String> series = books.stream()
                .filter(b -> b.getType().equals(type))
                .filter(b -> b.belongsToCategory(category) || b.mentionedInCategory(category))
                .filter(b -> b.getSeries() != null)
                .map(b -> b.getSeries().getName())
                .distinct()
                .collect(Collectors.toSet());
        return books.stream()
                .filter(b -> b.getType().equals(type))
                //.filter(b -> b.getOwn() != null && b.getOwn())
                .peek(b -> {
                    if (b.getSeries() == null) {
                        b.setSeries(new PublisherSeries(0L, b.getTitle(), ""));
                    }
                })
                .filter(b -> series.contains(b.getSeries().getName()))
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()));
    }
}

package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.calibre.Author;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.calibre.Tag;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.StringUtils.GUIDE;

public class ForumGuidesRenderer extends SiteRenderer {

    private static final List<String> capitalize = Arrays.asList("arcade", "saturn", "switch", "amiga", "lynx", "jaguar", "android", "dreamcast");

    private static final Map<String, String> replace = new HashMap<>();

    static {
        replace.put("megadrive", "Sega MD");
        replace.put("psone", "PS One");
        replace.put("neogeo", "Neo-Geo");
        replace.put("xbox360", "X360");
        replace.put("spectrum", "ZX Spectrum");
        replace.put("2600", "Atari 2600");
        replace.put("5200", "Atari 5200");
        replace.put("consoles", "Приставки");
        replace.put("gamecom", "Game.com");
        replace.put("ios", "iOS");
        replace.put("gamecube", "GameCube");
        replace.put("gb", "GameBoy");
        replace.put("xboxone", "Xbox One");
        replace.put("segacd", "Sega CD");
        replace.put("wii", "Wii");
        replace.put("wiiu", "Wii U");
        replace.put("computers", "Компьютеры");
        replace.put("apple2", "Apple II");
    }

    private final Map<String, List<CalibreBook>> groupedBooks;

    public ForumGuidesRenderer(List<CalibreBook> calibreBooks) {
        this.groupedBooks = calibreBooks.stream()
                .filter(b -> b.getType().equals(GUIDE))
                .filter(CalibreBook::getOwn)
                .collect(Collectors.groupingBy(b -> {
                    String authors = b.getAuthors().stream().map(Author::getName).collect(joining(", ")).replace("|", ",");
                    if (authors.equalsIgnoreCase("неизвестный")) {
                        authors = b.getPublisher() != null ? b.getPublisher().getName() : "неизвестный";
                    }
                    return authors;
                }));
    }

    //TODO file output, [bb]
    public void generateForumGuides() {
        groupedBooks.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().toLowerCase())).forEach(entry -> {

            System.out.println(String.format("\n[b]%s[/b]", entry.getKey()));

            List<String> lines = new ArrayList<>();

            entry.getValue().forEach(b -> {

                List<String> sources = getSources(b);
                List<String> links = getExternalLink(b);

                Set<String> fileNames = new HashSet<>();

                b.getDataList().forEach(d -> {
                    sources.add(d.getFormat());
                    String fileName = findFreeFileName(fileNames, b.getFileName() != null ? b.getFileName() : b.getTitle(), d.getFormat().toLowerCase(), 0);
                    links.add(generateDownloadLink(GUIDE, BookUtils.getCategoryByTags(b), fileName));
                });

                String text = b.getTextShort().isEmpty() ? b.getTextMore() : b.getTextShort();
                text = trimHtmlTags(text.replace("\n", "").replace("\r", ""))
                        .replace("<p>", "").replace("</p>", "").replace("<strong>", "[b]").replace("</strong>", "[/b]")
                        .replace("<b>", "[b]").replace("</b>", "[/b]").replace("&amp;", "&").trim();
                if (text.endsWith(".")) {
                    text = text.substring(0, text.length() - 1);
                }

                int count = (text.length() - text.replace("[b]", "").length()) / 3;

                if (count == 1) {
                    text = text.substring(text.indexOf("[b]"), text.indexOf("[/b]") + 4);
                }

                String categories = String.format("- [URL=\"%s\"]%s[/URL] (%s)", links.get(0), text, formatCategories(b));

                StringBuilder first = new StringBuilder(categories);
                for (int i = 0; i < sources.size(); i++) {
                    if (i == 0) {
                        first.append(String.format("(%s)", sources.get(i)));
                    } else {
                        first.append(String.format("([URL=\"%s\"]%s[/URL])", links.get(i), sources.get(i)));
                    }
                }

                lines.add(String.format("%s;", first.toString()));
            });

            lines.stream().sorted(Comparator.comparing(l -> l.substring(l.indexOf("\"]")))).forEach(System.out::println);
        });

        //manual.setFullText(fixLatinChars(manual.getFullText()));
    }


    private String formatCategories(CalibreBook book) {
        List<String> categories = book.getTags().stream().map(Tag::getName).map(t -> {
            if (capitalize.contains(t)) {
                return t.substring(0, 1).toUpperCase() + t.substring(1);
            }
            if (replace.containsKey(t)) {
                return replace.get(t);
            }
            return t.toUpperCase();
        }).collect(toList());

        if (categories.size() > 4) {
            categories = categories.subList(0, 4);
            categories.add("...");
        }
        return String.join(", ", categories);
    }

    private List<String> getSources(CalibreBook book) {
        List<String> sources = new ArrayList<>();
        if (book.getExternalLink() != null && !book.getExternalLink().isEmpty()) {
            if (book.getExternalLink().contains("showthread")) {
                sources.add("Форум");
            } else if (book.getExternalLink().contains("blog")) {
                sources.add("Дневник");
            } else if (book.getExternalLink().contains("wiki")) {
                sources.add("База знаний");
            } else {
                sources.add("Другой источник");
            }
        }
        return sources;
    }

    private List<String> getExternalLink(CalibreBook book) {
        List<String> links = new ArrayList<>();
        if (book.getExternalLink() != null && !book.getExternalLink().isEmpty()) {
            links.add(book.getExternalLink());
        }
        return links;
    }
}

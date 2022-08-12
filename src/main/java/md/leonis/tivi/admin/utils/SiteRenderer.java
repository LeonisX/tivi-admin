package md.leonis.tivi.admin.utils;

import javafx.util.Pair;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.*;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.renderer.TextShortRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static md.leonis.tivi.admin.utils.Config.sitePath;
import static md.leonis.tivi.admin.utils.StringUtils.platformsTranslationMap;
import static md.leonis.tivi.admin.utils.StringUtils.typeTranslationMap;

public class SiteRenderer {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String cloudStorageLink;

    public static String generateSystemIconLink(String catCpu) {
        return String.format("images/systems/%s.png", catCpu);
    }

    public static String generateDownloadLink(Type type, String category, String fileName) {
        return String.format("%s/up/media/%ss/%s/%s", sitePath, typeTranslationMap.get(type).getPlural(), category, fileName);
    }

    public static String generateDownloadLink(Type type, String category, String fileName, String ext) {
        return String.format("%s/up/media/%ss/%s/%s.%s", sitePath, typeTranslationMap.get(type).getPlural(), category, fileName, ext);
    }

    //TODO enum const or delete this comment
    public static String generateSiteUri(CalibreBook book) {
        switch (book.getType()) {
            case BOOK:
            case MAGAZINE:
            case COMICS:
                return generateBookViewUri(book.getSiteCpu());
            case GUIDE:
            case DOC:
            case MANUAL:
            case EMULATOR:
                return generateBookGroupViewUri(BookUtils.getCategoryByTags(book), book.getType());
            default:
                throw new RuntimeException("Wrong book type: " + book.getType());
        }
    }

    public static String generateBookViewUri(String cpu) {
        return String.format("%s/media/open/%s.html", sitePath, cpu);
    }

    public static String generateBookGroupViewUri(String category, Type type) {
        return String.format("%s/media/open/%s_%ss.html", sitePath, category, type);
    }

    public static String generateBookCategoryUri(String cpu) {
        return String.format("%s/media/view/%s.html", sitePath, cpu);
    }

    public static String generateBookThumbUri(String category, String cpu) {
        return String.format("%s/images/books/thumb/%s/%s.jpg", sitePath, category, cpu);
    }

    public static String generateBookCoverUri(String category, String cpu) {
        return String.format("%s/images/books/cover/%s/%s.jpg", sitePath, category, cpu);
    }

    static String cleanHtml(String str) {
        return str.trim().replace("\\r", "").replace("\\n", "")
                .replace("\r", "").replace("\n", "")
                .replace("\u0000", "").replace(" ", "")
                .replace("<p></p><p>", "<p>").replace("</p><p></p>", "</p>");
    }

    //TODO
    //TODO probably for books, magazines too
    //TODO probably for vBulletin renderers too, need to check
    protected static String fixLatinChars(String fullText) {
        fullText = fullText.replace("ü", "&#xfc;");
        fullText = fullText.replace("ō", "&#x14d;");
        fullText = fullText.replace("ū", "&#x16b;");
        return fullText;
    }

    protected static String trimHtmlTags(String text) {
        Element element = Jsoup.parseBodyFragment(text).body();
        return doTrimHtmlTags(element);
    }

    private static String doTrimHtmlTags(Node node) {
        if (node instanceof TextNode) {
            return "<p>" + ((TextNode) node).text() + "</p>";
        }
        if (node.childNodeSize() > 1) {
            return node.childNodes().stream().map(c -> {
                if (c instanceof TextNode) {
                    return ((TextNode) c).text();
                } else {
                    return c.outerHtml();
                }
            }).collect(joining());
        } else {
            if (node.childNodeSize() == 0) {
                return "";
            } else {
                return doTrimHtmlTags(node.childNodes().get(0));
            }
        }
    }

    protected static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "???";
        } else if (dateTime.toLocalDate().isBefore(LocalDate.of(1000, 1, 1))) {
            return "???";
        } else if (dateTime.plusHours(4).getDayOfMonth() == 1 && dateTime.plusHours(4).getMonthValue() == 1) {
            return Integer.toString(dateTime.plusHours(4).getYear());
        } else {
            return dateTime.plusHours(4).toLocalDate().format(DTF);
        }
    }

    //TODO html
    public static String getMagazineFullText(Map.Entry<CalibreBook, List<CalibreBook>> groupedMagazines, String category, String cpu) {
        int[] k = new int[1];
        k[0] = 1;
        return groupedMagazines.getValue().stream().filter(b -> b.getOwn() != null && b.getOwn()).sorted(Comparator.comparing(Book::getSort))
                .map(b -> {
                    if (groupedMagazines.getValue().size() == 1) {
                        CalibreBook book = groupedMagazines.getValue().get(0);
                        return getAdditionalNotes(book) + b.getTextMore();
                    } else {
                        String imageTitle = b.getOfficialTitle() == null ? b.getTitle() : b.getOfficialTitle();
                        String imageAlt = b.getFileName() == null ? b.getTitle() : b.getFileName();
                        String image = String.format("<img style=\"vertical-align: middle;\" width=\"20\" height=\"20\" title=\"%s\" src=\"images/save.png\" alt=\"%s\" />\n", imageTitle, imageAlt);
                        String textShort = new TextShortRenderer(b, b.getCpu().equals(cpu) ? null : b.getCpu()).getTextShort();
                        String textMore = (k[0] == 1) ? "" : String.format("<h3>%s</h3>", b.getTitle()) + textShort;
                        textMore += "<span class=\"spoiler\" style=\"display: none;\">" + b.getTextMore() + "</span>";
                        String downloadLink = String.format("<p>%s<a href=\"%s\" target=\"_blank\"> Скачать %s</a></p><p><br /></p>", image, BookUtils.cloudStorageLink, b.getTitle());
                        /*String downloadLink = String.format("<p>%s<a href=\"up/media/%s/%s/%s.%s\" target=\"_blank\"> Скачать %s</a></p><p><br /></p>", image, category, b.getSeries().getName(),
                                b.getFileName() == null ? b.getTitle() : b.getFileName(), b.getDataList().get(0).getFormat().toLowerCase(), b.getTitle());*/
                        k[0]++;
                        return textMore + downloadLink;
                    }
                })
                .collect(joining("\n"));
    }

    protected static String getAdditionalNotes(CalibreBook book) {
        StringBuilder sb = new StringBuilder();
        if (book.getReleaseNote() != null && !book.getReleaseNote().isEmpty()) {
            sb.append(String.format("<p>%s</p>\n", book.getReleaseNote()));
        }
        if (!book.getType().equals(Type.COMICS)) {
            PlatformsTranslation translation = platformsTranslationMap.get(book.getType());
            if (book.getTags() != null && book.getTags().size() > 1) {
                sb.append(String.format(translation.getPlatforms(), getPlatforms(book.getTags())));
            }
            if (book.getAltTags() != null && !book.getAltTags().isEmpty()) {
                if (book.getTags() == null || book.getTags().size() == 1) {
                    sb.append(String.format(translation.getPlatforms(), getPlatforms(book.getTags())));
                }// else {
                    sb.append(String.format(translation.getAltPlatforms(), getAltPlatforms(book.getAltTags())));
                //}
            }
        }
        return sb.toString();
    }

    protected static String getPlatforms(List<Tag> tags) {
        return tags.stream().map(b -> BookUtils.getCategoryName(b.getName())).collect(joining(", "));
    }

    protected static String getAltPlatforms(List<CustomColumn> tags) {
        return tags.stream().map(b -> BookUtils.getCategoryName(b.getValue())).collect(joining(", "));
    }

    private static final String NEWS_FILE = "news.html";

    //TODO remove if not need more (table, 3 columns)
    private static void generateNewsPage(Collection<Video> addedBooks, Map<Video, List<Pair<String, Pair<String, String>>>> changedBooks) {
        StringBuilder sb = new StringBuilder();
        if (!addedBooks.isEmpty()) {
            sb.append("<h4>Добавленные книги:</h4>\n");
            sb.append("<ul>\n");
            addedBooks.forEach(b -> sb.append(String.format("<li><a href=\"%s\">%s</a></li>\n", generateBookViewUri(b.getCpu()), b.getTitle())));
            sb.append("</ul>\n");
        }
        if (!changedBooks.isEmpty()) {
            sb.append("<h4>Изменённые книги:</h4>\n");
            sb.append("<ul>\n");
            changedBooks.forEach((b, l) -> sb.append(String.format("<li><a href=\"%s\">%s</a></li>\n", generateBookViewUri(b.getCpu()), b.getTitle())));
            sb.append("</ul>\n");
        }
        Collection<Video> allBooks = new ArrayList<>(addedBooks);
        allBooks.addAll(new ArrayList<>(changedBooks.keySet()));
        sb.append("<br />\n");
        int counter = 1;
        sb.append("<p><table style=\"width:600px;\">\n");
        for (Video book : allBooks) {
            if (counter == 1) {
                sb.append("<tr>\n");
            }
            sb.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\">\n");
            String imageLink = generateBookViewUri(book.getCpu());
            String imageThumb = generateBookThumbUri(BookUtils.getCategoryById(book.getCategoryId()).getCatcpu(), book.getCpu());
            sb.append(String.format("<a href=\"%s\"><img style=\"border: 1px solid #aaaaaa;\" title=\"%s\" src=\"%s\" alt=\"%s\" /></a>\n", imageLink, book.getTitle(), imageThumb, book.getTitle()));
            sb.append("</td>\n");
            counter++;
            if (counter > 3) {
                sb.append("</tr><tr>\n");
                counter = 1;
            }
        }
        if (counter != 1) {
            for (int i = counter - 1; i <= 3; i++) {
                sb.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\"></td>\n");
            }
        }
        sb.append("</tr>\n");
        sb.append("</table></p>\n");
        // save
        File file = new File(Config.calibreDbPath + NEWS_FILE);
        FileUtils.backupFile(file);
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

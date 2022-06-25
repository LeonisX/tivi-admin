package md.leonis.tivi.admin.utils;

import javafx.util.Pair;
import md.leonis.tivi.admin.model.calibre.*;
import md.leonis.tivi.admin.model.danneo.Video;
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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static md.leonis.tivi.admin.utils.Config.sitePath;
import static md.leonis.tivi.admin.utils.StringUtils.*;

public class SiteRenderer {

    public static String cloudStorageLink;

    public static String generateSystemIconLink(String catCpu) {
        return String.format("images/systems/%s.png", catCpu);
    }

    public static String generateDownloadLink(String type, String category, String fileName) {
        return String.format("%s/up/media/%ss/%s/%s", sitePath, type, category, fileName);
    }

    public static String generateDownloadLink(String type, String category, String fileName, String ext) {
        return String.format("%s/up/media/%ss/%s/%s.%s", sitePath, type, category, fileName, ext);
    }

    //TODO enum const or delete this comment
    public static String generateSiteUri(CalibreBook book) {
        switch (book.getType()) {
            case "book":
            case "magazine":
            case "comics":
                return generateBookViewUri(book.getSiteCpu());
            case "guide":
            case "doc":
            case "manual":
            case "emulator":
                return generateBookGroupViewUri(BookUtils.getCategoryByTags(book), book.getType());
            default:
                throw new RuntimeException("Wrong book type: " + book.getType());
        }
    }

    public static String generateBookViewUri(String cpu) {
        return String.format("%s/media/open/%s.html", sitePath, cpu);
    }

    public static String generateBookGroupViewUri(String category, String type) {
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

    public static void forumGuideRenderer(List<CalibreBook> calibreBooks) {

        Map<String, List<CalibreBook>> books = calibreBooks.stream()
                .collect(Collectors.groupingBy(b -> {
                    String authors = b.getAuthors().stream().map(Author::getName).collect(joining(", ")).replace("|", ",");
                    if (authors.equalsIgnoreCase("неизвестный")) {
                        authors = b.getPublisher() != null ? b.getPublisher().getName() : "неизвестный";
                    }
                    return authors;
                }));

        books.entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().toLowerCase())).forEach(e -> {

            System.out.println(String.format("\n[b]%s[/b]", e.getKey()));

            List<String> lines = new ArrayList<>();

            e.getValue().forEach(b -> {

                List<String> sources = new ArrayList<>();
                List<String> links = new ArrayList<>();

                if (b.getExternalLink() != null && !b.getExternalLink().isEmpty()) {
                    if (b.getExternalLink().contains("showthread")) {
                        sources.add("Форум");
                    } else if (b.getExternalLink().contains("blog")) {
                        sources.add("Дневник");
                    } else if (b.getExternalLink().contains("wiki")) {
                        sources.add("База знаний");
                    } else {
                        sources.add("Другой источник");
                    }
                    links.add(b.getExternalLink());
                }

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

                List<String> capitalize = Arrays.asList("arcade", "saturn", "switch", "amiga", "lynx", "jaguar", "android", "dreamcast");
                Map<String, String> replace =  new HashMap<>();
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

                List<String> tags = b.getTags().stream().map(Tag::getName).map(t -> {
                    if (capitalize.contains(t)) {
                        return t.substring(0, 1).toUpperCase() + t.substring(1);
                    }
                    if (replace.containsKey(t)) {
                        return replace.get(t);
                    }
                    return t.toUpperCase();
                }).collect(toList());

                if (tags.size() > 4) {
                    tags = tags.subList(0, 4);
                    tags.add("...");
                }

                StringBuilder first = new StringBuilder(String.format("- [URL=\"%s\"]%s[/URL] (%s)", links.get(0), text, String.join(", ", tags)));
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

    public static String findFreeFileName(Set<String> fileNames, String fileName, String ext, int incr) {
        String result = FileUtils.prepareFileName(fileName, ext, incr);
        if (fileNames.contains(result)) {
            return findFreeFileName(fileNames, fileName, ext, ++incr);
        }
        fileNames.add(result);
        return result;
    }

    protected static String getTypeLink(String type) {
        switch (type.toLowerCase()) {
            case "7z":
            case "djvu":
            case "doc":
            case "docx":
            case "gif":
            case "jpg":
            case "pdf":
            case "png":
            case "psd":
            case "rar":
            case "rtf":
            case "tif":
            case "txt":
            case "xls":
            case "xlsx":
            case "zip":
            case "cbr":
            case "cbz":
                return String.format("images/books/type/%s.gif", type.toLowerCase());
            default:
                return "images/book-16.png";
        }
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return dateTime.plusHours(4).toLocalDate().format(formatter);
        }
    }

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
                        String textMore = (k[0] == 1) ? "" : String.format("<h3>%s</h3>", b.getTitle()) + getTextShort(b, b.getCpu().equals(cpu) ? null : b.getCpu());
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

    public static String getTextShort(CalibreBook book, String cpu) {
        StringBuilder sb = new StringBuilder();
        if (cpu != null) {
            String imageLink = generateBookCoverUri(BookUtils.getCategoryByTags(book), cpu);
            String imageThumb = generateBookThumbUri(BookUtils.getCategoryByTags(book), cpu);
            String imageTitle = book.getOfficialTitle() == null ? book.getTitle() : book.getOfficialTitle();
            String imageAlt = book.getFileName() == null ? book.getTitle() : book.getFileName();
            sb.append(String.format("<p><a href=\"%s\">", imageLink));
            sb.append(String.format("<img style=\"border: 1px solid #aaaaaa; float: right; margin-left: 10px; margin-top: 4px;\" title=\"%s\" src=\"%s\" alt=\"%s\" /></a></p>\n", imageTitle, imageThumb, imageAlt));
        }
        sb.append("<ul class=\"file-info\">\n");
        if (book.getOfficialTitle() != null && !book.getOfficialTitle().equals(book.getTitle())) {
            sb.append(String.format("<li><span>Название:</span> %s</li>\n", book.getOfficialTitle()));
        }
        if (book.getFileName() != null && !book.getFileName().equals(book.getTitle()) && !book.getFileName().equals(book.getOfficialTitle()) && !book.getType().equals(MAGAZINE)) {
            sb.append(String.format("<li><span>Неофициальное название:</span> %s</li>\n", book.getFileName()));
        }
        if (book.getSeries() != null) {
            //TODO may be number, link in future
            sb.append(String.format("<li><span>Серия:</span> %s</li>\n", book.getSeries().getName()));
        }
        if (book.getCompany() != null) {
            sb.append(String.format("<li><span>Компания:</span> %s</li>\n", book.getCompany()));
        }
        if (book.getAuthors() != null && !book.getAuthors().isEmpty() && !book.getAuthors().get(0).getName().equalsIgnoreCase("неизвестный")) {
            String title = book.getAuthors().size() > 1 || book.getAuthors().stream().map(Author::getName).collect(Collectors.joining()).contains("|") ? "ы" : "";
            sb.append(String.format("<li><span>Автор%s:</span> %s</li>\n", title, book.getAuthors().stream().map(Author::getName).collect(joining(", ")).replace("|", ",")));
        }
        if (book.getPublisher() != null && !book.getPublisher().getName().equals("???")) {
            sb.append(String.format("<li><span>Издательство:</span> %s</li>\n", /*book.getPublisher() == null ? "???" :*/ book.getPublisher().getName()));
        }
        if (book.getSignedInPrint() != null) {
            sb.append(String.format("<li><span>Подписано в печать:</span> %s г.</li>\n", formatDate(book.getSignedInPrint())));
        }
        if (book.getPages() != null && book.getPages() > 0) {
            sb.append(String.format("<li><span>Объём:</span> %s</li>\n", StringUtils.choosePluralMerge(book.getPages(), "страница", "страницы", "страниц")));
        }

        if (book.getIsbn() != null) {
            sb.append(String.format("<li><span>ISBN:</span> %s</li>\n", book.getIsbn()));
        }
        if (book.getBbk() != null) {
            sb.append(String.format("<li><span>ББК:</span> %s</li>\n", book.getBbk()));
        }
        if (book.getUdk() != null) {
            sb.append(String.format("<li><span>УДК:</span> %s</li>\n", book.getUdk()));
        }

        if (book.getEdition() != null && book.getEdition() > 0) {
            sb.append(String.format("<li><span>Тираж:</span> %s</li>\n", book.getEdition()));
        }
        if (book.getFormat() != null) {
            sb.append(String.format("<li><span>Формат:</span> %s</li>\n", book.getFormat()));
        }
        if (book.getScannedBy() != null) {
            sb.append(String.format("<li><span>Сканировал:</span> <a rel=\"nofollow\" target=\"_blank\" href=\"%s\">%s</a>\n", book.getSource(), book.getScannedBy()));
        }
        if (book.getPostprocessing() != null) {
            sb.append(String.format("<li><span>Постобработка:</span>%s\n", book.getPostprocessing()));
        }

        sb.append("</ul>\n");

        if (book.getTextShort() != null && !book.getTextShort().isEmpty()) {
            sb.append(book.getTextShort()).append("\n");
        }
        return sb.append(getAdditionalNotes(book)).toString();
    }

    private static String getAdditionalNotes(CalibreBook book) {
        StringBuilder sb = new StringBuilder();
        if (book.getReleaseNote() != null && !book.getReleaseNote().isEmpty()) {
            sb.append(String.format("<p>%s</p>\n", book.getReleaseNote()));
        }

        PlatformsTranslation translation = platformsTranslationMap.get(book.getType());
        if (book.getTags() != null && book.getTags().size() > 1) {
            String platforms = book.getTags().stream().map(b -> BookUtils.getCategoryName(b.getName())).collect(joining(", "));
            sb.append(String.format(translation.getPlatforms(), platforms));
        }
        if (book.getAltTags() != null && !book.getAltTags().isEmpty()) {
            String platforms = book.getAltTags().stream().map(b -> BookUtils.getCategoryName(b.getValue())).collect(joining(", "));
            if (book.getTags() == null || book.getTags().size() == 1) {
                sb.append(String.format(translation.getPlatforms(), platforms));
            } else {
                sb.append(String.format(translation.getAltPlatforms(), platforms));
            }
        }
        return sb.toString();
    }

    private static final String NEWS_FILE = "news.html";

    //TODO remove if not need more
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

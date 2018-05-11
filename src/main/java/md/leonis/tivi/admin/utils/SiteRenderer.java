package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.Video;
import md.leonis.tivi.admin.model.media.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.StringUtils.listTypeTranslationMap;
import static md.leonis.tivi.admin.utils.StringUtils.platformsTranslationMap;
import static md.leonis.tivi.admin.utils.StringUtils.viewTypeTranslationMap;

public class SiteRenderer {

    public static String getSystemIconLink(String catCpu) {
        return String.format("images/systems/%s.png", catCpu);
    }

    public static String getDownloadLink(CalibreBook calibreBook, String category, Data data) {
        return String.format("up/media/%ss/%s/%s", calibreBook.getType(), category, data.getFileName());
    }

    public static void generateManualsPage(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks, String type) {
        TypeTranslation translation = listTypeTranslationMap.get(type);
        List<CalibreBook> calibreBooks = allCalibreBooks.stream().filter(b -> b.getType().equals(type)).collect(toList());
        calibreBooks = calibreBooks.stream().filter(b -> b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)).sorted(Comparator.comparing(Book::getTitle)).collect(toList());
        if (calibreBooks.isEmpty()) {
            return;
        }
        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(category + "_" + translation.getPlural())).findFirst();
        if (!manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu(category + "_" + translation.getPlural());
            newManual.setCategoryId(BookUtils.getCategoryByCpu(category).getCatid());
            setManualText(calibreBooks, newManual, category, translation);
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else {
            // change
            Video newManual = new Video(manual.get());
            setManualText(calibreBooks, newManual, category, translation);
            oldBooks.add(newManual);
        }
    }

    public static void generateCitationsPage(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        List<CalibreBook> calibreBooks = allCalibreBooks.stream().filter(b -> b.getType().equals("book"))
                .filter(b -> b.getOwn() != null && b.getOwn()).collect(toList());

        calibreBooks = calibreBooks.stream().filter(b -> b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)).sorted(Comparator.comparing(Book::getSort)).collect(toList());

        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(category + "_citation")).findFirst();
        if (!calibreBooks.isEmpty() && !manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu(category + "_citation");
            newManual.setCategoryId(BookUtils.getCategoryByCpu(category).getCatid());
            newManual.setTitle("Упоминания в других книгах");
            newManual.setText(String.format("<p>В этих книгах так же можно найти информацию об играх для %s.</p>", BookUtils.getCategoryName(category)));
            StringBuilder sb = new StringBuilder();
            sb.append("<ul class=\"file-info\">\n");
            calibreBooks.forEach(b -> sb.append(String.format("<li><a href=\"media/open/%s.html\">%s</a></li>", b.getCpu(), b.getTitle())));
            sb.append("</ul>\n");
            newManual.setFullText(sb.toString());
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else if (!calibreBooks.isEmpty()) {
            // change
            Video newManual = new Video(manual.get());
            newManual.setText(String.format("<p>В этих книгах так же можно найти информацию об играх для %s.</p>", BookUtils.getCategoryName(category)));
            StringBuilder sb = new StringBuilder();
            sb.append("<ul class=\"file-info\">\n");
            calibreBooks.forEach(b -> sb.append(String.format("<li><a href=\"media/open/%s.html\">%s</a></li>", b.getCpu(), b.getTitle())));
            sb.append("</ul>\n");
            newManual.setFullText(sb.toString());
            oldBooks.add(newManual);
        }
    }

    public static void generateSearchPage(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        List<CalibreBook> calibreBooks = allCalibreBooks.stream().filter(b -> b.getType().equals("book")).filter(b -> b.getOwn() == null || !b.getOwn()).collect(toList());
        calibreBooks = calibreBooks.stream().filter(b ->
                b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                        (b.getAltTags() != null && b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)) ||
                        (b.getReleaseNote() != null && !b.getReleaseNote().isEmpty())).sorted(Comparator.comparing(Book::getSort)).collect(toList());
        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(category + "_search")).findFirst();
        if (!calibreBooks.isEmpty() && !manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu(category + "_search");
            newManual.setCategoryId(BookUtils.getCategoryByCpu(category).getCatid());
            newManual.setTitle("Книги в поиске");
            newManual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже книг.</p>");
            newManual.setFullText(generateTableView(calibreBooks));
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else if (!calibreBooks.isEmpty()) {
            // change
            Video newManual = new Video(manual.get());
            newManual.setTitle("Книги в поиске");
            newManual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже книг.</p>");
            newManual.setFullText(generateTableView(calibreBooks));
            oldBooks.add(newManual);
        }
    }

    public static void generateMagazinesPage(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks, String type) {
        TypeTranslation translation = viewTypeTranslationMap.get(type);
        List<CalibreBook> calibreBooks = allCalibreBooks.stream().filter(b -> b.getType().equals(type)).filter(b -> b.getOwn() != null && b.getOwn()).collect(toList());
        Map<String, List<CalibreBook>> books = calibreBooks.stream().filter(b ->
                b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                        (b.getAltTags() != null && b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)))
                .peek(b -> {
                    if (b.getSeries() == null) {
                        b.setSeries(new PublisherSeries(0L, b.getTitle(), ""));
                    }
                })
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()));
        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(category + "_" + translation.getPlural())).findFirst();
        if (!books.isEmpty() && !manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu(category + "_" + translation.getPlural());
            newManual.setCategoryId(BookUtils.getCategoryByCpu(category).getCatid());
            setMagazineText(books, newManual, category, translation);
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else if (!books.isEmpty()) {
            // change
            Video newManual = new Video(manual.get());
            setMagazineText(books, newManual, category, translation);
            oldBooks.add(newManual);
        }
    }

    public static void generateMagazinesSearchPage(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        List<CalibreBook> calibreMagazines = allCalibreBooks.stream().filter(b -> b.getType().equals("magazine") && !category.equals("gd"))
                //.filter(b -> b.getTags().stream().map(Tag::getName).collect(toList()).contains(category))
                .filter(b -> b.getOwn() == null || !b.getOwn()).sorted(Comparator.comparing(Book::getSort)).collect(toList());

        Map<CalibreBook, List<CalibreBook>> groupedMagazines = calibreMagazines.stream()//.filter(b ->
                //b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                //        (b.getAltTags() != null && b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)))
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()))
                .entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue().get(0), Map.Entry::getValue))
                .entrySet().stream().sorted(Comparator.comparing(e -> e.getKey().getSeries().getName())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals("magazines_in_search")).findFirst();
        if (!groupedMagazines.isEmpty() && !manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu("magazines_in_search");
            newManual.setCategoryId(BookUtils.getCategoryByCpu(category).getCatid());
            newManual.setTitle("Разыскиваемые журналы");
            newManual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже журналов.</p>");
            StringBuilder sb = new StringBuilder();
            //TODO link
            //TODO table with images
            groupedMagazines.forEach((key, value) -> {
                sb.append(String.format("<h3>%s</h3>", key.getSeries().getName()));
                sb.append("<ul class=\"file-info\">\n");
                value.forEach(c -> sb.append(String.format("<li>%s</li>", c.getTitle())));
                sb.append("</ul>\n");
            });
            newManual.setFullText(sb.toString());
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else if (!groupedMagazines.isEmpty()) {
            // change
            Video newManual = new Video(manual.get());
            newManual.setTitle("Разыскиваемые журналы");
            newManual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже журналов.</p>");
            StringBuilder sb = new StringBuilder();
            //TODO link
            //TODO table with images
            groupedMagazines.forEach((key, value) -> {
                sb.append(String.format("<h3>%s</h3>", key.getSeries().getName()));
                sb.append("<ul class=\"file-info\">\n");
                value.forEach(c -> sb.append(String.format("<li>%s</li>", c.getTitle())));
                sb.append("</ul>\n");
            });
            newManual.setFullText(sb.toString());
            oldBooks.add(newManual);
        }
    }

    private static void setMagazineText(Map<String, List<CalibreBook>> books, Video manual, String category, TypeTranslation translation) {
        Declension declension = StringUtils.getDeclension(BookUtils.getCategoryName(category));
        manual.setTitle(String.format(translation.getShortText(), declension.getRod()));
        manual.setText(String.format(translation.getText(), declension.getRod()));
        StringBuilder sb = new StringBuilder();
        sb.append("<ul class=\"file-info\">\n");
        new TreeMap<>(books).forEach((key, value) -> sb.append(String.format("<li><a href=\"media/open/%s.html\">%s</a></li>", BookUtils.generateCpu(key), key)));
        sb.append("</ul>\n");
        manual.setFullText(sb.toString());
    }

    private static String generateTableView(List<CalibreBook> books) {
        int counter = 1;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbi = new StringBuilder();
        StringBuilder sbt = new StringBuilder();
        sb.append("<p><table style=\"width:600px;\">");
        for (CalibreBook book : books) {
            if (counter == 1) {
                sb.append("<tr>");
            }
            sbi.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\">");
            sbt.append("<td style=\"text-align:center; padding-top: 5px; padding-bottom: 10px;\">");
            if (book.getHasCover() != 0) {
                String imageLink = String.format("images/books/cover/%s/%s.jpg", BookUtils.getCategoryByTags(book), book.getCpu());
                String imageThumb = String.format("images/books/thumb/%s/%s.jpg", BookUtils.getCategoryByTags(book), book.getCpu());
                String imageTitle = book.getOfficialTitle() == null ? book.getTitle() : book.getOfficialTitle();
                String imageAlt = book.getFileName() == null ? book.getTitle() : book.getFileName();
                sbi.append(String.format("<a href=\"%s\"><img style=\"border: 1px solid #aaaaaa;\" title=\"%s\" src=\"%s\" alt=\"%s\" /></a>", imageLink, imageTitle, imageThumb, imageAlt));
            } else {
                String imageThumb = "images/books/nocover.png";
                String imageTitle = book.getOfficialTitle() == null ? book.getTitle() : book.getOfficialTitle();
                String imageAlt = book.getFileName() == null ? book.getTitle() : book.getFileName();
                sbi.append(String.format("<img style=\"border: 1px solid #aaaaaa;\" title=\"%s\" src=\"%s\" alt=\"%s\" />", imageTitle, imageThumb, imageAlt));
            }
            sbt.append(book.getTitle());
            sbi.append("</td>");
            sbt.append("</td>");
            counter++;
            if (counter > 3) {
                sbi.append("</tr><tr>");
                sbt.append("</tr>");
                sb.append(sbi).append(sbt);
                sbi = new StringBuilder();
                sbt = new StringBuilder();
                counter = 1;
            }
        }
        if (counter != 1) {
            for (int i = counter - 1; i <= 3; i++) {
                sbi.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\"></td>");
                sbt.append("<td style=\"text-align:center;\"></td>");
            }
        }
        if (!sbi.toString().isEmpty()) {
            sb.append(sbi).append("</tr>").append(sbt).append("</tr>");
        }
        sb.append("</table></p>");
        return sb.toString();
    }


    //TODO renderer for guides
    private static void setManualText(List<CalibreBook> calibreBooks, Video manual, String category, TypeTranslation translation) {
        if (translation.getPlural().equals("guides")) {
            guideRenderer(calibreBooks, manual, category, translation);
        }  else {
            manualRenderer(calibreBooks, manual, category, translation);
        }
    }

    private static void manualRenderer(List<CalibreBook> calibreBooks, Video manual, String category, TypeTranslation translation) {
        String catName = BookUtils.getCategoryByCpu(category).getCatname();
        Declension declension = StringUtils.getDeclension(catName);
        manual.setTitle(translation.getShortText() + " " + declension.getRod());

        CalibreBook book = calibreBooks.stream().filter(cb -> cb.getHasCover() != 0).findFirst().get();
        String imageLink = String.format("images/books/thumb/%s/%s.jpg", BookUtils.getCategoryByTags(book), book.getCpu());

        manual.setText(String.format("<p><img style=\"border: 1px solid #aaaaaa; float: right; margin: 5px;\" title=\"%s\" src=\"%s\" alt=\"%s\" />%s %s</p>",
                translation.getImageTitle() + catName, imageLink, translation.getImageAlt() + declension.getRod(), translation.getShortText(), declension.getRod()));

        manual.setFullText(calibreBooks.stream().map(b -> {
            String authors = b.getAuthors().stream().map(Author::getName).collect(joining(", ")).replace("|", ",");
            if (authors.equalsIgnoreCase("неизвестный")) {
                authors = b.getPublisher() != null ? b.getPublisher().getName() : "";
            }
            String downloadLink = b.getDataList().isEmpty() ? "" :
                    String.format("<a href=\"up/media/%s/%s/%s.%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/book.png\" alt=\"download\" /></a>", translation.getPlural(), category,
                            b.getFileName() != null ? b.getFileName() : b.getTitle(), b.getDataList().get(0).getFormat().toLowerCase());
            String externalLink = b.getExternalLink() == null || b.getExternalLink().isEmpty() ? ""
                    : String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/page.png\" alt=\"download\" /></a> ", b.getExternalLink());
            String text = b.getTextShort().isEmpty() ? b.getTextMore() : b.getTextShort();
            String spoiler = "";
            if (!text.equals(b.getTextMore()) && !b.getTextMore().isEmpty()) {
                spoiler += "<br /><span class=\"spoiler\" style=\"display: none;\">" + b.getTextMore() + "</span>";
            }
            text = trimHtmlTags(text.replace("\n", ""));
            String br = text.length() < 108 && spoiler.isEmpty() ? "<br /><br />" : "";
            return String.format("<p>%s%s%s (C) %s, %s%s%s</p>",
                    externalLink, downloadLink, text, authors, formatDate(b.getSignedInPrint()), br, spoiler);
        }).collect(joining()));
    }

    //TODO remove code duplicates
    private static void guideRenderer(List<CalibreBook> calibreBooks, Video manual, String category, TypeTranslation translation) {
        String catName = BookUtils.getCategoryByCpu(category).getCatname();
        Declension declension = StringUtils.getDeclension(catName);
        manual.setTitle(translation.getShortText() + " " + declension.getRod());

        CalibreBook book = calibreBooks.stream().filter(cb -> cb.getHasCover() != 0).findFirst().get();
        //TODO need???
        String imageLink = String.format("images/books/thumb/%s/%s.jpg", BookUtils.getCategoryByTags(book), book.getCpu());

        manual.setText(String.format("<p><img style=\"border: 1px solid #aaaaaa; float: right; margin: 5px;\" title=\"%s\" src=\"%s\" alt=\"%s\" />%s %s</p>",
                translation.getImageTitle() + catName, imageLink, translation.getImageAlt() + declension.getRod(), translation.getShortText(), declension.getRod()));

        String fulltext = "<div class=\"tab-guides\">" +
                "<table><thead><tr>" +
                "<td class=\"col-n\">Название игры</td>" +
                "<td class=\"col-u\"><img src=\"images/pages.png\" width=\"16\" height=\"16\"></td>" +
                "<td class=\"col-y\">Автор</td>" +
                "<td class=\"col-y\">Дата</td>" +
                "</tr></thead><tbody>";

        boolean[] k = new boolean[1];
        k[0] = false;
        manual.setFullText(fulltext + calibreBooks.stream().sorted(Comparator.comparing(Book::getSort)).map(b -> {
            String authors = b.getAuthors().stream().map(Author::getName).collect(joining(", ")).replace("|", ",");
            if (authors.equalsIgnoreCase("неизвестный")) {
                authors = b.getPublisher() != null ? b.getPublisher().getName() : "";
            }
            String downloadLink = b.getDataList().isEmpty() ? "" :
                    String.format("<a href=\"up/media/%s/%s/%s.%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/book-16.png\" alt=\"download\" /></a>", translation.getPlural(), category,
                            b.getFileName() != null ? b.getFileName() : b.getTitle(), b.getDataList().get(0).getFormat().toLowerCase());
            String externalLink = b.getExternalLink() == null || b.getExternalLink().isEmpty() ? ""
                    : String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/page-16.png\" alt=\"download\" /></a> ", b.getExternalLink());
            String text = b.getTextShort().isEmpty() ? b.getTextMore() : b.getTextShort();
            /*String spoiler = "";
            if (!text.equals(b.getTextMore()) && !b.getTextMore().isEmpty()) {
                spoiler += "<br /><span class=\"spoiler\" style=\"display: none;\">" + b.getTextMore() + "</span>";
            }*/
            String spoiler = "";
            if (!text.equals(b.getTextMore()) && !b.getTextMore().isEmpty()) {
                spoiler = " " + b.getTextMore();
            }
            text = trimHtmlTags(text.replace("\n", ""));
            String className = k[0] ? "odd" : "";
            k[0] = !k[0];
            String pages = b.getPages() == null ? "-" : b.getPages().toString();
            return String.format("<tr class=\"%s\"><td class=\"col-n\">%s%s%s%s</td><td class=\"col-u\">%s</td><td class=\"col-y\">%s</td><td class=\"col-y\">%s</td></td></tr>",
                    className, externalLink, downloadLink, text, spoiler, pages, authors, formatDate(b.getSignedInPrint()));
        }).collect(joining()) + "</tr></tbody></table></div>");
        manual.setFullText(fixLatinChars(manual.getFullText()));
    }

    //TODO probably for books, magazines too
    //TODO probably for vBulletin renderers too, need to check
    private static String fixLatinChars(String fullText) {
        fullText = fullText.replace("ü", "&#xfc;");
        fullText = fullText.replace("ō", "&#x14d;");
        fullText = fullText.replace("ū", "&#x16b;");
        return fullText;
    }

    private static String trimHtmlTags(String text) {
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

    private static String formatDate(LocalDateTime dateTime) {
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
            String imageLink = String.format("images/books/cover/%s/%s.jpg", BookUtils.getCategoryByTags(book), cpu);
            String imageThumb = String.format("images/books/thumb/%s/%s.jpg", BookUtils.getCategoryByTags(book), cpu);
            String imageTitle = book.getOfficialTitle() == null ? book.getTitle() : book.getOfficialTitle();
            String imageAlt = book.getFileName() == null ? book.getTitle() : book.getFileName();
            sb.append(String.format("<p><a href=\"%s\">", imageLink));
            sb.append(String.format("<img style=\"border: 1px solid #aaaaaa; float: right; margin-left: 10px; margin-top: 4px;\" title=\"%s\" src=\"%s\" alt=\"%s\" /></a></p>\n", imageTitle, imageThumb, imageAlt));
        }
        sb.append("<ul class=\"file-info\">\n");
        if (book.getOfficialTitle() != null && !book.getOfficialTitle().equals(book.getTitle())) {
            sb.append(String.format("<li><span>Название:</span> %s</li>\n", book.getOfficialTitle()));
        }
        if (book.getFileName() != null && !book.getFileName().equals(book.getTitle()) && !book.getFileName().equals(book.getOfficialTitle())) {
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
            sb.append(String.format("<li><span>Сканировал:</span> <a rel=\"nofollow\" href=\"%s\">%s</a>\n", book.getSource(), book.getScannedBy()));
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

}

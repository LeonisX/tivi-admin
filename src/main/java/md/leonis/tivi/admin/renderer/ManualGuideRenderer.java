package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.*;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.FileUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;
import md.leonis.tivi.admin.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.model.Type.*;
import static md.leonis.tivi.admin.utils.Config.sitePath;
import static md.leonis.tivi.admin.utils.StringUtils.*;

public class ManualGuideRenderer extends SiteRenderer {

    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;
    private final Type type;

    private final List<CalibreBook> calibreBooks;
    private final TypeTranslation translation;
    private final Declension declension;

    public ManualGuideRenderer(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks, Type type) {
        this.filteredSiteBooks = filteredSiteBooks;
        this.category = category;
        this.addedBooks = addedBooks;
        this.oldBooks = oldBooks;
        this.type = type;

        this.calibreBooks = allCalibreBooks.stream()
                .filter(b -> b.getType().equals(type))
                .filter(b -> b.belongsToCategory(category) || b.mentionedInCategory(category))
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(toList());
        this.translation = typeTranslationMap.get(type);
        this.declension = StringUtils.getDeclension(BookUtils.getCategoryName(category));
    }

    public void generateManualsPage() {
        if (calibreBooks.isEmpty()) {
            return;
        }
        String cpu = generateCpu();
        Optional<Video> manual = filteredSiteBooks.stream().filter(b -> b.getCpu().equals(cpu)).findFirst();
        if (manual.isPresent()) {
            // change
            Video newManual = new Video(manual.get());
            renderTexts(newManual);
            oldBooks.add(newManual); //TODO это странно, зачем добавлять, а не заменять существующий??? Это надо протестировать
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

    private String generateCpu() {
        return category + "_" + typeTranslationMap.get(type).getPlural();
    }

    private void renderTexts(Video manual) {
        manual.setTitle(generateTitle());
        manual.setText(generateText());
        manual.setFullText(generateFullText());
    }

    private String generateTitle() {
        return translation.getShortText() + " " + declension.getRod();
    }

    //TODO html
    private String generateText() {
        String image = "manuals";
        if (type.equals(GUIDE)) {
            image = "solutions";
        } else if (type.equals(EMULATOR)) {
            image = "emulators";
        } else if (type.equals(DOC)) {
            image = "documents";
        }
        return SiteRenderer.generateHeaderImage(type, category, String.format("%s %s.", translation.getShortText().substring(PR.length() - 1), declension.getRod()), image);
    }

    private String generateFullText() {
        if (type.equals(MANUAL) || type.equals(DOC)) {
            return renderManualFullText();
        } else if (type.equals(GUIDE) || type.equals(EMULATOR)) {
            return renderGuideFullText();
        } else {
            throw new RuntimeException("Wrong type: " + type);
        }
    }

    //TODO html
    private String renderManualFullText() {
        return calibreBooks.stream().map(b -> {
            String authors = b.getAuthors().stream().map(Author::getName).collect(joining(", ")).replace("|", ",");
            if (authors.equalsIgnoreCase("неизвестный")) {
                authors = b.getPublisher() == null ? "" : b.getPublisher().getName();
            }
            String downloadLink = b.getDataList().isEmpty() ? "" :
                    String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/book.png\" alt=\"download\" target=\"_blank\" /></a>\n", cloudStorageLink);
            /*String downloadLink = b.getDataList().isEmpty() ? "" :
                    String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/book.png\" alt=\"download\" /></a>",
                            generateDownloadLink(translation.getPlural(), category, b.getFileName() != null ? b.getFileName() : b.getTitle()), b.getDataList().get(0).getFormat().toLowerCase());*/
            String externalLink = b.getExternalLink() == null || b.getExternalLink().isEmpty() ? ""
                    : String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/page.png\" alt=\"download\" /></a>\n", b.getExternalLink());
            String text = b.getTextShort().isEmpty() ? b.getTextMore() : b.getTextShort();
            String spoiler = "";
            if (!text.equals(b.getTextMore()) && !b.getTextMore().isEmpty()) {
                spoiler += "<br /><span class=\"spoiler\" style=\"display: none;\">" + b.getTextMore() + "</span>\n";
            }
            text = trimHtmlTags(text.replace("\n", "").replace("\r", ""));
            String br = text.length() < 108 && spoiler.isEmpty() ? "<br /><br />" : "";
            String author = authors.isEmpty() ? "" : String.format("%s,", authors);
            return String.format("<p>%s%s%s (C) %s,%s%s%s</p>\n",
                    externalLink, downloadLink, text, author, formatDate(b.getSignedInPrint()), br, spoiler);
        }).collect(joining());
    }

    //TODO html
    private String renderGuideFullText() {
        String fulltext = "<div class=\"tab-guides\" style=\"display: inline-block\">" +
                "<table><thead><tr>" +
                "<td class=\"col-n\">Название</td>" +
                "<td class=\"col-u\"><img src=\"images/pages.png\" width=\"16\" height=\"16\"></td>" +
                "<td class=\"col-y\">Автор</td>" +
                "<td class=\"col-y\">Дата</td>" +
                "</tr></thead><tbody>";

        boolean[] k = new boolean[1];
        fulltext = fulltext + calibreBooks.stream().sorted(Comparator.comparing(Book::getSort)).map(b -> {
            String authors = b.getAuthors().stream().map(Author::getName).collect(joining(", ")).replace("|", ",");
            if (authors.equalsIgnoreCase("неизвестный")) {
                authors = b.getPublisher() == null ? "" : b.getPublisher().getName();
            }
            Set<String> fileNames = new HashSet<>();
            String downloadLink = b.getDataList().isEmpty() ? "" :
                    b.getDataList().stream().map(d -> {
                        String fileName = FileUtils.findFreeFileName(fileNames, b.getFileName() != null ? b.getFileName() : b.getTitle(), d.getFormat().toLowerCase(), 0);
                        return String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"%s\" alt=\"download\" target=\"_blank\"/></a>",
                                generateDownloadLink(type, BookUtils.getCategoryByTags(b), fileName), getTypeLink(d.getFormat()));
                    }).collect(Collectors.joining(" "));
            String externalLink = b.getExternalLink() == null || b.getExternalLink().isEmpty() ? ""
                    : String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/page-16.png\" alt=\"download\" target=\"_blank\" /></a> ", b.getExternalLink());
            String text = b.getTextShort().isEmpty() ? b.getTextMore() : b.getTextShort();
            String spoiler = "";
            if (!text.equals(b.getTextMore()) && !b.getTextMore().isEmpty()) {
                spoiler += " <span class=\"spoiler\" style=\"display: none;\"><br />"
                        + "<div style=\"padding-left: 20px; padding-top: 10px;\">" + b.getTextMore() + "</div></span>";
            }
            text = trimHtmlTags(text.replace("\n", "").replace("\r", ""));
            String className = k[0] ? "odd" : "";
            k[0] = !k[0];
            String pages = b.getPages() == null ? "-" : b.getPages().toString();
            return String.format("<tr class=\"%s\"><td class=\"col-n\">%s%s%s%s</td><td class=\"col-u\">%s</td><td class=\"col-y\">%s</td><td class=\"col-y\">%s</td></td></tr>",
                    className, externalLink, downloadLink, text, spoiler, pages, authors, formatDate(b.getSignedInPrint()));
        }).collect(joining()) + "</tr></tbody></table></div>";
        return fixLatinChars(fulltext);
    }

    private String getTypeLink(String type) {
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
}

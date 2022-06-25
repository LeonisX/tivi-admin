package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.calibre.*;
import md.leonis.tivi.admin.model.danneo.Video;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;
import md.leonis.tivi.admin.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.Config.sitePath;
import static md.leonis.tivi.admin.utils.StringUtils.*;

public class ManualGuideRenderer extends SiteRenderer {

    private final List<Video> filteredSiteBooks;
    private final String category;
    private final Collection<Video> addedBooks;
    private final List<Video> oldBooks;
    private final String type;

    private final List<CalibreBook> calibreBooks;
    private final TypeTranslation translation;
    private final String categoryName;
    private final Declension declension;

    public ManualGuideRenderer(List<CalibreBook> allCalibreBooks, List<Video> filteredSiteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks, String type) {
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
        this.translation = listTypeTranslationMap.get(type);
        this.categoryName = BookUtils.getCategoryName(category);
        this.declension = StringUtils.getDeclension(categoryName);
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
        return category + "_" + listTypeTranslationMap.get(type).getPlural();
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
        CalibreBook book = calibreBooks.stream().filter(cb -> cb.getHasCover() != 0).findFirst().orElseThrow(() -> new RuntimeException("All books w/o covers!"));
        String imageLink = generateBookThumbUri(BookUtils.getCategoryByTags(book), book.getCpu());

        return String.format("<p><img style=\"border: 1px solid #aaaaaa; float: right; margin: 5px;\" title=\"%s\" src=\"%s\" alt=\"%s\" />%s %s</p>",
                translation.getImageTitle() + " " + categoryName, imageLink, translation.getImageAlt() + " " + declension.getRod(), translation.getShortText(), declension.getRod());
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
                    String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/book.png\" alt=\"download\" target=\"_blank\" /></a>", cloudStorageLink);
            /*String downloadLink = b.getDataList().isEmpty() ? "" :
                    String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/book.png\" alt=\"download\" /></a>",
                            generateDownloadLink(translation.getPlural(), category, b.getFileName() != null ? b.getFileName() : b.getTitle()), b.getDataList().get(0).getFormat().toLowerCase());*/
            String externalLink = b.getExternalLink() == null || b.getExternalLink().isEmpty() ? ""
                    : String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/page.png\" alt=\"download\" /></a> ", b.getExternalLink());
            String text = b.getTextShort().isEmpty() ? b.getTextMore() : b.getTextShort();
            String spoiler = "";
            if (!text.equals(b.getTextMore()) && !b.getTextMore().isEmpty()) {
                spoiler += "<br /><span class=\"spoiler\" style=\"display: none;\">" + b.getTextMore() + "</span>";
            }
            text = trimHtmlTags(text.replace("\n", "").replace("\r", ""));
            String br = text.length() < 108 && spoiler.isEmpty() ? "<br /><br />" : "";
            String author = authors.isEmpty() ? "" : String.format("%s,", authors);
            return String.format("<p>%s%s%s (C) %s,%s%s%s</p>",
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
                        String fileName = findFreeFileName(fileNames, b.getFileName() != null ? b.getFileName() : b.getTitle(), d.getFormat().toLowerCase(), 0);
                        return String.format("<a href=\"%s\"><img style=\"float: left; margin-right: 5px;\" src=\"%s\" alt=\"download\" target=\"_blank\"/></a>",
                                generateDownloadLink(translation.getPlural(), BookUtils.getCategoryByTags(b), fileName), getTypeLink(d.getFormat()));
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
}

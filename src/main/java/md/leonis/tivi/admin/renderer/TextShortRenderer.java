package md.leonis.tivi.admin.renderer;

import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.Author;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.utils.BookUtils;
import md.leonis.tivi.admin.utils.SiteRenderer;
import md.leonis.tivi.admin.utils.StringUtils;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static md.leonis.tivi.admin.model.Type.MAGAZINE;

public class TextShortRenderer extends SiteRenderer {

    private final CalibreBook book;
    private final String cpu;

    public TextShortRenderer(CalibreBook book) {
        this(book, book.getCpu());
    }

    public TextShortRenderer(CalibreBook book, String cpu) {
        this.book = book;
        this.cpu = cpu;
    }

    //TODO html
    public String getTextShort() {
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
            if (!book.getType().equals(Type.COMICS)) {
                sb.append(String.format("<li><span>Серия:</span> %s</li>\n", book.getSeries().getName()));
            }
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
            sb.append(String.format("<li><span>Сканирование:</span> <a rel=\"nofollow\" target=\"_blank\" href=\"%s\">%s</a>\n", book.getSource(), book.getScannedBy()));
        }
        if (book.getPostprocessing() != null) {
            sb.append(String.format("<li><span>Обработка:</span>%s\n", book.getPostprocessing()));
        }

        if (book.getType().equals(Type.COMICS)) {
            sb.append(String.format("<li><span>Платформы:</span> %s</li>\n", SiteRenderer.getPlatforms(book.getTags())));
        }

        sb.append("</ul>\n");

        if (book.getTextShort() != null && !book.getTextShort().isEmpty()) {
            sb.append(book.getTextShort()).append("\n");
        }
        return sb.append(getAdditionalNotes(book)).toString();
    }
}

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
    private final String category;

    public TextShortRenderer(CalibreBook book, String category) {
        this(book, book.getCpu(), category);
    }

    public TextShortRenderer(CalibreBook book, String cpu, String category) {
        this.book = book;
        this.cpu = cpu;
        this.category = category;
    }

    //TODO html
    public String getTextShort(boolean isFirst) {
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

        //if (book.getType().equals(MAGAZINE)) {
        if ((isFirst && book.getGroup() != null) || (isFirst && book.getType().equals(MAGAZINE))) {
            sb.append(String.format("<li><strong>%s</strong></li>\n", book.getTitle()));
        }
        if (book.getOwn() == null || !book.getOwn()) {
            sb.append("<li><span>Статус:</span> <strong style=\"color:red\"> В розыске</strong></li>\n");
        }
        //}

        if (!book.getLanguages().isEmpty() && !book.getLanguages().get(0).getCode().equals("rus")) {
            String lang;
            switch (book.getLanguages().get(0).getCode()) {
                case "eng": lang = "Английский";
                break;
                case "deu": lang = "Немецкий";
                    break;
                case "jpn": lang = "Японский";
                    break;
                case "zho": lang = "Китайский";
                    break;
                case "fra": lang = "Французский";
                    break;
                case "spa": lang = "Испанский";
                    break;
                default: lang = "Иностранный";
                    break;
            }
            sb.append(String.format("<li><span>Язык:</span> %s</li>\n", lang));
        }

        if (book.getOfficialTitle() != null && !book.getOfficialTitle().equals(book.getTitle())) {
            sb.append(String.format("<li><span>Название:</span> %s</li>\n", book.getOfficialTitle()));
        }

        if (book.getFileName() != null && !book.getFileName().equals(book.getTitle()) && !book.getFileName().equals(book.getOfficialTitle()) && !book.getType().equals(MAGAZINE)) {
            sb.append(String.format("<li><span>Неофициальное название:</span> %s</li>\n", book.getFileName()));
        }
        if (book.getSeries() != null) {
            //TODO may be number, link in future

            //TODO включить серию+ссылку для журналов после разгруппировки
            if (!book.getType().equals(Type.COMICS) && !book.getType().equals(MAGAZINE)) {
                sb.append(String.format("<li><span>Серия:</span> %s</li>\n", book.getSeries().getName()));
            } else if (book.getGroup() != null) {
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
            String scannedBy = book.getSource() == null ? book.getScannedBy()
                    : String.format("<a rel=\"nofollow\" target=\"_blank\" href=\"%s\">%s</a>", book.getSource(), book.getScannedBy());
            String word = book.getPostprocessing() == null ? "Сканирование и обработка" : "Сканирование";
            sb.append(String.format("<li><span>%s:</span> %s\n", word, scannedBy));
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
        return sb.append(getAdditionalNotes(book, category)).toString();
    }
}

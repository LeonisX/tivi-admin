package md.leonis.tivi.admin.model.template;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@NoArgsConstructor
@EqualsAndHashCode
@lombok.Data
public class ModelBook {

    // List<String> fields = Arrays.asList("id", "title", "sort", "serieIndex", "hasCover", "lastModified", "textShort",
    //                "textMore", "comment", "releaseNote", "authors", "languages", "publisher", "rating", "series", "tags", "altTags",
    //                "isbn", "bbk", "format", "source", "officialTitle", "type", "company", "udk", "edition", "postprocessing",
    //                "signedInPrint", "fileName", "externalLink", "scannedBy", "pages", "own");

    private Long id;
    private String title;
    private String sort;
    private Double serieIndex;

    private boolean hasCover;
    private LocalDateTime lastModified;

    private String textShort;
    private String textMore;
    private String comment;
    private String releaseNote;

    private String authors;
    private String languages;
    private String publisher;
    private Long rating;
    private String series;
    private String tags;
    private String altTags;

    private String isbn;
    private String bbk;
    private String format;
    private String source;
    private String officialTitle;
    private Type type;
    private String company;
    private String udk;
    private Integer edition; // тираж
    private String postprocessing;
    private LocalDateTime signedInPrint;
    private String fileName;
    private String externalLink;
    private String scannedBy;
    private Integer pages;
    private Boolean own;

    private Long tiviId;
    private String cpu;

    // вычисляемые значения, эти значения нужны для генерации страниц на сайте
    private transient String siteCpu;
    private transient String siteUri;
    private transient String siteThumbUri;

    public ModelBook(CalibreBook book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.sort = book.getSort();
        this.serieIndex = book.getSerieIndex();

        this.hasCover = book.getHasCover() == 1;
        this.lastModified = book.getLastModified();

        this.textShort = book.getTextShort();
        this.textMore = book.getTextMore();
        this.comment = book.getComment();
        this.releaseNote = book.getReleaseNote();

        this.authors = book.getAuthors() == null ? "" : book.getAuthors().stream().map(Author::getName).collect(Collectors.joining(", "));
        this.languages = book.getLanguages() == null ? "" : book.getLanguages().stream().map(Language::getCode).collect(Collectors.joining(", "));
        this.publisher = book.getPublisher() == null ? "" : book.getPublisher().getName();
        this.rating = book.getRating() == null ? 0 : book.getRating().getRating();
        this.series = book.getSeries() == null ? "" : book.getSeries().getName();
        this.tags = book.getTags() == null ? "" : book.getTags().stream().map(Tag::getName).collect(Collectors.joining(", "));
        this.altTags = book.getAltTags() == null ? "" : book.getAltTags().stream().map(CustomColumn::getValue).collect(Collectors.joining(", "));

        this.isbn = book.getIsbn();
        this.bbk = book.getBbk();
        this.format = book.getFormat();
        this.source = book.getSource();
        this.officialTitle = book.getOfficialTitle();
        this.type = book.getType();
        this.company = book.getCompany();
        this.udk = book.getUdk();
        this.edition = book.getEdition();
        this.postprocessing = book.getPostprocessing();
        this.signedInPrint = book.getSignedInPrint();
        this.fileName = book.getFileName();
        this.externalLink = book.getExternalLink();
        this.scannedBy = book.getScannedBy();
        this.pages = book.getPages();
        this.own = book.getOwn();

        this.tiviId = book.getTiviId();
        this.cpu = book.getCpu();

        // вычисляемые значения, эти значения нужны для генерации страниц на сайте
        this.siteCpu = book.getSiteCpu();
        this.siteUri = book.getSiteUri();
        this.siteThumbUri = book.getSiteThumbUri();
    }
}

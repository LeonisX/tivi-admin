package md.leonis.tivi.admin.model.calibre;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import md.leonis.tivi.admin.model.Type;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@lombok.Data
public class CalibreBook extends Book {

    private String textShort;
    private String textMore;
    private String comment;
    private String releaseNote;

    private List<Author> authors;
    private List<Language> languages;
    private PublisherSeries publisher;
    private Rating rating;
    private PublisherSeries series;
    private String group;
    private List<Tag> tags;
    private List<CustomColumn> altTags;

    @SerializedName("customIsbn")
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

    private List<Data> dataList;
    private List<Identifier> identifiers;

    private Long tiviId;
    private String cpu;

    // вычисляемые значения, эти значения нужны для генерации страниц на сайте
    private transient String siteCpu;
    private transient String siteUri;
    private transient String siteThumbUri;
    private transient String siteCoverUri;

    public boolean belongsToCategory(String category) {
        return tags != null && tags.stream().map(Tag::getName).collect(toList()).contains(category);
    }

    public boolean belongsToCategoryExclusive(String category) {
        return tags != null && tags.size() == 1 && tags.get(0).getName().equals(category);
    }

    public boolean mentionedInCategory(String category) {
        return altTags != null && altTags.stream().map(CustomColumn::getValue).collect(toList()).contains(category);
    }

    public boolean mentionedSomewhere() {
        return altTags != null && altTags.size() > 0;
    }
}
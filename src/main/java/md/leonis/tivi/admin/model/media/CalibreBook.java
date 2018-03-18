package md.leonis.tivi.admin.model.media;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<Tag> tags;
    private List<CustomColumn> altTags;

    @SerializedName("customIsbn")
    private String isbn;
    private String bbk;
    private String format;
    private String source;
    private String officialTitle;
    private String type;
    private String company;
    private String udk;
    private Integer edition;
    private String postprocessing;
    private LocalDateTime signedInPrint;
    private String fileName;
    private String scannedBy;
    private Integer pages;
    private Boolean own;

    private List<Data> dataList;
    private List<Identifier> identifiers;

    private Long tiviId;
    private String cpu;
}
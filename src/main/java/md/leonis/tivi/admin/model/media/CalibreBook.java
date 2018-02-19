package md.leonis.tivi.admin.model.media;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@lombok.Data
@ToString(exclude = {"unusedIsbn", "unusedLccn", "flags", "uuid"})
public class CalibreBook {

    private Long id;
    private String title;
    private String sort;
    private LocalDateTime timestamp;
    @SerializedName("pubdate")
    private LocalDateTime publDate;
    @SerializedName("series_index")
    private Double serieIndex;
    @SerializedName("author_sort")
    private String authorSort;

    @SerializedName("isbn")
    private String unusedIsbn;
    @SerializedName("lccn")
    private String unusedLccn;

    private String path;
    private Boolean flags;
    private String uuid;
    @SerializedName("has_cover")
    private Boolean hasCover;
    @SerializedName("last_modified")
    private LocalDateTime lastModified;

    private String comment;
    private List<Author> authors;
    private List<Language> languages;
    private PublisherSeries publisher;
    private Rating rating;
    private PublisherSeries series;
    private List<Tag> tags;

    private transient String isbn;
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

}
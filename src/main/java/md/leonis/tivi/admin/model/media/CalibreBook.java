package md.leonis.tivi.admin.model.media;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import md.leonis.tivi.admin.utils.BookUtils;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@lombok.Data
public class CalibreBook  extends Book {

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
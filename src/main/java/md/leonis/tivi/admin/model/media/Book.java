package md.leonis.tivi.admin.model.media;

import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@lombok.Data
@ToString(exclude = {"unusedIsbn", "unusedLccn", "flags", "uuid"})
public class Book {

    private Long id;
    private String title;
    private String sort;
    private LocalDateTime timestamp;
    private LocalDateTime publDate;
    private Double serieIndex;
    private String authorSort;

    private String unusedIsbn;
    private String unusedLccn;

    private String path;
    private Long flags;
    private String uuid;
    private Boolean hasCover;
    private LocalDateTime lastModified;

    private List<Author> authors;
    private List<Language> languages;
    private PublisherSeries publisher;
    private Rating rating;
    private PublisherSeries series;
    private List<Tag> tags;

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

}
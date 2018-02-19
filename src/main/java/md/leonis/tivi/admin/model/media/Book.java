package md.leonis.tivi.admin.model.media;

import com.google.gson.annotations.SerializedName;
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
    private Integer flags;
    private String uuid;
    @SerializedName("has_cover")
    private Integer hasCover;
    @SerializedName("last_modified")
    private LocalDateTime lastModified;

}

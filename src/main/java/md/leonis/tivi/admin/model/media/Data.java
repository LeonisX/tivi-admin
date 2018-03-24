package md.leonis.tivi.admin.model.media;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
public class Data {

    private Long id;
    private Long book;
    private String format;
    @SerializedName("uncompressed_size")
    private Long uncompressedSize;
    private String name;
    private String crc32;
    transient private String fileName;

}

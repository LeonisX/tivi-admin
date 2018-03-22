package md.leonis.tivi.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookRecord {

    private Boolean checked;
    private String name;
    private long crc32;
    private long size;
    //private String uuid;
    private String bookName;

}

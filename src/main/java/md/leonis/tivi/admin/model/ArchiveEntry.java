package md.leonis.tivi.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArchiveEntry {

    private String name;
    private long crc32;
    private long size;

}

package md.leonis.tivi.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArchiveEntry {

    private final String name;
    private final long crc32;
    private final long size;
}

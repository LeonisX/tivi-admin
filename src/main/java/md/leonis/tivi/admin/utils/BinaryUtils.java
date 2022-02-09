package md.leonis.tivi.admin.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.CRC32;

public class BinaryUtils {

    public static long crc32(File file) throws IOException {
        return crc32(Files.readAllBytes(file.toPath()));
    }

    public static long crc32(byte[] bytes) {

        CRC32 crc = new CRC32();
        crc.update(bytes);
        return crc.getValue();
    }
}

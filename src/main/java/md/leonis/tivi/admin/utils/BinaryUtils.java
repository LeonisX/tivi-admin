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

    /*public static String crc32(Path path) {
        return String.format("%08X", getCrc32(path));
    }*/

    /*private static long getCrc32(Path path) {
        try {
            InputStream in = new FileInputStream(path.toFile());
            CRC32 crcMaker = new CRC32();
            byte[] buffer = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                crcMaker.update(buffer, 0, bytesRead);
            }
            return crcMaker.getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/
}

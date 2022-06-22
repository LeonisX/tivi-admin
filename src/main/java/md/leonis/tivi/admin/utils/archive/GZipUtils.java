package md.leonis.tivi.admin.utils.archive;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class GZipUtils {

    private static final int BUFFER_SIZE = 1024;

    public static void gunzipItToFile(String fileName, File newFile) {

        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            GZIPInputStream gzis = new GZIPInputStream(new URL(fileName).openStream());
            FileOutputStream out = new FileOutputStream(newFile);
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            gzis.close();
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String gunzipItToString(String fileName) throws IOException {

        final char[] buffer = new char[BUFFER_SIZE];
        final StringBuilder out = new StringBuilder();
        try (Reader in = new InputStreamReader(new GZIPInputStream(new URL(fileName).openStream()), StandardCharsets.UTF_8)) {
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            return out.toString();
        }
    }
}

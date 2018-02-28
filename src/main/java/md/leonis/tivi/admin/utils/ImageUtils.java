package md.leonis.tivi.admin.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Rendering;

import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static void saveThumbnail(File source, String destination) throws IOException {
        Thumbnails.of(source).outputQuality(0.95).width(180).rendering(Rendering.QUALITY).toFile(destination);
    }

    public static void saveThumbnail(String source, String destination) throws IOException {
        Thumbnails.of(source).outputQuality(0.95).width(180).rendering(Rendering.QUALITY).toFile(destination);
    }
}

package md.leonis.tivi.admin.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Rendering;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ImagesTest {

    //TODO remove if not need
    @Test
    public void test() throws IOException {
        Thumbnails.of(new File("E:\\cover.jpg")).outputQuality(1).width(180).rendering(Rendering.QUALITY)
                .toFile(new File("E:\\Calibre\\cover-t100.jpg"));

        Thumbnails.of(new File("E:\\cover.jpg")).outputQuality(0.95).width(180).rendering(Rendering.QUALITY)
                .toFile(new File("E:\\Calibre\\cover-t95.jpg"));

        Thumbnails.of(new File("E:\\cover.jpg")).outputQuality(0.9).width(180).rendering(Rendering.QUALITY)
                .toFile(new File("E:\\Calibre\\cover-t90.jpg"));

        Thumbnails.of(new File("E:\\cover.jpg")).outputQuality(0.8).width(180).rendering(Rendering.QUALITY)
                .toFile(new File("E:\\Calibre\\cover-t80.jpg"));
    }

    @Test
    public void testDumper() throws IOException {
        //CalibreUtils.dumpImages();
        //CalibreUtils.dumpBooks(onlyForSiteCheckBox.isSelected());
    }

}

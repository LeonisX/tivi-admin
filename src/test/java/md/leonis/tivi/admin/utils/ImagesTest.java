package md.leonis.tivi.admin.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Rendering;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.stream.Collectors.toSet;

public class ImagesTest {

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
        //dumpImages();
        dumpBooks();
    }

    public static void dumpImages() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();

        BookUtils.calibreBooks = CalibreUtils.readBooks();

        File coversDir = new File(Config.workPath + "covers");
        File thumbsDir = new File(Config.workPath + "thumbs");

        deleteFileOrFolder(coversDir.toPath());
        deleteFileOrFolder(thumbsDir.toPath());

        coversDir.mkdirs();
        thumbsDir.mkdirs();

        BookUtils.calibreBooks.stream().filter(b -> b.getOwn() != null && b.getOwn()).forEach(b -> {
            try {
                //TODO remove Calibre
                Path srcCover = Paths.get(Config.calibreDbPath).resolve("Calibre").resolve(b.getPath()).resolve("cover.jpg");
                Path destCover = coversDir.toPath().resolve(b.getCpu() + ".jpg");
                Files.copy(srcCover, destCover, REPLACE_EXISTING);
                Path destThumb = thumbsDir.toPath().resolve(b.getCpu() + ".jpg");
                ImageUtils.saveThumbnail(destCover.toFile(), destThumb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void dumpBooks() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();

        BookUtils.calibreBooks = CalibreUtils.readBooks();

        File booksDir = new File(Config.workPath + "books");

        deleteFileOrFolder(booksDir.toPath());

        BookUtils.calibreBooks.stream().filter(b -> b.getOwn() != null && b.getOwn()).forEach(b -> {
            String system;
            if (b.getTags().size() > 1) {
                system = "consoles"; //TODO computers
            } else {
                system = b.getTags().get(0).getName();
            }
            Path destPath = booksDir.toPath().resolve(system);
            destPath.toFile().mkdirs();
            final String fileName = b.getFileName() == null ? b.getTitle() : b.getFileName();
            b.getDataList().forEach(data -> {
                //TODO uncompress
                //TODO remove Calibre
                Path srcBook = Paths.get(Config.calibreDbPath).resolve("Calibre").resolve(b.getPath()).resolve(data.getName() + "." + data.getFormat().toLowerCase());
                switch (data.getFormat().toLowerCase()) {
                    case "zip":
                        if (uncompress(SevenZipUtils.getZipFileList(srcBook.toFile()))) {
                            SevenZipUtils.extractZip(srcBook, destPath, fileName);
                        } else {
                            copyFile(srcBook, destPath, fileName, data.getFormat());
                        }
                        break;
                    case "7z":
                        if (uncompress(SevenZipUtils.get7zFileList(srcBook.toFile()))) {
                            SevenZipUtils.extract7z(srcBook, destPath, fileName);
                        } else {
                            copyFile(srcBook, destPath, fileName, data.getFormat());
                        }
                        break;
                    case "rar":
                        if (uncompress(RarUtils.getRarFileList(srcBook.toFile()))) {
                            RarUtils.extractArchive(srcBook, destPath, fileName);
                        } else {
                            copyFile(srcBook, destPath, fileName, data.getFormat());
                        }
                        break;
                    case "pdf":
                    case "djvu":
                    case "cbr":
                    case "doc":
                    case "jpg":
                        copyFile(srcBook, destPath, fileName, data.getFormat());
                        break;
                    default:
                        throw new RuntimeException(data.toString());
                }
            });
        });
    }

    private static void copyFile(Path srcBook, Path destPath, String fileName, String ext) {
        System.out.println("Copy: " + srcBook);
        Path destBook = SevenZipUtils.findFreeFileName(destPath, fileName, ext.toLowerCase(), 0);
        try {
            Files.copy(srcBook, destBook, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static Set<String> imgs = new HashSet<>(Arrays.asList("jpeg", "jpg", "png", "tif", "tiff", "exe"));

    private static boolean uncompress(List<String> fileNames) {
        Set<String> exts = fileNames.stream().map(SevenZipUtils::getExtension).collect(toSet());
        return Collections.disjoint(exts, imgs);
    }



    public static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(final Path file, final IOException e) {
                return handleException(e);
            }

            private FileVisitResult handleException(final IOException e) {
                e.printStackTrace(); // replace with more robust error handling
                return TERMINATE;
            }

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                    throws IOException {
                if (e != null) return handleException(e);
                Files.delete(dir);
                return CONTINUE;
            }
        });
    }

    ;
}

package md.leonis.tivi.admin.utils.archive;

import lombok.SneakyThrows;
import md.leonis.tivi.admin.model.ArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static java.util.stream.Collectors.toList;

public class SevenZipUtils {

    /*TO-DO resolve file name
    public static void extractZip(String sourceFile, String destinationDir) throws IOException {
        File dir = new File(destinationDir);
        mkdirs(dir);

        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourceFile)))) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                OutputStream os = new FileOutputStream(destinationDir + File.separatorChar + ze.getName());
                byte[] buffer = new byte[1024];
                int count;
                while ((count = zis.read(buffer)) > -1) {
                    os.write(buffer, 0, count);
                }
                os.close();
                zis.closeEntry();
            }
        }
    }*/

    @SneakyThrows
    public static void extractZip(Path sourcePath, Path destPath, String fileName) {
        System.out.println(sourcePath);
        mkdirs(destPath);
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourcePath.toFile())), Charset.forName("CP1251"))) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " + ze.getName());
                OutputStream os = new FileOutputStream(findFreeFileName(destPath, fileName, getExtension(ze.getName()), 0).toFile());
                byte[] buffer = new byte[1024];
                int count;
                while ((count = zis.read(buffer)) > -1) {
                    os.write(buffer, 0, count);
                }
                os.close();
                zis.closeEntry();
            }
        }
    }

    @SneakyThrows
    public static void extract7z(Path sourcePath, Path destPath, String fileName) {
        System.out.println(sourcePath);
        mkdirs(destPath);
        SevenZFile sevenZFile = new SevenZFile(sourcePath.toFile());
        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null) {
            while (entry != null) {
                System.out.println("Extracting: " + entry.getName());
                OutputStream os = new FileOutputStream(findFreeFileName(destPath, fileName, getExtension(entry.getName()), 0).toFile());
                byte[] buffer = new byte[8192];//
                int count;
                while ((count = sevenZFile.read(buffer, 0, buffer.length)) > -1) {
                    os.write(buffer, 0, count);
                }
                entry = sevenZFile.getNextEntry();
                os.close();
            }
        }
        sevenZFile.close();
    }


    /*TO-DO resolve file name
    public static void extract7z(String sourceFile, String destinationDir) throws IOException {
        File dir = new File(destinationDir);
        mkdirs(dir);

        SevenZFile sevenZFile = new SevenZFile(new File(sourceFile));
        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null) {
            while (entry != null) {
                OutputStream os = new FileOutputStream(destinationDir + File.separatorChar + entry.getName());
                byte[] buffer = new byte[8192];//
                int count;
                while ((count = sevenZFile.read(buffer, 0, buffer.length)) > -1) {
                    os.write(buffer, 0, count);
                }
                entry = sevenZFile.getNextEntry();
                os.close();
            }
        }
        sevenZFile.close();
    }*/


    @SneakyThrows
    public static List<ArchiveEntry> getZipFileList(File fileName) {
        System.out.println(fileName);
        ZipFile zipFile = new ZipFile(fileName, Charset.forName("CP1251"));
        return zipFile.stream().map(ze -> new ArchiveEntry(ze.getName(), ze.getCrc(), ze.getSize()) {
        }).collect(toList());
    }

    @SneakyThrows
    public static List<ArchiveEntry> get7zFileList(File fileName) {
        SevenZFile sevenZFile = new SevenZFile(fileName);
        return StreamSupport.stream(sevenZFile.getEntries().spliterator(), false).map(ze -> new ArchiveEntry(ze.getName(), ze.getCrcValue(), ze.getSize())).collect(toList());
    }


    // TODO to separate utils

    public static Path findFreeFileName(Path destPath, String fileName, int incr) {
        String[] tokens = fileName.split("\\.(?=[^.]+$)");
        //System.out.println(Arrays.asList(tokens));
        return findFreeFileName(destPath, tokens[0], tokens[1], incr);
    }


    public static Path findFreeFileName(Path path, String fileName, String ext, int incr) {
        Path result;
        //TODO remove try
        try {
            result = path.resolve(fileName + incrToString(incr) + "." + ext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (Files.exists(result)) {
            return findFreeFileName(path, fileName, ext, ++incr);
        }
        return result;
    }

    private static String incrToString(int incr) {
        switch (incr) {
            case 0:
                return "";
            case 1:
                return " (alt)";
            default:
                return " (alt" + incr + ")";
        }
    }

    public static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
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

    //TODO separate utils
    private static void mkdirs(Path path) {
        mkdirs(path.toFile());
    }

    @SuppressWarnings("all")
    private static void mkdirs(File file) {
        file.mkdirs();
    }

}

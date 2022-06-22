package md.leonis.tivi.admin.utils.archive;

import lombok.SneakyThrows;
import md.leonis.tivi.admin.model.ArchiveEntry;
import md.leonis.tivi.admin.utils.FileUtils;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static java.util.stream.Collectors.toList;

public class SevenZipUtils {

    /*TO-DO resolve file name
    public static void extractZip(String sourceFile, String destinationDir) throws IOException {
        File dir = new File(destinationDir);
        FileUtils.mkdirs(dir);

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
        FileUtils.mkdirs(destPath);
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(sourcePath.toFile())), Charset.forName("CP1251"))) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                System.out.println("Extracting: " + ze.getName());
                OutputStream os = new FileOutputStream(FileUtils.findFreeFileName(destPath, fileName, FileUtils.getExtension(ze.getName()), 0).toFile());
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
        FileUtils.mkdirs(destPath);
        SevenZFile sevenZFile = new SevenZFile(sourcePath.toFile());
        SevenZArchiveEntry entry;
        while ((entry = sevenZFile.getNextEntry()) != null) {
            while (entry != null) {
                System.out.println("Extracting: " + entry.getName());
                OutputStream os = new FileOutputStream(FileUtils.findFreeFileName(destPath, fileName, FileUtils.getExtension(entry.getName()), 0).toFile());
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
        FileUtils.mkdirs(dir);

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
}

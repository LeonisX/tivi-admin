package md.leonis.tivi.admin.utils;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

public class FileUtils {

    @SuppressWarnings("all")
    public static void backupFile(File file) {
        if (file.exists()) {
            file.renameTo(new File(file.toString() + ".bak"));
        }
    }

    @SneakyThrows
    public static void backupFile(Path path) {
        if (Files.exists(path)) {
            Path backupPath = path.resolveSibling(path.getFileName() + ".bak");
            Files.deleteIfExists(backupPath);
            Files.copy(path, backupPath);
            try {
                Files.deleteIfExists(path);
            } catch (Exception ignored) {
            }
        }
    }

    public static void mkdirs(File destination, String fileName) {
        if (fileName == null) {
            return;
        }
        String[] dirs = fileName.split("\\\\");
        String path = "";
        for (String dir : dirs) {
            path = path + File.separator + dir;
            mkdirs(new File(destination, path));
        }
    }

    @SneakyThrows
    public static void mkdirs(Path path) {
        Files.createDirectories(path);
    }

    public static void mkdirs(String path) {
        mkdirs(Paths.get(path));
    }

    @SuppressWarnings("all")
    public static void mkdirs(File file) {
        file.mkdirs();
    }

    public static Path findFreeFileName(Path destPath, String fileName, int incr) {
        String[] tokens = fileName.split("\\.(?=[^.]+$)");
        return findFreeFileName(destPath, tokens[0], tokens[1], incr);
    }


    public static Path findFreeFileName(Path path, String fileName, String ext, int incr) {
        Path result = path.resolve(prepareFileName(fileName, ext, incr));
        if (Files.exists(result)) {
            return findFreeFileName(path, fileName, ext, ++incr);
        }
        return result;
    }

    public static String findFreeFileName(Set<String> fileNames, String fileName, String ext, int incr) {
        String result = FileUtils.prepareFileName(fileName, ext, incr);
        if (fileNames.contains(result)) {
            return findFreeFileName(fileNames, fileName, ext, ++incr);
        }
        fileNames.add(result);
        return result;
    }

    public static String prepareFileName(String fileName, String ext, int incr) {
        fileName = fileName.replace("\"", "").replace(":", " -");
        return fileName + incrToString(incr) + "." + ext;
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
        return extension.toLowerCase();
    }

    @SneakyThrows
    public static void deleteFileOrFolder(final Path path) {
        if (Files.exists(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
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
                public FileVisitResult postVisitDirectory(final Path dir, final IOException e) throws IOException {
                    if (e != null) return handleException(e);
                    Files.deleteIfExists(dir);
                    return CONTINUE;
                }
            });
        }
    }

    public static List<String> loadTextFile(Path path) {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveToFile(Path path, List<String> list) {
        saveToFile(path, String.join("\n", list));
    }

    public static void saveToFile(Path path, String text) {
        try (PrintWriter out = new PrintWriter(path.toFile())) {
            out.println(text);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

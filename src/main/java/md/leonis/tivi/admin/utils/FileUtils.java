package md.leonis.tivi.admin.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;

public class FileUtils {
    public static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final String JSON = ".json";

    public static Map<String, String> loadJsonMap(Path path, String fileName) {
        try {
            return MAPPER.readValue(path.resolve(fileName + JSON).toFile(), new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static <T> Map<String, List<T>> loadJsonMapWithList(Path path, String fileName, Class<T> clazz) {
        try {
            return MAPPER.readValue(path.resolve(fileName + JSON).toFile(), new TypeReference<Map<String, List<T>>>() {
            });
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public static List<String> loadJsonList(Path path, String fileName) {
        return loadJsonList(path, fileName, String.class);
    }

    public static <T> List<T> loadJsonList(Path path, String fileName, Class<T> clazz) {
        try {
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return MAPPER.readValue(path.resolve(fileName + JSON).normalize().toAbsolutePath().toFile(), type);
        } catch (Exception e) {
            if (!(e instanceof FileNotFoundException)) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    public static <T> List<T> loadJsonList(Path path, Class<T> clazz) {
        try {
            JavaType type = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
            return MAPPER.readValue(path.normalize().toAbsolutePath().toFile(), type);
        } catch (Exception e) {
            if (!(e instanceof FileNotFoundException)) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    public static <T> T loadAsJson(Path path, String fileName, Class<T> clazz) {
        try {
            return MAPPER.readValue(path.resolve(fileName + JSON).normalize().toAbsolutePath().toFile(), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveAsJson(Path path, String fileName, Object object) throws IOException {
        saveAsJson(path.resolve(fileName + JSON), object);
    }

    public static void saveAsJson(Path path, Object object) throws IOException {
        backupFile(path);

        String result = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        result = result.replace("&nbsp;", " ");

        Files.createDirectories(path.getParent());
        Files.write(path, result.getBytes());
        //TODO revert
        //LOGGER.debug("Saved: " + path.getFileName().toString());
    }

    public static void backupFile(File file) throws IOException {
        backupFile(file.toPath());
    }

    public static void backupFile(Path path) throws IOException {
        if (Files.exists(path)) {
            Path backupFile = Paths.get(path.normalize().toAbsolutePath().toString() + ".bak");
            if (Files.exists(backupFile)) {
                Files.delete(backupFile);
            }
            try {
                Files.copy(path, backupFile);
            } catch (Exception ignored) {
            }
        }
    }

    public static void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteJsonFile(Path path, String fileName) {
        try {
            Files.delete(path.resolve(fileName + JSON));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Path> listFiles(Path path) throws IOException {
        try (Stream<Path> stream = Files.list(path)) {
            return stream.filter(file -> !Files.isDirectory(file)).collect(Collectors.toList());
        }
    }

    public static void copyFile(Path src, Path dest) {
        try {
            Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteFile(Path path) {
        try {
            Files.delete(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static long fileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}

package md.leonis.databaser;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import md.leonis.tivi.admin.utils.FileUtils;
import md.leonis.tivi.admin.utils.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static md.leonis.tivi.admin.utils.FileUtils.MAPPER;

public class MigrateDataBaser {

    static Path PATH = Paths.get("D:\\tivi-admin\\src\\test\\java\\md\\leonis\\databaser");

    static String FILE_NAME = "new";

    // new.txt -> new-fixed.sql
    @Test
    public void fixOld() {
        List<String> lines = FileUtils.loadTextFile(PATH.resolve(FILE_NAME + ".txt"));
        lines.removeIf(l -> l.startsWith("CREATE NOCASE INDEX IF NOT EXISTS"));
        lines.removeIf(l -> l.startsWith("CREATE INDEX IF NOT EXISTS"));
        lines.removeIf(l -> l.startsWith("/*"));
        lines.removeIf(l -> l.startsWith("DESCRIPTION"));
        lines.removeIf(l -> l.startsWith("PRIMARY KEY"));

        lines = lines.stream().map(line -> {
            if (line.startsWith("CREATE TABLE IF NOT EXISTS")) {
                return line.replace('"', '`').replace("EXISTS `", "EXISTS `old_");
            } else if (line.startsWith("INSERT INTO")) {
                return line.replace('"', '`').replace("INTO `", "INTO `old_");
            } else {
                return line;
            }
        }).collect(Collectors.toList());

        String text = String.join("\n", lines);
        text = text.replace("   \"N\" INTEGER,\n" +
                        "   \"Sys\" VARCHAR(12),\n" +
                        "   \"Sid\" VARCHAR(3),\n" +
                        "   \"Title\" VARCHAR(96),\n" +
                        "   \"Region\" VARCHAR(30),\n" +
                        "   \"Publisher\" VARCHAR(64),\n" +
                        "   \"Developer\" VARCHAR(64),\n" +
                        "   \"God\" VARCHAR(4),\n" +
                        "   \"God1\" VARCHAR(4),\n" +
                        "   \"Ngamers\" SMALLINT,\n" +
                        "   \"Mgamers\" SMALLINT,\n" +
                        "   \"Genre\" VARCHAR(64),\n" +
                        "   \"Type\" VARCHAR(64),\n" +
                        "   \"Gen\" VARCHAR(30),\n" +
                        "   \"Image1\" VARCHAR(96),\n" +
                        "   \"Image2\" VARCHAR(96),\n" +
                        "   \"Image3\" VARCHAR(96),\n" +
                        "   \"Image4\" VARCHAR(96),\n" +
                        "   \"Image5\" VARCHAR(96),\n" +
                        "   \"Image6\" VARCHAR(96),\n" +
                        "   \"Image7\" VARCHAR(96),\n" +
                        "   \"Image8\" VARCHAR(96),\n" +
                        "   \"Image9\" VARCHAR(96),\n" +
                        "   \"Image10\" VARCHAR(96),\n" +
                        "   \"Image11\" VARCHAR(96),\n" +
                        "   \"Image12\" VARCHAR(96),\n" +
                        "   \"Image13\" VARCHAR(96),\n" +
                        "   \"Image14\" VARCHAR(96),\n" +
                        "   \"Text1\" MEMO,\n" +
                        "   \"Text2\" MEMO,\n" +
                        "   \"Analog\" VARCHAR(255),\n" +
                        "   \"Drname\" VARCHAR(255),\n" +
                        "   \"Cross\" VARCHAR(96),\n" +
                        "   \"Serie\" VARCHAR(96),\n" +
                        "   \"Rating\" SMALLINT,\n" +
                        "   \"Serial\" VARCHAR(64),\n" +
                        "   \"Yrating\" VARCHAR(10),\n" +
                        "   \"Price\" VARCHAR(10),\n" +
                        "   \"Rarity\" CHAR(1),\n" +
                        "   \"Barcode\" VARCHAR(18),\n",

                "   `n` INTEGER,\n" +
                        "   `sys` VARCHAR(12),\n" +
                        "   `sid` VARCHAR(3),\n" +
                        "   `title` VARCHAR(96),\n" +
                        "   `region` VARCHAR(30),\n" +
                        "   `publisher` VARCHAR(64),\n" +
                        "   `developer` VARCHAR(64),\n" +
                        "   `god` VARCHAR(4),\n" +
                        "   `god1` VARCHAR(4),\n" +
                        "   `ngamers` SMALLINT,\n" +
                        "   `mgamers` SMALLINT,\n" +
                        "   `genre` VARCHAR(64),\n" +
                        "   `type` VARCHAR(64),\n" +
                        "   `gen` VARCHAR(30),\n" +
                        "   `image1` VARCHAR(96),\n" +
                        "   `image2` VARCHAR(96),\n" +
                        "   `image3` VARCHAR(96),\n" +
                        "   `image4` VARCHAR(96),\n" +
                        "   `image5` VARCHAR(96),\n" +
                        "   `image6` VARCHAR(96),\n" +
                        "   `image7` VARCHAR(96),\n" +
                        "   `image8` VARCHAR(96),\n" +
                        "   `image9` VARCHAR(96),\n" +
                        "   `image10` VARCHAR(96),\n" +
                        "   `image11` VARCHAR(96),\n" +
                        "   `image12` VARCHAR(96),\n" +
                        "   `image13` VARCHAR(96),\n" +
                        "   `image14` VARCHAR(96),\n" +
                        "   `text1` TEXT,\n" +
                        "   `text2` TEXT,\n" +
                        "   `analog` VARCHAR(255),\n" +
                        "   `drname` VARCHAR(255),\n" +
                        "   `cros` VARCHAR(96),\n" +
                        "   `serie` VARCHAR(96),\n" +
                        "   `rating` SMALLINT,\n" +
                        "   `serial` VARCHAR(64),\n" +
                        "   `yrating` VARCHAR(10),\n" +
                        "   `price` VARCHAR(10),\n" +
                        "   `rarity` CHAR(1),\n" +
                        "   `barcode` VARCHAR(18)\n")
                .replace("'+\n" +
                        "         '", "")
                .replace("'+#13+''+#10+'", "<br />")
                .replace("'+#13+'", "<br />")
                .replace("'+#10+'", "<br />")
                .replace("'+#39+'", "’")
                .replace("   \"N\" INTEGER NOT NULL,\n" +
                        "   \"Sys\" VARCHAR(16) NOT NULL,\n" +
                        "   \"Hash\" VARCHAR(25) NOT NULL,\n" +
                        "   \"Type\" CHAR(1) NOT NULL,\n" +
                        "   \"Order\" CHAR(1) NOT NULL,\n" +
                        "   \"Ext\" VARCHAR(8),\n" +
                        "   \"Size\" VARCHAR(8) NOT NULL,\n" +
                        "   \"Position\" WORD,\n" +
                        "   \"CRC32\" CHAR(8) NOT NULL,\n" +
                        "   \"Comment\" VARCHAR(512),\n", "   `n` INTEGER NOT NULL,\n" +
                        "   `sys` VARCHAR(16) NOT NULL,\n" +
                        "   `hash` VARCHAR(25) NOT NULL,\n" +
                        "   `type` CHAR(1) NOT NULL,\n" +
                        "   `order` CHAR(1) NOT NULL,\n" +
                        "   `ext` VARCHAR(8),\n" +
                        "   `size` VARCHAR(8) NOT NULL,\n" +
                        "   `position` WORD,\n" +
                        "   `crc32` CHAR(8) NOT NULL,\n" +
                        "   `comment` VARCHAR(512)\n")
                .replace("   \"N\" INTEGER,\n" +
                        "   \"Sys\" VARCHAR(12),\n" +
                        "   \"Type\" VARCHAR(1),\n" +
                        "   \"Comment\" VARCHAR(128),\n", "   `n` INTEGER,\n" +
                        "   `sys` VARCHAR(12),\n" +
                        "   `type` VARCHAR(1),\n" +
                        "   `comment` VARCHAR(128)\n")

                .replace("LOCALE CODE 0\n" +
                        "USER MAJOR VERSION 1\n", "")
                .replace(
                        "LOCALE CODE 0\n" +
                                "USER MAJOR VERSION 2\n" +
                                "USER MINOR VERSION 5\n",
                        "")
                .replace("\"", "\\\"");

        FileUtils.saveToFile(PATH.resolve(FILE_NAME + "-fixed.sql"), text);
    }

    // new-fixed.sql -> dbsr/xxxx.json
    @Test
    public void insert2() throws SQLException, ClassNotFoundException {
        Path path = PATH.resolve(FILE_NAME + "-fixed.sql");
        List<String> lines = FileUtils.loadTextFile(path);

        List<String> chunk = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("CREATE TABLE IF NOT EXISTS")) {
                System.out.println(line);
                dbInsert(chunk);
                chunk.clear();
            }
            chunk.add(line);
        }
        dbInsert(chunk);

        Class.forName("org.h2.Driver");

        List<String> platforms = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(String.join("\n", lines));

            ResultSet tables = stmt.executeQuery("SHOW TABLES");
            while (tables.next()) {
                platforms.add(tables.getString(1));
            }

            for (String tableName : platforms) {
                List<TiviStructure> structures = readObjectList(conn, String.format("SELECT * FROM %s", tableName), TiviStructure.class);
                String fileName = tableName.toLowerCase().replace("old_", "");
                System.out.println(fileName);
                path = PATH.resolve("dbsr");
                FileUtils.mkdirs(path);
                MAPPER.writeValue(path.resolve(fileName + ".json").toFile(), structures);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //todo probably delete
    /*@Test
    public void insert() throws SQLException {
        Path path = PATH.resolve(FILE_NAME + "-fixed.sql");
        List<String> lines = FileUtils.loadTextFile(path);

        //Class.forName("org.h2.Driver");

        dbInsert(lines);
        //new CalibreUtils().readObjectList("", Type.class);
    }*/

    // tivi (from site) to 3 structures
    //TODO sorted
    @Test
    public void convertTiviGames() throws IOException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configOverride(String.class).setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));

        for (Path path : FileUtils.listFiles(PATH.resolve("tivi"))) {
            System.out.println(path.getFileName());
            Path newPath = PATH.resolve("tivi2").resolve(path.getFileName());
            saveAsJson(newPath, sort(loadJsonList(path, TiviStructure.class, mapper).stream().map(TiviStructure::corrected)
                    .collect(Collectors.toList())), mapper);

            newPath = PATH.resolve("tivi2s").resolve(path.getFileName());
            saveAsJson(newPath, sort2(loadJsonList(path, ShortTiviStructure.class, mapper).stream().map(ShortTiviStructure::corrected)
                    .collect(Collectors.toList())), mapper);

            newPath = PATH.resolve("tivi3s").resolve(path.getFileName());
            saveAsJson(newPath, sort3(loadJsonList(path, ShortTiviStructure2.class, mapper).stream().map(ShortTiviStructure2::corrected)
                    .collect(Collectors.toList())), mapper);
        }
    }

    // databaser to 3 structures
    @Test
    public void convertDataBaser() throws IOException {
        StringModule stringModule = new StringModule();

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(stringModule);

        Path imagePath = PATH.resolve("images");

        mapper.configOverride(String.class).setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
        for (Path path : FileUtils.listFiles(PATH.resolve("dbsr"))) {
            System.out.println(path.getFileName());
            //todo
            /*if (!path.getFileName().toString().startsWith("gg")) {
                continue;
            }*/
            Path newPath = PATH.resolve("dbsr2").resolve(path.getFileName());
            List<TiviStructure> list = sort(loadJsonList(path, TiviStructure.class, mapper));
            saveAsJson(newPath, fix(list, imagePath, TiviStructure.class, mapper), mapper);

            newPath = PATH.resolve("dbsr2s").resolve(path.getFileName());
            saveAsJson(newPath, fix(list, imagePath, ShortTiviStructure.class, mapper), mapper);

            newPath = PATH.resolve("dbsr3s").resolve(path.getFileName());
            saveAsJson(newPath, fix(list, imagePath, ShortTiviStructure2.class, mapper), mapper);
        }
    }

    <T> List<T> fix(List<TiviStructure> list, Path imagePath, Class<T> clazz, ObjectMapper mapper) {
        return list.stream().map(structure -> {
            Map<String, Object> map = mapper.convertValue(structure, new TypeReference<Map<String, Object>>() {
            });
            return mapper.convertValue(normalizeImageName(map, imagePath), clazz);
        }).collect(Collectors.toList());
    }

    public static String unescapeChars(String s) {
        s = s.replace("&rsquo;", "'");
        //s = s.replace("&amp;", "&");

        return StringEscapeUtils.unescapeHtml4(s);
    }

    private Map<String, Object> normalizeImageName(Map<String, Object> map, Path root) {
        String sid = (String) map.get("sid");
        String region = (sid.equals("pd") || sid.equals("hak")) ? "" : "_" + ((String) map.get("region")).replace(";", "");
        String image = StringUtils.cleanString(unescapeChars(map.get("name") + region).replace(" ", "_"));
        image = image.substring(0, Math.min(62, image.length()));

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            result.put(e.getKey(), findImageName(e, image, map, root));
        }

        return result;
    }

    private Object findImageName(Map.Entry<String, Object> e, String image, Map<String, Object> map, Path root) {
        if (!e.getKey().startsWith("image")) {
            return e.getValue();
        }
        int index = Integer.parseInt(e.getKey().replace("image", ""));
        return renameImage(unescapeChars((String) e.getValue()), index, image, map, root);
    }

    private String renameImage(String imageOld, int index, String image, Map<String, Object> map, Path root) {
        //todo тут хак миграции "-"
        if (imageOld.isEmpty() || imageOld.matches("^[\\w-]+\\.\\w+$")) {
            return imageOld;
        }
        if (map.get("sys").equals("archi")) { // скринов мало и их нету
            return "";
        }
        String ext = FilenameUtils.getExtension(imageOld); // jpg
        String imageNew = String.format("%s_%s.%s", image, index, ext);
        if (!imageNew.equals(imageOld)) {
            imageOld = imageOld.replace("’", "'"); // даже так
            Path source = root.resolve((String) map.get("sys")).resolve((String) map.get("sid")).resolve(imageOld);
            if (Files.exists(source)) {
                Path target = root.resolve((String) map.get("sys")).resolve((String) map.get("sid")).resolve(imageNew);
                if (Files.exists(target)) {
                    String author = ((String) map.get("publisher")).isEmpty() ? (String) map.get("developer") : (String) map.get("publisher");
                    imageNew = String.format("%s_%s_%s.%s", image, StringUtils.cleanString(unescapeChars(author)).replace(" ", "_"), index, ext);
                    target = root.resolve((String) map.get("sys")).resolve((String) map.get("sid")).resolve(imageNew);
                    if (Files.exists(target)) {
                        throw new RuntimeException("Can't find new image title: " + target);
                    }
                }
                try {
                    System.out.printf("Rename %s to %s%n", imageOld, imageNew);
                    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    throw new RuntimeException("Can't rename image: " + target, e);
                }
            } else {
                System.out.printf("Skip %s%n", source);
            }
        }
        return imageNew;
    }

    private List<TiviStructure> sort(List<TiviStructure> list) {
        //fix sid
        list.forEach(game -> game.setSid(StringUtils.sid(game.getName())));

        List<TiviStructure> result = list.stream().filter(g -> g.getSid().equals("1"))
                .sorted(Comparator.comparing(TiviStructure::getName)).collect(Collectors.toList());
        result.addAll(list.stream().filter(g -> g.getSid().equals("num"))
                .sorted(Comparator.comparing(TiviStructure::getName)).collect(Collectors.toList()));
        for (char c = 'a'; c <= 'z'; c++) {
            char finalC = c;
            result.addAll(list.stream().filter(g -> g.getSid().equals("" + finalC))
                    .sorted(Comparator.comparing(TiviStructure::getName)).collect(Collectors.toList()));
        }
        result.addAll(list.stream().filter(g -> g.getSid().equals("pd"))
                .sorted(Comparator.comparing(TiviStructure::getName)).collect(Collectors.toList()));
        result.addAll(list.stream().filter(g -> g.getSid().equals("hak"))
                .sorted(Comparator.comparing(TiviStructure::getName)).collect(Collectors.toList()));

        return result;
    }

    private List<ShortTiviStructure> sort2(List<ShortTiviStructure> list) {
        //fix sid
        list.forEach(game -> {
            if (StringUtils.isBlank(game.getSid()) && StringUtils.isNotBlank(game.getName())) {
                if (game.getName().endsWith("(Hack)") || game.getName().endsWith(" Hack)") || game.getName().contains("(Hack ")) {
                    game.setSid("hak");
                } else if (game.getName().contains("(PD)")) {
                    game.setSid("pd");
                } else if ("abcdefghijklmnopqrstuvwxyz".contains("" + game.getName().charAt(0))) { // todo substr
                    game.setSid("" + game.getName().charAt(0)); // todo substr
                } else {
                    game.setSid("num");
                }
            }
        });

        List<ShortTiviStructure> result = list.stream().filter(g -> g.getSid().equals("1"))
                .sorted(Comparator.comparing(ShortTiviStructure::getSid).thenComparing(ShortTiviStructure::getName)).collect(Collectors.toList());
        for (char c = 'a'; c <= 'z'; c++) {
            char finalC = c;
            result.addAll(list.stream().filter(g -> g.getSid().equals("" + finalC))
                    .sorted(Comparator.comparing(ShortTiviStructure::getSid).thenComparing(ShortTiviStructure::getName)).collect(Collectors.toList()));
        }
        result.addAll(list.stream().filter(g -> g.getSid().equals("pd"))
                .sorted(Comparator.comparing(ShortTiviStructure::getSid).thenComparing(ShortTiviStructure::getName)).collect(Collectors.toList()));
        result.addAll(list.stream().filter(g -> g.getSid().equals("hak"))
                .sorted(Comparator.comparing(ShortTiviStructure::getSid).thenComparing(ShortTiviStructure::getName)).collect(Collectors.toList()));

        return result;
    }

    private List<ShortTiviStructure2> sort3(List<ShortTiviStructure2> list) {
        //fix sid
        list.forEach(game -> {
            if (StringUtils.isBlank(game.getSid()) && StringUtils.isNotBlank(game.getName())) {
                if (game.getName().endsWith("(Hack)") || game.getName().endsWith(" Hack)") || game.getName().contains("(Hack ")) {
                    game.setSid("hak");
                } else if (game.getName().contains("(PD)")) {
                    game.setSid("pd");
                } else if ("abcdefghijklmnopqrstuvwxyz".contains("" + game.getName().charAt(0))) { // todo substr
                    game.setSid("" + game.getName().charAt(0)); // todo substr
                } else {
                    game.setSid("num");
                }
            }
        });

        List<ShortTiviStructure2> result = list.stream().filter(g -> g.getSid().equals("1"))
                .sorted(Comparator.comparing(ShortTiviStructure2::getSid).thenComparing(ShortTiviStructure2::getName)).collect(Collectors.toList());
        for (char c = 'a'; c <= 'z'; c++) {
            char finalC = c;
            result.addAll(list.stream().filter(g -> g.getSid().equals("" + finalC))
                    .sorted(Comparator.comparing(ShortTiviStructure2::getSid).thenComparing(ShortTiviStructure2::getName)).collect(Collectors.toList()));
        }
        result.addAll(list.stream().filter(g -> g.getSid().equals("pd"))
                .sorted(Comparator.comparing(ShortTiviStructure2::getSid).thenComparing(ShortTiviStructure2::getName)).collect(Collectors.toList()));
        result.addAll(list.stream().filter(g -> g.getSid().equals("hak"))
                .sorted(Comparator.comparing(ShortTiviStructure2::getSid).thenComparing(ShortTiviStructure2::getName)).collect(Collectors.toList()));

        return result;
    }

    private void dbInsert(List<String> lines) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(String.join("\n", lines));
        }
    }

    public <T> List<T> readObjectList(Connection conn, String sql, Class<T> clazz) {
        List<T> results = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    Object value;
                    switch (metaData.getColumnType(i)) { //TODO java.sql.Types values
                        case -7:
                        case 5:
                        case 4:
                        case -5:
                            value = rs.getLong(i);
                            break;
                        case 6:
                        case 7:
                        case 8:
                        case 2:
                        case 3:
                            value = rs.getDouble(i);
                            break;
                        case 1:
                        case -1:
                            value = rs.getString(i);
                            break;
                        case 12:
                            if (metaData.getColumnTypeName(i).equalsIgnoreCase("TIMESTAMP")) {
                                value = parseDate(rs.getString(i));
                            } else {
                                value = rs.getString(i);
                            }
                            break;
                        case 93:
                            value = parseDate(rs.getString(i));
                            break;
                        default:
                            //TODO java.sql.Types
                            throw new RuntimeException("Unknown type:" + metaData.getColumnTypeName(i) + " (" + metaData.getColumnType(i) + ")");
                    }
                    map.put(metaData.getColumnName(i).toLowerCase(), value);
                }
                results.add(MAPPER.convertValue(map, clazz));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return results;
    }

    private static LocalDateTime parseDate(String date) {
        if (date.length() == 25) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");
            return LocalDateTime.parse(date, formatter);
        } else if (date.length() == 23) {
            return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        } else if (date.length() == 22) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");
            return LocalDateTime.parse(date, formatter);
        } else if (date.length() == 21) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S");
            return LocalDateTime.parse(date, formatter);
        } else if (date.length() == 19) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            return LocalDateTime.parse(date, formatter);
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSXXX");
            return LocalDateTime.parse(date, formatter);
        }
    }

    public static void saveAsJson(Path path, Object object, ObjectMapper mapper) throws IOException {
        FileUtils.backupFile(path);

        String result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        //result = result.replace("&nbsp;", " ");

        Files.createDirectories(path.getParent());
        Files.write(path, result.getBytes());
        //TODO revert
        //LOGGER.debug("Saved: " + path.getFileName().toString());
    }

    public static <T> List<T> loadJsonList(Path path, Class<T> clazz, ObjectMapper mapper) {
        try {
            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return mapper.readValue(path.normalize().toAbsolutePath().toFile(), type);
        } catch (Exception e) {
            if (!(e instanceof FileNotFoundException)) {
                e.printStackTrace();
            }
            return new ArrayList<>();
        }
    }

    @Component
    public static class StringModule extends SimpleModule {

        public StringModule() {
            addSerializer(String.class, new StdScalarSerializer<String>(String.class) {
                @Override
                public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                    String init = value;
                    value = value == null ? "" : value;
                    value = value.trim();
                    if (value.endsWith("<br />")) {
                        value = value.substring(0, value.length() - 6);
                    }

                    if (value.endsWith("<br />")) {
                        value = value.substring(0, value.length() - 6);
                    }

                    if (value.endsWith("<br />")) {
                        value = value.substring(0, value.length() - 6);
                    }

                    if (value.equals("-")) {
                        value = "";
                    }

                    /*value = value.replace("&", "&amp;");
                    value = value.replace("\\\"", "&quot;");
                    value = value.replace("’", "&rsquo;");
                    value = value.replace("…", "&hellip;");
                    value = value.replace("•", "&bull;");
                    value = value.replace("·", "&middot;");
                    value = value.replace("“", "&quot;");
                    value = value.replace("”", "&quot;");
                    value = value.replace("—", "&mdash;");
                    value = value.replace("†", "&dagger;");
                    value = value.replace("«", "&laquo;");
                    value = value.replace("»", "&raquo;");*/

                    if (!value.equals(init)) {
                        //System.out.println();
                    }
                    jsonGenerator.writeString(value);
                }
            });
        }
    }

    // Правка DOS ( JSON -> SQL )
    // При первом запуске создаётся CSV файл. Там можно править название и CPU.
    // Сохранить как Книга.csv (utf8), потом запустить этот метод снова
    @Test
    void prepareDosTable() {
        String tableStructure = "DROP TABLE IF EXISTS `base_%s`;\n" +
                "CREATE TABLE `base_%s` (\n" +
                "  `n` smallint(5) NOT NULL default '0',\n" +
                "  `sys` varchar(12) NOT NULL default '',\n" +
                "  `created` int(11) unsigned NOT NULL default '0',\n" +
                "  `modified` int(11) unsigned NOT NULL default '0',\n" +
                "  `sid` varchar(3) NOT NULL default '',\n" +
                "  `cpu` varchar(128) NOT NULL default '',\n" +
                "  `name` varchar(128) default NULL,\n" +
                "  `descript` varchar(255) NOT NULL default '',\n" +
                "  `keywords` varchar(255) NOT NULL default '',\n" +
                "  `region` varchar(12) NOT NULL default '',\n" +
                "  `publisher` varchar(56) NOT NULL default '',\n" +
                "  `developer` varchar(56) NOT NULL default '',\n" +
                "  `god` varchar(4) NOT NULL default '',\n" +
                "  `god1` varchar(4) NOT NULL default '',\n" +
                "  `ngamers` int(2) default '0',\n" +
                "  `type` varchar(64) NOT NULL default '',\n" +
                "  `genre` varchar(64) NOT NULL default '',\n" +
                "  `image1` varchar(128) default NULL,\n" +
                "  `image2` varchar(128) default NULL,\n" +
                "  `image3` varchar(128) default NULL,\n" +
                "  `image4` varchar(128) default NULL,\n" +
                "  `image5` varchar(128) default NULL,\n" +
                "  `image6` varchar(128) default NULL,\n" +
                "  `image7` varchar(128) default NULL,\n" +
                "  `image8` varchar(128) default NULL,\n" +
                "  `image9` varchar(128) default NULL,\n" +
                "  `image10` varchar(128) default NULL,\n" +
                "  `image11` varchar(128) default NULL,\n" +
                "  `image12` varchar(128) default NULL,\n" +
                "  `image13` varchar(128) default NULL,\n" +
                "  `image14` varchar(128) default NULL,\n" +
                "  `game` varchar(128) NOT NULL default '',\n" +
                "  `downloaded` int(11) unsigned NOT NULL default '0',\n" +
                "  `music` varchar(128) NOT NULL default '',\n" +
                "  `music_downloaded` int(11) unsigned NOT NULL default '0',\n" +
                "  `rom` varchar(128) NOT NULL default '',\n" +
                "  `playable` enum('yes','no') NOT NULL default 'no',\n" +
                "  `played` int(11) unsigned NOT NULL default '0',\n" +
                "  `text1` text NOT NULL,\n" +
                "  `text2` text NOT NULL,\n" +
                "  `analog` varchar(255) NOT NULL default '',\n" +
                "  `drname` varchar(255) NOT NULL default '',\n" +
                "  `cros` varchar(128) default NULL,\n" +
                "  `serie` varchar(128) default NULL,\n" +
                "  `rating` int(3) default '0',\n" +
                "  `userrating` int(11) NOT NULL default '0',\n" +
                "  `totalrating` int(11) NOT NULL default '0',\n" +
                "  `viewes` int(11) NOT NULL default '0',\n" +
                "  `comments` int(11) unsigned NOT NULL default '0',\n" +
                "  PRIMARY KEY  (`cpu`),\n" +
                "  UNIQUE KEY `name` (`name`),\n" +
                "  KEY `n` (`n`),\n" +
                "  KEY `sid` (`sid`),\n" +
                "  KEY `playable` (`playable`)\n" +
                ") ENGINE=MyISAM /*!40101 DEFAULT CHARSET=utf8 */;\n\n";

        String insert = "INSERT INTO `base_%s` VALUES " +
                // n, sys, created, modified, sid, cpu, name, descript, keywords
                // 1, '3do', 0, 1640065258, '1', 'zamechanie-po-baze-dannyh', 'Замечание по базе данных', '', ''
                "(%s, '%s', %s, %s, '%s', '%s', '%s', '%s', '%s', " +
                // region, publisher, developer, god, god1, ngamers, type, genre
                // 'EU;', 'Leonis', '', '2012', '0303', NULL, '', ''
                "'%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', " +
                // image1 - image14
                "'%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', " +
                // game, downloaded, music, music_downloaded, rom, playable, played, text1, text2
                // '', 0, '', 0, '', 'no', 0, 'Стро...', ''
                "'%s', %s, '%s', %s, '%s', '%s', %s, '%s', '%s', " +
                // analog, drname, cros, serie, rating, userrating, totalrating, viewes, comments
                // '', '', '', '', 53, 93, 224, 15246, 1
                "'%s', '%s', '%s', '%s', %s, %s, %s, %s, %s);";

        String sys = "dos";

        String structure = String.format(tableStructure, sys, sys);

        Path correctedListPath = PATH.resolve("Книга1.csv");
        List<String> correctedList = new ArrayList<>();
        if (Files.exists(correctedListPath)) {
            System.out.println("Load: " + correctedListPath);
            correctedList = FileUtils.loadTextFile(correctedListPath);
            correctedList.remove(0);
        } else {
            System.out.println("Первый запуск - обязательно отредактируйте CSV файл, сохраните как Книга1.csv и запустите повторно!");
        }

        List<String> games = new ArrayList<>();
        games.add(structure);
        List<String> titles = new ArrayList<>();

        List<TiviStructure> items = sort(FileUtils.loadJsonList(PATH.resolve("dbsr2").resolve("pc.json"), TiviStructure.class));

        Set<String> cpus = new HashSet<>();
        Set<String> names = new HashSet<>();

        List<TiviStructure> filteredItems = new ArrayList<>();

        int index = 0;

        for (TiviStructure item : items) {
            if (!item.getName().contains("(Application)") && !item.getName().contains("(OS)")) {
                String name = findName(names, item.getName());
                String cpu = findCpu(cpus, StringUtils.generateBooksCpu(name));

                if (!correctedList.isEmpty()) {
                    String[] chunks = correctedList.get(index).split(";");
                    cpu = chunks[0];
                    name = chunks[1];
                }

                name = fixQuote(name);

                item.setName(name);
                item.setCpu(cpu);

                if (StringUtils.isNotBlank(cpu) && StringUtils.isNotBlank(name)) {
                    item.setName(fixDQuotes(item.getName()));
                    item.setCpu(fixDQuotes(item.getCpu()));
                    item.setText1(fixDQuotes(item.getText1()));
                    item.setText2(fixDQuotes(item.getText2()));
                    filteredItems.add(item);
                }
                index++;
            }
        }

        filteredItems = sort(filteredItems);

        index = 0;

        for (int i = 0; i < filteredItems.size(); i++) {
            TiviStructure item = filteredItems.get(i);
            long created = System.currentTimeMillis() / 1000;
            // INSERT INTO `base_coleco` (`n`, `sys`, `created`, `modified`, `sid`, `cpu`, `name`, `descript`, `keywords`,
            // `region`, `publisher`, `developer`, `god`, `god1`, `ngamers`, `type`, `genre`,
            // `image1`, `image2`, `image3`, `image4`, `image5`, `image6`, `image7`, `image8`, `image9`, `image10`, `image11`, `image12`, `image13`, `image14`,
            // `game`, `downloaded`, `music`, `music_downloaded`, `rom`, `playable`, `played`, `text1`, `text2`,
            // `analog`, `drname`, `cros`, `serie`, `rating`, `userrating`, `totalrating`, `viewes`, `comments`) VALUES

            String game = String.format(insert, sys,
                    i + 1, sys, created, created, item.getSid(), item.getCpu(), item.getName(), "", "",
                    item.getRegion(), item.getPublisher(), item.getDeveloper(), item.getGod(), item.getGod1(), item.getNgamers(), item.getType(), item.getGenre(),
                    item.getImage1(), item.getImage2(), item.getImage3(), item.getImage4(), item.getImage5(), item.getImage6(), item.getImage7(),
                    item.getImage8(), item.getImage9(), item.getImage10(), item.getImage11(), item.getImage12(), item.getImage13(), item.getImage14(),
                    "", 0, "", 0, "", "no", 0, item.getText1().replace("\\\"", "\""),
                    item.getText2().replace("\\\"", "\""),
                    item.getAnalog(), item.getDrname(), item.getCros(), item.getSerie(), item.getRating(), 0, 0, 0, 0
            );
            if (StringUtils.isNotBlank(item.getCpu()) && StringUtils.isNotBlank(item.getName())) {
                games.add(game);
            } else {
                System.out.println("Deleted: " + item.getName());
            }
            titles.add(String.format("\"%s\";\"%s\";\"%s\";\"%s\";\"%s\";\"%s\"",
                    item.getCpu(), item.getName(), item.getPublisher(), item.getDeveloper(), item.getGod1() + "." + item.getGod(), item.getText1()));
            index++;
        }

        FileUtils.saveToFile(PATH.resolve(sys + "-create.sql"), games);
        FileUtils.saveToFile(PATH.resolve(sys + "-titles.csv"), titles);
    }

    private String fixDQuotes(String text) {
        if (text.isEmpty() || !text.startsWith("\"")) {
            return text;
        }
        text = text.substring(1);
        text = text.substring(0, text.length() - 1);
        text = text.replace("\"\"", "\"");

        return text;
    }

    private String findCpu(Set<String> cpus, String cpu) {
        if (cpus.contains(cpu.toLowerCase())) {
            cpu = cpu + "_";
            return findCpu(cpus, cpu);
        }
        return cpu;
    }

    private String findName(Set<String> names, String name) {
        if (names.contains(name.toLowerCase())) {
            name = name + "_";
            return findCpu(names, name);
        }
        return name;
    }

    private String fixQuote(String name) {
        return name.replace("'", "`");
    }

    private String fixName(String name) {

        if (name.endsWith("*")) {
            name = name.substring(0, name.length() - 1);
        }
        name = name.replace("\\\"", "\"");
        name = name.replace("(CD)", "");
        name = name.replace("(FDD3.5)", "");
        name = name.replace("(FDD5.25)", "");
        name = name.replace("[DOS]", "");
        name = name.replace("(Coverdisc)", "");
        name = name.replace("(PDI)", "");
        name = name.replace("(M3)", "");
        name = name.replace("(M4)", "");
        name = name.replace("(M5)", "");
        name = name.replace("(M6)", "");
        name = name.replace("(M7)", "");
        name = name.replace("(IMA)", "");
        name = name.replace("(Boot Disk)", "");
        name = name.replace("(CD)", "");
        name = name.replace("(DSK)", "");
        name = name.replace("(JRC)", "");
        name = name.replace("(CP2)", "");
        name = name.replace("(TD0)", "");
        name = name.replace("(EXE)", "");
        name = name.replace("(fr)", "");
        name = name.replace("(de)", "");
        name = name.replace("(it)", "");
        name = name.replace("(en)", "");
        name = name.replace("(de-fr)", "");
        name = name.replace("(en-de)", "");
        name = name.replace("(en-fr)", "");
        name = name.replace("(Disc 2 of 2)", "");
        name = name.replace("(Disk 2 of 2)", "");
        name = name.replace("(Disk 3 of 3)", "");
        name = name.replace("(Disk 4 of 4)", "");
        name = name.replace("(Educational)", "");
        name = name.replace("(1995)", "");
        name = name.replace("(demo)", "");
        name = name.replace("(Compilation)", "");
        name = name.replace("(Deluxe Edition)", "");
        return name.trim();
    }
}
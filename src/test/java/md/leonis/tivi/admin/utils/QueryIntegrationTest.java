package md.leonis.tivi.admin.utils;

import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import md.leonis.tivi.admin.model.calibre.Sql;
import md.leonis.tivi.admin.model.media.Book;
import md.leonis.tivi.admin.model.mysql.Field;
import md.leonis.tivi.admin.model.mysql.TableStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertTrue;
import static md.leonis.tivi.admin.utils.BookUtils.queryRequest;
import static md.leonis.tivi.admin.utils.BookUtils.rawQueryRequest;
import static org.junit.Assert.assertEquals;

public class QueryIntegrationTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testBook() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();
        BookUtils.calibreBooks = CalibreUtils.readBooks();
        Book book = BookUtils.calibreBooks.get(0);

        String json = JsonUtils.gson.toJson(book, Book.class);
        System.out.println(json);

        CalibreUtils.executeQuery("DROP TABLE IF EXISTS `books2`");

        String query = CalibreUtils.getCreateTableQuery("books");
        Sql sql = CalibreUtils.readObject(query, Sql.class);
        CalibreUtils.executeQuery(sql.getSql().replace("books", "books2"));

        query = CalibreUtils.getInsertQuery("books2", book, Book.class);
        CalibreUtils.executeInsertQuery(query);

        Book book2 = CalibreUtils.readObject("SELECT * FROM `books2` WHERE id = 1", Book.class);

        assertEquals(json, JsonUtils.gson.toJson(book2, Book.class));

        Book book3 = new Book(256L, "uTitle", "uSort", LocalDateTime.now(), LocalDateTime.now(), 256.0, "uAuthorSort",
                "unusedIsbn", "unusedLccn", "uPath", 256, "uUuid", 256, LocalDateTime.now());

        assertTrue(CalibreUtils.isEquals(book2, book2));

        Book book4 = new Book(256L, "uTitle2", "uSort", LocalDateTime.now(), LocalDateTime.now(), 256.0, "uAuthorSort",
                "unusedIsbn", "unusedLccn", "uPath", 257, "uUuid", 256, LocalDateTime.now());

        Map<String, Pair<JsonPrimitive, JsonPrimitive>> diff = CalibreUtils.getDiff(book3, book4);
        assertEquals(diff.size(), 5);
        assertEquals(diff.get("flags").getKey().getAsInt(), 256);
        assertEquals(diff.get("flags").getValue().getAsInt(), 257);
        assertEquals(diff.get("title").getKey().getAsString(), "uTitle");
        assertEquals(diff.get("title").getValue().getAsString(), "uTitle2");

        Book book5 = new Book(256L, "uTitle22~`!@#$%^&*()_+-={}[]:\";'<>?,./<><", "uSort", LocalDateTime.now(), LocalDateTime.now(), 256.0, "uAuthorSort",
                "unusedIsbn", "unusedLccn", "uPath", 257, "uUuid", 256, LocalDateTime.now());

        query = CalibreUtils.getInsertQuery("books2", book5, Book.class);
        Integer id = CalibreUtils.executeInsertQuery(query);
        assertEquals(id.intValue(), 256);

        List<Book> books = CalibreUtils.readObjectList("SELECT * FROM `books2`", Book.class);
        System.out.println(books);

        String updateQuery = CalibreUtils.getUpdateQuery("books2", book4, book5);
        CalibreUtils.executeUpdateQuery(updateQuery + "id = 256");

        Book book6 = CalibreUtils.readObject("SELECT * FROM `books2` WHERE id = 256", Book.class);
        assertTrue(CalibreUtils.isEquals(book5, book6));

        try (Writer writer = new FileWriter("file.txt")) {
            JsonUtils.gson.toJson(book6, writer);
        }

        try (Reader reader = new FileReader("file.txt")) {
            Book book7 = JsonUtils.gson.fromJson(reader, Book.class);
            assertEquals(book6, book7);
            assertTrue(CalibreUtils.isEquals(book6, book7));
        }


        CalibreUtils.executeQuery("DROP TABLE `books2`");

        //BookUtils.selectBooks();
        //BookUtils.dumpDB();
        //BookUtils.queryRequest("SELECTъ пропро COUNT(downid) AS count FROM `danny_media`");
            /*CalibreBook calibreBook = new CalibreBook();
            calibreBook.setBbk("sdfdsfdsf");
            calibreBook.setId(12L);
            calibreBook.setLastModified(LocalDateTime.now());
            calibreBook.setLanguages(Arrays.asList(new Language(1L, "dfdfd")));
            System.out.println(BookUtils.getInsertQuery(calibreBook, CalibreBook.class));*/
        //BookUtils.queryOperation(String.format("INSERT INTO `danny_info` VALUES(NULL, \"%s\", \"%s\", \"%s\")", "протест", BookUtils.prepareQuery("текст \" ' &^%$#@!*(<>{} fff"), "as"));
        //BookUtils.queryOperation("DELETE FROM `danny_info` WHERE infoid = 213");
    }

    @Test
    public void testComparator() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();
        BookUtils.queryRequest("ASD");
        Config.loadProperties();
        Config.loadProtectedProperties();
        //TODO
        //ComparisionResult comparisionResult = CalibreUtils.compare();
        //System.out.println(comparisionResult);
    }

    public void dumpDBAsNativeSql(String tableName) {
        new File(Config.workPath + "nat").mkdirs();
        int count = 0;
        int maxTries = 15;
            while (true) {
                try {
                    String requestURL = Config.apiPath + String.format("dumper.php?action=backup&drop_table=true&create_table=true&tables=%s&format=sql&comp_level=9&comp_method=1", tableName);
                    String queryId = WebUtils.readFromUrl(requestURL);
                    System.out.println(queryId);
                    String fileName = Config.apiPath + "backup/" + queryId + ".sql.gz";
                    File newFile = new File(Config.workPath + "nat" + File.separatorChar + tableName + ".txt");
                    gunzipIt(fileName, newFile);
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        Thread.sleep(1000 * (count + 1) * 3);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    if (++count == maxTries) throw new RuntimeException(e);
                }
            }
    }

    @Test
    public void testDumper() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();

        new File(Config.workPath + "gen").mkdirs();

        Type type = new TypeToken<List<TableStatus>>() {}.getType();
        List<TableStatus> tableStatuses = JsonUtils.gson.fromJson(queryRequest("SHOW TABLE STATUS"), type);

        for (TableStatus table : tableStatuses) {
            if (!table.getName().startsWith("vv_post")) {
                continue;
            }
            System.out.println(table.getName());

            //dumpDBAsNativeSql(table.getName());

            List<String> jsons = new ArrayList<>();
            long offset = 0;
            long limit = 4000;
            String result;
            do {
                int count = 0;
                int maxTries = 15;
                while (true) {
                    try {
                        System.out.println(offset);
                        String requestURL = Config.apiPath + String.format("dumper.php?action=backup&tables=%s&offset=%d,%d&format=json&comp_level=9&comp_method=1", table.getName(), offset, limit);
                        String queryId = WebUtils.readFromUrl(requestURL);
                        System.out.println(queryId);
                        String fileName = Config.apiPath + "backup/" + queryId + ".sql.gz";
                        result = gunzipIt(fileName);
                        break;
                    } catch (Exception e) {
                        System.out.println("#" + e.getMessage());
                        try {
                            Thread.sleep(1000 * (count + 1) * 3);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        if (++count == maxTries) throw new RuntimeException(e);
                    }
                }
                result = result.substring(1, result.length() - 1).trim();
                jsons.add(result);
                offset += limit;
                System.out.println(result.substring(0, result.length() > 256 ? 256 : result.length()).trim());
            } while (!result.isEmpty());

                    /*try (FileOutputStream fos = new FileOutputStream("E:\\" + table.getName() + "-gen.txt")) {
                        fos.write(BookUtils.ascii2Native(result).getBytes(charset));
                        fos.close();
                    }*/
            //Type REVIEW_TYPE = new TypeToken<List<Video>>() {}.getType();
            //JsonReader reader = new JsonReader(new FileReader(newFile));
            //List<Video> data = JsonUtils.gson.fromJson(result, REVIEW_TYPE); // contains the whole reviews list
            //System.out.println(data); // prints to screen some values

            //TODO to sql
            try (FileOutputStream fos = new FileOutputStream(Config.workPath + "gen" + File.separatorChar + table.getName() + ".txt")) {
                //queryRequest("SHOW COLUMNS FROM `danny_media`");
                //queryRequest("SHOW TABLE STATUS");
                jsonToSqlInsertQuery("[" + jsons.stream().filter(s -> !s.isEmpty()).collect(joining(",")) + "]", fos, table.getName());
            }
        }
        //TODO compare

        //http://tv-games.ru/api2d/dumper.php?action=backup&db_backup=alenka975_wiki&drop_table=false&create_table=false&where=downid%3C4&tables=danny_media&format=json&comp_level=9&comp_method=1&as=danny_media2
    }

    private static void jsonToSqlInsertQuery(String json, FileOutputStream fos, String pattern) throws IOException {
        Type type = new TypeToken<List<TableStatus>>() {
        }.getType();
        List<TableStatus> tableStatuses = JsonUtils.gson.fromJson(queryRequest("SHOW TABLE STATUS"), type);

        for (TableStatus table : tableStatuses) {
            if (!table.getName().matches(pattern)) {
                continue;
            }
            // TODO Выставляем кодировку соединения соответствующую кодировке таблицы
            // Создание таблицы
            String charset = table.getCollation().split("_")[0];
            // Fix bad charset ;)
            /*if (table.getName().startsWith("danny_")) {
                charset = "cp1251";
            }*/
            String result = rawQueryRequest(String.format("SHOW CREATE TABLE `%s`", table.getName()));
            result = result.replaceAll("(?i)(DEFAULT CHARSET=\\w+|COLLATE=\\w+)", "/*!40101 $1 */;");
            result = result.replaceAll("(?i)(default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP|collate \\w+|character set \\w+)", "/*!40101 $1 */");
            fos.write(String.format("DROP TABLE IF EXISTS `%s`;\n", table.getName()).getBytes(charset));
            fos.write((result + "\n\n").getBytes(charset));
            // Опредеделяем типы столбцов
            result = queryRequest(String.format("SHOW COLUMNS FROM `%s`", table.getName()));
            Type fieldType = new TypeToken<List<Field>>() {
            }.getType();
            List<Field> fields = JsonUtils.gson.fromJson(result, fieldType);
            //TODO other numeric https://dev.mysql.com/doc/refman/5.7/en/numeric-types.html
            List<String> numericColumns = fields.stream().filter(t -> t.getType().matches("^(\\w*int.*)")).map(Field::getField).collect(toList());
            List<String> blobColumns = fields.stream().filter(t -> t.getType().matches("^(\\w*blob.*)")).map(Field::getField).collect(toList());

            boolean isFirst = true;
            type = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> rows = JsonUtils.gson.fromJson(json, type);

            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    String values = row.entrySet().stream().map(field -> {
                        if (numericColumns.contains(field.getKey())) {
                            //$row[$k] = isset($row[$k]) ? $row[$k] : "NULL";
                            if (field.getValue() == null) {
                                return "NULL";
                            }
                            String v = field.getValue().toString();
                            Long value = v.isEmpty() ? null : Long.valueOf(v);
                            if (value == null) {
                                return "NULL";
                            }
                            return value.toString();
                        } else if (blobColumns.contains(field.getKey())) {
                            if (field.getValue() == null) {
                                return "NULL";
                            }
                            return "'" + field.getValue().toString().replace("\"", "\\\"") + "'";
                        } else {
                            //TODO
                            //$row[$k] = isset($row[$k]) ? "'".mysql_escape_string($row[$k]). "'" :"NULL";
                            if (field.getValue() == null) {
                                return "NULL";
                            }
                            return "'" + field.getValue().toString()
                                    .replace("\\", "\\\\")
                                    .replace("\"", "\\\"")
                                    .replace("'", "\\'")
                                    .replace("\r\n", "\\r\\n")
                                    //.replace("\r\n", "\\r\\n")
                                    .replace("\n", "\\n")
                                    //.replace("" + ((char) 0), "\\0")
                                    + "'";
                        }
                    }).collect(Collectors.joining(", "));

                    if (!isFirst) {
                        fos.write((",\n").getBytes(charset));
                    } else {
                        fos.write(String.format("INSERT INTO `%s` VALUES\n", table.getName()).getBytes(charset));
                    }
                    fos.write(("(" + values + ")").getBytes(charset));
                    isFirst = false;
                }
            }
            if (!isFirst) {
                fos.write((";\n\n").getBytes(charset));
            }
        }
    }

    public void gunzipIt(String fileName, File newFile) {
        byte[] buffer = new byte[1024];
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


    public String gunzipIt(String fileName) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try (Reader in = new InputStreamReader(new GZIPInputStream(new URL(fileName).openStream()), "UTF-8")) {
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
package md.leonis.tivi.admin.utils;

import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import md.leonis.tivi.admin.model.danneo.Video;
import unneeded.model.calibre.Sql;
import md.leonis.tivi.admin.model.calibre.Book;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class QueryIntegrationTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testText() {
        Document document = Jsoup.parseBodyFragment("<div><p>123</p><hr><p>123</p></div>");
        System.out.println(document);
        document = Jsoup.parseBodyFragment("<div><p>123</p>");
        System.out.println(document);
        document = Jsoup.parseBodyFragment("<p>123</p></div>");
        System.out.println(document);
    }


    @Test
    public void testBook() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();
        CalibreUtils calibreUtils = new CalibreUtils();
        List<CalibreBook> calibreBooks = calibreUtils.readBooks();
        Book book = calibreBooks.get(0);

        String json = JsonUtils.gson.toJson(book, Book.class);
        System.out.println(json);



        calibreUtils.executeQuery("DROP TABLE IF EXISTS `books2`");

        String query = calibreUtils.getCreateTableQuery("books");
        Sql sql = calibreUtils.readObject(query, Sql.class);
        calibreUtils.executeQuery(sql.getSql().replace("books", "books2"));

        query = calibreUtils.getInsertQuery("books2", book, Book.class);
        calibreUtils.executeInsertQuery(query);

        Book book2 = calibreUtils.readObject("SELECT * FROM `books2` WHERE id = 1", Book.class);

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

        query = calibreUtils.getInsertQuery("books2", book5, Book.class);
        Integer id = calibreUtils.executeInsertQuery(query);
        assertEquals(id.intValue(), 256);

        List<Book> books = calibreUtils.readObjectList("SELECT * FROM `books2`", Book.class);
        System.out.println(books);

        String updateQuery = calibreUtils.getUpdateQuery("books2", book4, book5);
        calibreUtils.executeUpdateQuery(updateQuery + "id = 256");

        Book book6 = calibreUtils.readObject("SELECT * FROM `books2` WHERE id = 256", Book.class);
        assertTrue(CalibreUtils.isEquals(book5, book6));

        try (Writer writer = new FileWriter("file.txt")) {
            JsonUtils.gson.toJson(book6, writer);
        }

        try (Reader reader = new FileReader("file.txt")) {
            Book book7 = JsonUtils.gson.fromJson(reader, Book.class);
            assertEquals(book6, book7);
            assertTrue(CalibreUtils.isEquals(book6, book7));
        }


        calibreUtils.executeQuery("DROP TABLE `books2`");

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

/*    @Test
    public void testTextMigration() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();

        CalibreUtils.readBooks().forEach(book -> {
            String text = book.getTextShort() == null ? "" : book.getTextShort();
            text += "<hr>";
            text += book.getTextMore() == null ? "" : book.getTextMore();
            String updateQuery = String.format("UPDATE `comments` SET text = '%s' WHERE book = %d", CalibreUtils.escape(text), book.getId());
            CalibreUtils.executeUpdateQuery(updateQuery);
        });
    }*/

    @Test
    public void testComparator() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();
        SiteDbUtils.queryRequest("ASD");
        //TODO
        //CalibreComparisionResult comparisionResult = CalibreUtils.compare();
        //System.out.println(comparisionResult);
    }

//    @Test
//    public void testDumper() throws IOException, NoSuchAlgorithmException {
//        for (TableStatus table : BookUtils.tableStatuses) {
//            System.out.println(table.getName());
//
//            if (table.getName().startsWith("vv_socialgroupicon") || queryExist(table.getName())) {
//                System.out.println("Skipped: " + table.getName());
//                continue;
//            }
//            BookUtils.dumpDBAsNativeSql(table.getName());
//            String json = BookUtils.dumpBaseAsJson(table);
//            generateInsertQueries(json, table, table.getName());
//        }
//        //http://tv-games.ru/api2d/dumper.php?action=backup&db_backup=alenka975_wiki&drop_table=false&create_table=false&where=downid%3C4&tables=danny_media&format=json&comp_level=9&comp_method=1&as=danny_media2
//    }

    private boolean queryExist(String tableName) throws IOException, NoSuchAlgorithmException {
        File f = new File(Config.outputPath + File.separatorChar + tableName + ".txt");
        if (!f.exists()) {
            return false;
        }
        byte[] b = Files.readAllBytes(f.toPath());
        byte[] hash = MessageDigest.getInstance("MD5").digest(b);
        String expected = DatatypeConverter.printHexBinary(hash);


        f = new File(Config.outputPath + File.separatorChar + tableName + ".txt");
        if (!f.exists()) {
            return false;
        }
        b = Files.readAllBytes(f.toPath());
        hash = MessageDigest.getInstance("MD5").digest(b);
        String actual = DatatypeConverter.printHexBinary(hash);
        return expected.equalsIgnoreCase(actual);
    }

//    public void generateInsertQueries(String json, TableStatus table, String as) throws IOException {
//        try (FileOutputStream fos = new FileOutputStream(Config.workPath + File.separatorChar + table.getName() + ".txt")) {
//            jsonToSqlInsertQuery(json, fos, table, as);
//        }
//    }
//
//    private static void jsonToSqlInsertQuery(String json, OutputStream fos, TableStatus table, String as) throws IOException {
//        String charset = table.getCollation().split("_")[0];
//        // Fix bad charset ;)
//            /*if (table.getName().startsWith("danny_")) {
//                charset = "cp1251";
//            }*/
//        String result = rawQueryRequest(String.format("SHOW CREATE TABLE `%s`", table.getName()));
//        result = result.replaceAll("(?i)(DEFAULT CHARSET=\\w+|COLLATE=\\w+)", "/*!40101 $1 */;");
//        result = result.replaceAll("(?i)(default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP|collate \\w+|character set \\w+)", "/*!40101 $1 */");
//        fos.write(String.format("DROP TABLE IF EXISTS `%s`;\n", as).getBytes(charset));
//        fos.write((result.replace(table.getName(), as) + "\n\n").getBytes(charset));
//
//
//        fos.write(BookUtils.jsonToSqlInsertQuery(json, as, new ColumnsResolver(table.getName())).getBytes(charset));
//    }

    @Test
    public void testInsertQuery() throws IOException {
        Video video = new Video();
        video.setId(null);
        video.setAge("age");
        video.setComments(2);
        video.setYid("yid");
        video.setCpu("cpu");
        video.setUrl("url");
        video.setMirror("exturl");
        video.setImage("image");
        video.setImageAlt("image_alt");
        video.setFullText("textmore");
        video.setOpenGraphImage("image_thumb");
        video.setDescription("Комментъ comment \" ' ~`!@#$%^&*()_+-={}[]:;<>?,./\\| <>< \n\r\t <p>asd</p>");
        video.setPreviousImage("previousImage");
        //BookUtils.queryOperation("DELETE FROM danny_media WHERE cpu='cpu'");
        String insertQuery = SiteDbUtils.objectToSqlInsertQuery(video, Video.class, "danny_media");
        System.out.println(insertQuery);
        //TODO compress
        String result = WebUtils.readFromUrl("http://tv-games.ru/api2d/upload2.php?to=clean_backups");
        System.out.println(result);
        String fileName = UUID.randomUUID().toString() + ".sql";
        result = SiteDbUtils.upload("api2d/backup", fileName, new ByteArrayInputStream(insertQuery.getBytes(StandardCharsets.UTF_8)));
        System.out.println(result);
        result = WebUtils.readFromUrl("http://tv-games.ru/api2d/dumper.php?to=restore&file=" + fileName);
        System.out.println(result);
        Integer id = getIdByCpu("cpu");
        System.out.println(id);
        SiteDbUtils.queryRequest("DELETE FROM danny_media WHERE cpu='cpu'");
    }

    public Integer getIdByCpu(String cpu) {
        String result = SiteDbUtils.queryRequest(String.format("SELECT * FROM `danny_media` WHERE cpu='%s'", cpu));
        Type fieldType = new TypeToken<List<Video>>() {}.getType();
        List<Video> ids = JsonUtils.gson.fromJson(result, fieldType);
        if (ids.isEmpty()) {
            throw new RuntimeException("Empty result, probably, failed query");
        }
        if (ids.size() > 1) {
            throw new RuntimeException("Too many results, CPU not unique");
        }
        return ids.get(0).getId();
    }



}
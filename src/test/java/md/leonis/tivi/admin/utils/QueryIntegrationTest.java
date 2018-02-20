package md.leonis.tivi.admin.utils;

import com.google.gson.JsonPrimitive;
import javafx.util.Pair;
import md.leonis.tivi.admin.model.calibre.Sql;
import md.leonis.tivi.admin.model.media.Book;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
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

}
package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.model.calibre.Sql;
import md.leonis.tivi.admin.model.media.Book;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class QueryIntegrationTest {

    @Test
    public void testBook() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();
        CalibreUtils.readBooks();
        Book book = BookUtils.calibreBooks.get(0);

        String json = JsonUtils.gson.toJson(book, Book.class);
        System.out.println(json);

        CalibreUtils.executeQuery("DROP TABLE IF EXISTS `books2`");

        String query = CalibreUtils.getCreateTableQuery("books");
        Sql sql = CalibreUtils.readObject(query, Sql.class);
        CalibreUtils.executeQuery(sql.getSql().replace("books", "books2"));

        query = CalibreUtils.getInsertQuery("books2", book, Book.class);
        CalibreUtils.executeQuery(query);

        Book book2 = CalibreUtils.readObject("SELECT * FROM `books2` WHERE id = 1", Book.class);

        assertEquals(json, JsonUtils.gson.toJson(book2, Book.class));

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
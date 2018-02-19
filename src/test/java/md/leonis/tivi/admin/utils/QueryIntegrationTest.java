package md.leonis.tivi.admin.utils;

import org.junit.Test;

import java.io.IOException;

public class QueryIntegrationTest {

    @Test
    public void testBook() throws IOException {
        Config.loadProperties();
        Config.loadProtectedProperties();
        CalibreUtils.readBooks();
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
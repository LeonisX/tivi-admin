package md.leonis.tivi.admin.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import lombok.SneakyThrows;
import md.leonis.tivi.admin.model.ArchiveEntry;
import md.leonis.tivi.admin.model.ComparisionResult;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.Comment;
import md.leonis.tivi.admin.model.calibre.*;
import md.leonis.tivi.admin.model.calibre.links.*;
import md.leonis.tivi.admin.renderer.MagazinesCitationRenderer;
import md.leonis.tivi.admin.utils.archive.RarUtils;
import md.leonis.tivi.admin.utils.archive.SevenZipUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.stream.Collectors.*;
import static md.leonis.tivi.admin.model.Type.*;
import static md.leonis.tivi.admin.utils.StringUtils.*;

public class CalibreUtils {

    private final String calibreDbFullPath;

    public CalibreUtils() {
        calibreDbFullPath = Config.calibreDbFullPath;
    }

    public CalibreUtils(String dbFileName) {
        this.calibreDbFullPath = dbFileName;
    }

    /*PRAGMA index_list(books);
    PRAGMA index_xinfo(books_idx);
    PRAGMA stats;
    PRAGMA table_info(books);
    SELECT sql FROM sqlite_master WHERE name='books'*/

    // Tests only
    public String getCreateTableQuery(String tableName) {
        return String.format("SELECT sql FROM sqlite_master WHERE name='%s'", tableName);
    }

    private static String getJdbcString(String dbFileName) {
        return String.format("jdbc:sqlite:%s", dbFileName); // "jdbc:sqlite:E:\\metadata.db"
    }

    public List<CalibreBook> readBooks() {
        if (Config.debugMode) {
            return readBooksFromFile();
        } else {
            return readBooksFromDb();
        }
    }

    public List<CalibreBook> readBooksFromFile() {
        try {
            Path path = Paths.get(calibreDbFullPath);
            path = path.resolveSibling(path.getFileName() + ".json");
            if (Files.exists(path)) {
                Reader reader = Files.newBufferedReader(path);
                return JsonUtils.gson.fromJson(reader, new TypeToken<List<CalibreBook>>() {
                }.getType());
            } else {
                List<CalibreBook> books = readBooksFromDb();
                FileWriter writer = new FileWriter(path.toFile());
                JsonUtils.gson.toJson(books, writer);
                writer.flush();
                return books;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return readBooksFromDb();
        }
    }

    public List<CalibreBook> readBooksFromDb() {
        List<CalibreBook> calibreBooks = selectAllFrom("books", CalibreBook.class);

        List<Comment> comments = selectAllFrom("comments", Comment.class);
        calibreBooks.forEach(calibreBook -> {
            String comment = comments.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Comment::getText).findFirst().orElse(null);
            calibreBook.setComment(comment);
            if (comment == null) {
                calibreBook.setTextShort("");
                calibreBook.setTextMore("");
            } else {
                String[] chunks = comment.split("<hr>");
                //calibreBook.setTextShort(chunks[0].trim());
                Element element = Jsoup.parseBodyFragment(chunks[0]).body();
                if (element.childNodeSize() == 0) {
                    calibreBook.setTextShort(element.html().trim());
                } else if (element.childrenSize() == 0) {
                    calibreBook.setTextShort(element.html().trim());
                } else if (element.child(0).tagName().equals("div") && element.child(0).childNodeSize() == 1) {
                    calibreBook.setTextShort(element.child(0).html().trim());
                } else {
                    calibreBook.setTextShort(element.child(0).outerHtml().trim().replace("<p></p>\n", "").replace("<p></p>", ""));
                }

                if (chunks.length > 1) {
                    calibreBook.setTextMore(Jsoup.parseBodyFragment(chunks[1]).body().html().trim());
                } else {
                    calibreBook.setTextMore("");
                }
            }
            if (calibreBook.getTextMore() == null) {
                calibreBook.setTextMore("");
            }
        });

        List<AuthorLink> bookAuthors = selectAllFrom("books_authors_link", AuthorLink.class);
        List<Author> authors = selectAllFrom("authors", Author.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = bookAuthors.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(AuthorLink::getAuthor).collect(toList());
            calibreBook.setAuthors(authors.stream().filter(author -> ids.contains(author.getId())).collect(toList()));
        });

        //TODO add to Integer[] c = {1, 2, 4, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22}; after august release
        List<Link> links22 = null;
        try {
            links22 = selectAllFrom("books_custom_column_22_link", Link.class);
        } catch (Exception ignored) {
        }
        List<Link> linkk22 = links22;
        Integer[] c = {1, 2, 4, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21/*, 22*/};

        Map<Integer, List<Link>> links = Arrays.stream(c).collect(Collectors.toMap(i -> i, i -> selectAllFrom("books_custom_column_" + i + "_link", Link.class)));

        List<LanguageLink> languageLinks = selectAllFrom("books_languages_link", LanguageLink.class);
        List<PublisherLink> publisherLinks = selectAllFrom("books_publishers_link", PublisherLink.class);
        List<RatingLink> ratingLinks = selectAllFrom("books_ratings_link", RatingLink.class);
        List<SerieLink> serieLinks = selectAllFrom("books_series_link", SerieLink.class);
        List<TagLink> tagLinks = selectAllFrom("books_tags_link", TagLink.class);

        List<CustomColumn> isbns = selectAllFrom("custom_column_1", CustomColumn.class);
        List<CustomColumn> bbks = selectAllFrom("custom_column_10", CustomColumn.class);
        List<CustomColumn> formats = selectAllFrom("custom_column_11", CustomColumn.class);
        List<CustomColumn> sources = selectAllFrom("custom_column_12", CustomColumn.class);
        List<CustomColumn> officialTitles = selectAllFrom("custom_column_13", CustomColumn.class);

        List<CustomColumn> types = selectAllFrom("custom_column_14", CustomColumn.class);
        List<CustomColumn> companies = selectAllFrom("custom_column_15", CustomColumn.class);
        List<CustomColumn> udks = selectAllFrom("custom_column_2", CustomColumn.class);
        List<Link> editions = selectAllFrom("custom_column_3", Link.class);
        List<CustomColumn> postprocessings = selectAllFrom("custom_column_4", CustomColumn.class);

        List<SignedInPrint> signedInPrint = selectAllFrom("custom_column_5", SignedInPrint.class);
        List<CustomColumn> fileNames = selectAllFrom("custom_column_6", CustomColumn.class);
        List<CustomColumn> scannedBys = selectAllFrom("custom_column_7", CustomColumn.class);
        List<CustomColumn> externalLinks = selectAllFrom("custom_column_21", CustomColumn.class);
        //TODO simplify after release
        List<CustomColumn> ggr = null;
        if (linkk22 != null) {
            ggr = selectAllFrom("custom_column_22", CustomColumn.class);
        }
        List<CustomColumn> groups = ggr;
        List<Link> pages = selectAllFrom("custom_column_8", Link.class);
        List<Own> owns = selectAllFrom("custom_column_9", Own.class);

        List<CustomColumn> cpus = selectAllFrom("custom_column_16", CustomColumn.class);
        List<Link> tiviIds = selectAllFrom("custom_column_17", Link.class);

        calibreBooks.forEach(calibreBook -> {
            List<Long> ids1 = links.get(1).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setIsbn(isbns.stream().filter(i -> ids1.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            List<Long> ids10 = links.get(10).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setBbk(bbks.stream().filter(i -> ids10.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            List<Long> ids11 = links.get(11).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setFormat(formats.stream().filter(i -> ids11.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            List<Long> ids12 = links.get(12).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setSource(sources.stream().filter(i -> ids12.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            List<Long> ids13 = links.get(13).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setOfficialTitle(officialTitles.stream().filter(i -> ids13.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            List<Long> ids14 = links.get(14).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setType(types.stream().filter(i -> ids14.contains(i.getId())).findFirst().map(CustomColumn::getValue).map(Type::fromString).orElse(null));

            List<Long> ids15 = links.get(15).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setCompany(companies.stream().filter(i -> ids15.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            List<Long> ids2 = links.get(2).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setUdk(udks.stream().filter(i -> ids2.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            calibreBook.setEdition(editions.stream().filter(e -> e.getBook().equals(calibreBook.getId())).findFirst().map(a -> a.getLongValue().intValue()).orElse(null));

            List<Long> ids4 = links.get(4).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setPostprocessing(postprocessings.stream().filter(i -> ids4.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            calibreBook.setSignedInPrint(signedInPrint.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(SignedInPrint::getValue).findFirst().orElse(null));

            List<Long> ids6 = links.get(6).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setFileName(fileNames.stream().filter(i -> ids6.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            List<Long> ids21 = links.get(21).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setExternalLink(externalLinks.stream().filter(i -> ids21.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            List<Long> ids7 = links.get(7).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setScannedBy(scannedBys.stream().filter(i -> ids7.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            calibreBook.setPages(pages.stream().filter(a -> a.getBook().equals(calibreBook.getId())).findFirst().map(a -> a.getLongValue().intValue()).orElse(null));
            calibreBook.setOwn(owns.stream().filter(a -> a.getBook().equals(calibreBook.getId())).findFirst().map(Own::getValue).orElse(null));

            calibreBook.setTiviId(tiviIds.stream().filter(a -> a.getBook().equals(calibreBook.getId())).findFirst().map(Link::getLongValue).orElse(null));

            List<Long> ids16 = links.get(16).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setCpu(cpus.stream().filter(i -> ids16.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            //TODO add to Integer[] c = {1, 2, 4, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22}; after august release
            if (groups != null) {
                List<Long> ids22 = linkk22.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
                //List<Long> ids22 = links.get(22).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
                calibreBook.setGroup(groups.stream().filter(i -> ids22.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));
            }
        });

        //List<CustomColumns> customColumns = selectAllFrom("custom_columns", CustomColumns.class);

        List<Data> dataList = selectAllFrom("data", Data.class);
        calibreBooks.forEach(calibreBook -> calibreBook.setDataList(dataList.stream().filter(a -> a.getBook().equals(calibreBook.getId())).collect(toList())));

        List<Identifier> identifierList = selectAllFrom("identifiers", Identifier.class);
        calibreBooks.forEach(calibreBook -> calibreBook.setIdentifiers(identifierList.stream().filter(a -> a.getBook().equals(calibreBook.getId())).collect(toList())));

        List<Language> languageList = selectAllFrom("languages", Language.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = languageLinks.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(LanguageLink::getCode).collect(toList());
            calibreBook.setLanguages(languageList.stream().filter(l -> ids.contains(l.getId())).collect(toList()));
        });

        List<PublisherSeries> publisherList = selectAllFrom("publishers", PublisherSeries.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = publisherLinks.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(PublisherLink::getPublisher).collect(toList());
            calibreBook.setPublisher(publisherList.stream().filter(author -> ids.contains(author.getId())).findFirst().orElse(null));
        });

        List<Rating> ratingList = selectAllFrom("ratings", Rating.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = ratingLinks.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(RatingLink::getRating).collect(toList());
            calibreBook.setRating(ratingList.stream().filter(author -> ids.contains(author.getId())).findFirst().orElse(null));
        });

        List<PublisherSeries> seriesList = selectAllFrom("series", PublisherSeries.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = serieLinks.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(SerieLink::getSeries).collect(toList());
            calibreBook.setSeries(seriesList.stream().filter(s -> ids.contains(s.getId())).findFirst().orElse(null));
        });

        List<Tag> tagList = selectAllFrom("tags", Tag.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = tagLinks.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(TagLink::getTag).collect(toList());
            calibreBook.setTags(tagList.stream().filter(t -> ids.contains(t.getId())).collect(toList()));
        });

        List<CustomColumn> altTags = selectAllFrom("custom_column_19", CustomColumn.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = links.get(19).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setAltTags(altTags.stream().filter(t -> ids.contains(t.getId())).collect(toList()));
        });

        List<CustomColumn> releaseNotes = selectAllFrom("custom_column_20", CustomColumn.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = links.get(20).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setReleaseNote(releaseNotes.stream().filter(t -> ids.contains(t.getId())).findFirst().map(CustomColumn::getValue).orElse(null));
        });

        calibreBooks.forEach(calibreBook -> {
            if (calibreBook.getScannedBy() != null && calibreBook.getScannedBy().equals(calibreBook.getPostprocessing())) {
                calibreBook.setPostprocessing(null);
            }
        });

        //calibreBooks.forEach(System.out::println);
        System.out.println("Readed books: " + calibreBooks.size());
        String validChars = " qwertyuiopasdfghjklzxcvbnmйцукенгшщзхъёфывапролджэячсмитьбю1234567890-_=+[{]};:'\",<.>/?!@#$%^&*()`~|©«®°µ»‘’“”•…№™u000a";
        String validLatinChars = "üōū";

        //TODO rollback, fix all descriptions не работает чистка описаний в аудите
        /*String allChars = validChars + validChars.toUpperCase();
        calibreBooks.forEach(b -> {
            String text = b.getTitle() + b.getAuthors().stream().map(Author::getName).collect(joining()) + b.getComment();
            text.chars().mapToObj(i -> (char) i).forEach(ch -> {
                if (allChars.indexOf(ch) == -1 && validLatinChars.indexOf(ch) == -1 && ch != 10 && ch != 0xA0) { // неразрывный пробел TODO менять на обычный
                    int id = ch;
                    System.out.println(ch + " = " + String.format("&#x%x;", id));
                    System.out.println(text);
                    System.out.println("Dirty chars!");
                    System.exit(256);
                }
            });
        });*/

        preprocessBooks(calibreBooks);

        return calibreBooks;
    }

    private void preprocessBooks(List<CalibreBook> calibreBooks) {
        calibreBooks.forEach(this::setSiteCpu);
        calibreBooks.forEach(this::setSiteUri);
        calibreBooks.forEach(this::setSiteThumbUri);
        calibreBooks.forEach(this::setSiteCoverUri);

        /*calibreBooks.forEach(b -> {
            if (b.getFileName() != null) {
                b.setFileName(b.getFileName().replace(":", " -"));
            }
        });*/
    }

    public static String getMagazineTitle(CalibreBook book) {
        if (book.getGroup() == null) {
            return book.getSeries().getName();
        } else {
            return book.getGroup();
        }
    }

    private void setSiteCpu(CalibreBook book) {
        if (book.getType().equals(MAGAZINE) || book.getType().equals(COMICS) || book.getGroup() != null) {
            book.setSiteCpu(getMagazineCpu(book));
        } else {
            book.setSiteCpu(getBookSiteCpu(book, BookUtils.getCategoryByTags(book)));
        }
    }

    public static String getMagazineCpu(CalibreBook calibreBook) {
        if (calibreBook.getType().equals(COMICS)) {
            return calibreBook.getCpu();
        } else {
            if (calibreBook.getGroup() != null) {
                return generateCpu(calibreBook.getGroup());
            } else if (calibreBook.getSeries() != null) {
                return generateCpu(calibreBook.getSeries().getName());
            } else {
                return generateCpu(calibreBook.getTitle());
            }
        }
    }

    private String getBookSiteCpu(CalibreBook calibreBook, String category) {
        if (calibreBook.getTags().size() > 1 && calibreBook.belongsToCategory(category)) {
            return category + "_" + calibreBook.getCpu();
        } else {
            return calibreBook.getCpu();
        }
    }

    private void setSiteUri(CalibreBook book) {
        book.setSiteUri(SiteRenderer.generateSiteUri(book));
    }

    private void setSiteThumbUri(CalibreBook book) {
        book.setSiteThumbUri(SiteRenderer.generateBookThumbUri(BookUtils.getCategoryByTags(book), book.getCpu()));
    }

    private void setSiteCoverUri(CalibreBook book) {
        book.setSiteCoverUri(SiteRenderer.generateBookCoverUri(BookUtils.getCategoryByTags(book), book.getCpu()));
    }

    public static ComparisionResult<CalibreBook> compare(List<CalibreBook> oldBooks, List<CalibreBook> newBooks) {

        Map<Long, CalibreBook> oldIds = oldBooks.stream().collect(Collectors.toMap(CalibreBook::getId, Function.identity()));
        Map<Long, CalibreBook> newIds = newBooks.stream().collect(Collectors.toMap(CalibreBook::getId, Function.identity()));

        Collection<CalibreBook> deletedBooks = mapDifference(newIds, oldIds);
        Collection<CalibreBook> addedBooks = mapDifference(oldIds, newIds);

        List<CalibreBook> allBooks = new ArrayList<>(oldBooks);
        allBooks.addAll(newBooks);
        List<Pair<CalibreBook, CalibreBook>> changed = allBooks.stream().collect(groupingBy(Book::getId))
                .values().stream().filter(calibreBooks -> calibreBooks.size() == 2)
                .filter(calibreBooks -> !calibreBooks.get(0).equals(calibreBooks.get(1)))
                .map(calibreBooks -> new Pair<>(calibreBooks.get(0), calibreBooks.get(1))).collect(toList());

        //Map<CalibreBook, List<Pair<String, String>>> changedBooks = new HashMap<>();

        System.out.println("===");
        Map<CalibreBook, List<Pair<String, Pair<String, String>>>> changedBooks = changed.stream().collect(Collectors.toMap(Pair::getKey, pair -> {
            List<Pair<String, Pair<String, String>>> result = new ArrayList<>();
            JsonObject oldJsonObject = JsonUtils.gson.toJsonTree(pair.getKey()).getAsJsonObject();
            JsonObject newJsonObject = JsonUtils.gson.toJsonTree(pair.getValue()).getAsJsonObject();
            oldJsonObject.entrySet().forEach(e -> {
                if (!e.getValue().toString().equals(newJsonObject.get(e.getKey()).toString())) {
                    System.out.print(e.getKey() + ": ");
                    System.out.print(e.getValue().toString() + " -> ");
                    System.out.println(newJsonObject.get(e.getKey()).toString());
                    Pair<String, String> value = new Pair<>(e.getValue().toString(), newJsonObject.get(e.getKey()).toString());
                    result.add(new Pair<>(e.getKey(), value));
                }
            });
            return result;
        }));

        return new ComparisionResult<>("", addedBooks, deletedBooks, changedBooks);
    }

    static <K, V> Collection<V> mapDifference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
        Map<K, V> difference = new HashMap<>(right);
        left.forEach((key, value) -> difference.remove(key));
        return difference.values();
    }

    private <T> List<T> selectAllFrom(String tableName, Class<T> clazz) {
        return readObjectList(String.format("SELECT * FROM %s", tableName), clazz);
    }

    public <T> T readObject(String sql, Class<T> clazz) {
        List<T> results = readObjectList(sql, clazz);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public <T> List<T> readObjectList(String sql, Class<T> clazz) {
        List<T> results = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
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
                    map.put(metaData.getColumnName(i), value);
                }
                String json = JsonUtils.gson.toJson(map);
                results.add(JsonUtils.gson.fromJson(json, TypeToken.get(clazz).getType()));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return results;
    }

    void executeQuery(String sql) {
        System.out.println(sql);
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Integer executeInsertQuery(String sql) {
        System.out.println(sql);
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            if (affectedRows == 0) {
                throw new SQLException("No rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public int executeUpdateQuery(String sql) {
        System.out.println(sql);
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /*public String getInsertQuery(String tableName, Object object) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new BookUtils.LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new BookUtils.LocalDateTimeAdapter())
                .create();
        String json = gson.toJson(object);
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> myMap = gson.fromJson(json, type);
        System.out.println(myMap);

        String campos = myMap.keySet().stream().collect(Collectors.joining(","));
        String valores = myMap.values().stream().map(v -> {
            if (v instanceof String) {
                return "'" + v + "'";
            } else if (v instanceof LocalDateTime) {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                return "'" + formatter.format((LocalDateTime) v) + "'";
            } else {
                return v.toString();
            }
            //TODO arrays - ignore???
        }).collect(Collectors.joining(","));

        return "INSERT INTO `" + tableName + "` (" + campos + ") values (" + valores + ")";
    }*/

    public String getInsertQuery(String tableName, Object object, Class<?> clazz) {
        //TODO - common gson
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        JsonObject jsonObject = gson.toJsonTree(object, clazz).getAsJsonObject();

        String keys = String.join(",", jsonObject.keySet());
        List<String> valuesList = jsonObject.entrySet().stream().map(v -> {
            if (v.getValue().getAsJsonPrimitive().isString()) {
                return "'" + escape(v.getValue().getAsJsonPrimitive().getAsString()) + "'";
            } else {
                return v.getValue().getAsJsonPrimitive().getAsString();
            }
        }).collect(toList());
        String values = String.join(",", valuesList);

        return "INSERT INTO `" + tableName + "` (" + keys + ") values (" + values + ")";
    }

    private static String escape(String value) {
        return value.replace("'", "''");
    }

    public String getUpdateQuery(String tableName, Object o1, Object o2) {
        Map<String, Pair<JsonPrimitive, JsonPrimitive>> diff = getDiff(o1, o2);
        String expression = diff.entrySet().stream().map(v -> {
            String k = v.getKey() + "=";
            if (v.getValue().getValue().isString()) {
                k += "'" + escape(v.getValue().getValue().getAsString()) + "'";
            } else {
                k += v.getValue().getValue().getAsString();
            }
            return k;
        }).collect(Collectors.joining(","));

        /*String key = diff.entrySet().iterator().next().getKey();
        String where = key + "=";
        JsonPrimitive value = diff.get(key).getKey();
        if (value.isString()) {
            where += "'" + value.getAsString() + "'";
        } else  {
            where += value.getAsString();
        }*/

        return "UPDATE `" + tableName + "` SET " + expression + " WHERE " /*+ where*/;
    }

    void upsertTiviId(long bookId, int tiviId) {
        CustomColumn cb = readObject("SELECT * FROM `custom_column_17` WHERE book=" + bookId, CustomColumn.class);
        if (cb == null) {
            String q = String.format("INSERT INTO `custom_column_17` VALUES (null, %d, %d)", bookId, tiviId);
            Integer newId = executeInsertQuery(q);
            System.out.println(newId);
        } else {
            String q = String.format("UPDATE `custom_column_17` SET value=%d WHERE book=%d", tiviId, bookId);
            Integer newId = executeUpdateQuery(q);
            System.out.println(newId);
        }
    }

    static Map<String, Pair<JsonPrimitive, JsonPrimitive>> getDiff(Object o1, Object o2) {
        Map<String, Pair<JsonPrimitive, JsonPrimitive>> diff = new LinkedHashMap<>();
        JsonObject jo1 = JsonUtils.gson.toJsonTree(o1).getAsJsonObject();
        JsonObject jo2 = JsonUtils.gson.toJsonTree(o2).getAsJsonObject();
        jo1.entrySet().forEach(e -> {
            JsonPrimitive v1 = e.getValue().getAsJsonPrimitive();
            JsonPrimitive v2 = jo2.get(e.getKey()).getAsJsonPrimitive();
            if (!v1.equals(v2)) {
                diff.put(e.getKey(), new Pair<>(v1, v2));
                System.out.println(e.getKey() + ": " + new Pair<>(v1, v2).toString());
            }
        });
        return diff;
    }

    static boolean isEquals(Object o1, Object o2) {
        return getDiff(o1, o2).isEmpty();
    }

    private Connection connect() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(getJdbcString(calibreDbFullPath));
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
        return conn;
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

    @SneakyThrows
    public static void dumpDB() {
        String source = Config.calibreDbPath + Config.calibreDbName;
        String destination = source.replace(".db", "-" + LocalDateTime.now().toString().replace(":", "-") + ".db");
        Files.copy(Paths.get(source), Paths.get(destination));
        System.out.println("Dumped to: " + destination);
    }

    @SneakyThrows
    public static String getOldestDbDumpPath() {
        // metadata-2022-02-11T15-03-14.778.db
        return Files.walk(Paths.get(Config.calibreDbPath), 1).map(p -> p.toAbsolutePath().toString())
                .filter(p -> p.matches(".*metadata-.*\\.db")).sorted().findFirst().orElseThrow(() -> new RuntimeException("No metadata dumps :("));
    }

    public static String getDateFromFile(String path) {
        // metadata-2022-02-11T15-03-14.778.db
        String[] chunks = Paths.get(path).getFileName().toString().split("-");
        return String.format("%s.%s.%s", chunks[3].split("T")[0], chunks[2], chunks[1]);
    }

    public static String getFullText(String textShort, String textFull) {
        String text = "";
        if (textShort != null) {
            text = textShort;
        }
        if (textFull != null && !textFull.isEmpty()) {
            text = text + "<hr>" + textFull;
        }
        //text = "<div>" + text + "</div>";
        return text;
    }

    public static boolean hasDirtyHtml(CalibreBook calibreBook) {
        String initialHtmlText = unify(calibreBook.getComment());
        String cleanedHtmlText = sanitize(getFullText(calibreBook.getTextShort(), calibreBook.getTextMore()));
        if (!initialHtmlText.equals(cleanedHtmlText)) {
            return true;
        }
        initialHtmlText = unify(calibreBook.getReleaseNote());
        cleanedHtmlText = sanitize(calibreBook.getReleaseNote());
        return !initialHtmlText.equals(cleanedHtmlText);
    }

    public static String unify(String htmlText) {
        if (htmlText == null) {
            return "<div></div>";
        }
        Document doc = Jsoup.parseBodyFragment(htmlText);
        doc.outputSettings().prettyPrint(false);
        doc.outputSettings().outline(true);
        doc.outputSettings().indentAmount(4);
        Element element = doc.body();
        return element.html();
    }

    public static String sanitize(String htmlText) {
        if (htmlText == null) {
            return "<div></div>";
        }
        Document doc = Jsoup.parseBodyFragment(htmlText);
        doc.outputSettings().prettyPrint(false);
        doc.outputSettings().outline(true);
        doc.outputSettings().indentAmount(4);
        Node element = doc.body();
        element = sanitizeElement(element, 0);
        if (element.childNodeSize() == 1) {
            if ((element.childNode(0) instanceof TextNode)) {
                Element child = new Element("p");
                child.appendChild(element.childNode(0));
                Element root = new Element("div");
                root.appendChild(child);
                return root.outerHtml();
            } else {
                ((Element) element.childNode(0)).tagName("div");
                return ((Element) element).html()
                        .replaceAll("\\s*<p>\\s*<br>\\s*</p>\\s*", "").replaceAll("\\s*<p>\\s*</p>\\s*", "")
                        .replace(" <p>", "<p>").replace(" </p>", "</p>")
                        .replace("<p> ", "<p>").replace("</p> ", "</p>")
                        .replace(" <ul>", "<ul>").replace(" </ul>", "</ul>")
                        .replace("<ul> ", "<ul>").replace("</ul> ", "</ul>")
                        .replace(" <li>", "<li>").replace(" </li>", "</li>")
                        .replace("<li> ", "<li>").replace("</li> ", "</li>");
            }
        } else { // many children
            ((Element) element).tagName("div");
            return (element).outerHtml().replaceAll("\\s*<p>\\s*<br>\\s*</p>\\s*", "").replaceAll("\\s*<p>\\s*</p>\\s*", "")
                    .replace(" <p>", "<p>").replace(" </p>", "</p>")
                    .replace("<p> ", "<p>").replace("</p> ", "</p>")
                    .replace(" <ul>", "<ul>").replace(" </ul>", "</ul>")
                    .replace("<ul> ", "<ul>").replace("</ul> ", "</ul>")
                    .replace(" <li>", "<li>").replace(" </li>", "</li>")
                    .replace("<li> ", "<li>").replace("</li> ", "</li>");
        }
    }

    private static Node sanitizeElement(Node node, int index) {
        if (node instanceof Element) {
            Element element = (Element) node;
            /*if (element.tagName().equals("p")) {
                element.tagName("div");
                node = element;
            }*/
            if (element.tagName().equals("div")) {
                element.tagName("p");
                node = element;
            }
            if (element.tagName().equals("center")) {
                element.tagName("p");
                node = element;
            }
            if (element.tagName().equals("font")) {
                element.tagName("span");
                node = element;
            }
        }
        //TODO remove \n from text nodes
        if (node.nodeName().equals("#text")) {
            assert node instanceof TextNode;
            TextNode textNode = (TextNode) node;
            textNode.text(textNode.text().replace("\n", " ").replace("  ", " "));
            node = textNode;
        }
        List<Attribute> attrs = new ArrayList<>();
        node.attributes().forEach(attr -> {
            if (!attr.getKey().equals("#text") && !attr.getKey().equals("href") && !attr.getKey().equals("rel") && !attr.getKey().equals("alt") && !attr.getKey().equals("title") && !attr.getKey().equals("src")
                    && !(attr.getKey().equals("class") && attr.getValue().equals("spoiler"))) {
                attrs.add(attr);
            }
        });
        for (Attribute a : attrs) {
            node.removeAttr(a.getKey());
        }
        if (node.childNodeSize() > 0) {
            for (int i = 0; i < node.childNodes().size(); i++) {
                sanitizeElement(node.childNode(i), i);
            }
        }
        if (node.nodeName().equals("span")) {
            /*if (node.childNodeSize() == 1 && node.childNode(0) instanceof TextNode) {
                Element element = (Element) node;
                node.parentNode().childNode(index).replaceWith(new TextNode(element.text()));
            }*/
            if (node.childNodeSize() == 1) {
                assert node.parentNode() != null;
                node.parentNode().childNode(index).replaceWith(node.childNode(0));
            }
        }
        return node;
    }

    public void dumpImages() {
        List<CalibreBook> calibreBooks = readBooks();

        File coversDir = new File(Config.outputPath + "cover");
        File thumbsDir = new File(Config.outputPath + "thumb");

        FileUtils.deleteFileOrFolder(coversDir.toPath());
        FileUtils.deleteFileOrFolder(thumbsDir.toPath());

        calibreBooks/*.stream().filter(b -> b.getOwn() != null && b.getOwn())*/.forEach(b -> {
            try {
                File coversSubDir = new File(coversDir, BookUtils.getCategoryByTags(b));
                Path srcCover = Paths.get(Config.calibreDbPath).resolve(b.getPath()).resolve("cover.jpg");
                if (Files.exists(srcCover)) {
                    FileUtils.mkdirs(coversSubDir);
                    Path destCover = coversSubDir.toPath().resolve(b.getCpu() + ".jpg");
                    Files.copy(srcCover, destCover, REPLACE_EXISTING);

                    File thumbsSubDir = new File(thumbsDir, BookUtils.getCategoryByTags(b));
                    FileUtils.mkdirs(thumbsSubDir);
                    Path destThumb = thumbsSubDir.toPath().resolve(b.getCpu() + ".jpg");
                    ImageUtils.saveThumbnail(destCover.toFile(), destThumb.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void dumpBooks() throws IOException {
        List<CalibreBook> calibreBooks = readBooks();

        File booksDir = new File(Config.outputPath + typeTranslationMap.get(BOOK).getPlural());
        File magazinesDir = new File(Config.outputPath + typeTranslationMap.get(MAGAZINE).getPlural());
        File manualsDir = new File(Config.outputPath + typeTranslationMap.get(MANUAL).getPlural());
        File comicsDir = new File(Config.outputPath + typeTranslationMap.get(COMICS).getPlural());
        File guidesDir = new File(Config.outputPath + typeTranslationMap.get(GUIDE).getPlural());
        File docsDir = new File(Config.outputPath + typeTranslationMap.get(DOC).getPlural());
        File emulatorsDir = new File(Config.outputPath + typeTranslationMap.get(EMULATOR).getPlural());

        FileUtils.deleteFileOrFolder(booksDir.toPath());
        FileUtils.deleteFileOrFolder(magazinesDir.toPath());
        FileUtils.deleteFileOrFolder(manualsDir.toPath());
        FileUtils.deleteFileOrFolder(comicsDir.toPath());

        List<CalibreBook> shallowCopy = calibreBooks.stream().filter(b -> b.getOwn() != null && b.getOwn()).collect(toList());
        Collections.reverse(shallowCopy);

        int i = 1;
        for (CalibreBook book : shallowCopy) {
            String category = BookUtils.getCategoryByTags(book);
            if (book.getGroup() != null) {
                category = category + "/" + book.getGroup().replace(":", ".").replace("/", ".");
            }
            Path destPath;
            switch (book.getType()) {
                case MAGAZINE:
                    //TODO may be languages in path
                    List<CalibreBook> books = shallowCopy.stream().filter(b -> b.getSeries() != null && b.getSeries().equals(book.getSeries())).collect(toList());
                    if (books.isEmpty()) {
                        throw new RuntimeException("Magazine w/o serie: " + book.getTitle());
                    }
                    String tag = MagazinesCitationRenderer.getSpecificTag(books);
                    if (MagazinesCitationRenderer.isSpecific(books)) { // specialized magz
                        destPath = booksDir.toPath().resolve(tag);
                    } else {
                        destPath = magazinesDir.toPath();
                    }
                    String group = getMagazineTitle(book).replace(":", ". ").replace("/", ". ");
                    destPath = destPath.resolve(group.replace(":", ". ").replace("/", ". "));
                    break;
                case MANUAL:
                    //TODO may be languages in path
                    destPath = manualsDir.toPath().resolve(category);
                    break;
                case COMICS:
                    //TODO may be languages in path
                    destPath = comicsDir.toPath().resolve(category);
                    break;
                case GUIDE:
                    //TODO may be languages in path
                    destPath = guidesDir.toPath().resolve(category);
                    break;
                case DOC:
                    //TODO may be languages in path
                    destPath = docsDir.toPath().resolve(category);
                    break;
                case EMULATOR:
                    //TODO may be languages in path
                    destPath = emulatorsDir.toPath().resolve(category);
                    break;
                default:
                    destPath = booksDir.toPath().resolve(category);
                    break;
            }
            FileUtils.mkdirs(destPath);
            final String fileName = book.getFileName() == null ? book.getTitle() : book.getFileName();
            //сначала надо копировать, а архивы обрабатывать в конце
            for (Data data : book.getDataList()) {
                Path srcBook = Paths.get(Config.calibreDbPath).resolve(book.getPath()).resolve(data.getName() + "." + data.getFormat().toLowerCase());
                // может так случиться, что файл был удалён, но в базе он есть.
                if (!Files.exists(srcBook)) {
                    System.out.println("The file is absent: " + srcBook);
                    continue;
                }
                switch (data.getFormat().toLowerCase()) {
                    case "zip":
                        if (!needToExtract(SevenZipUtils.getZipFileList(srcBook.toFile()))) {
                            copyFile(srcBook, destPath, fileName, data.getFormat(), i, shallowCopy.size());
                        }
                        break;
                    case "7z":
                        if (!needToExtract(SevenZipUtils.get7zFileList(srcBook.toFile()))) {
                            copyFile(srcBook, destPath, fileName, data.getFormat(), i, shallowCopy.size());
                        }
                        break;
                    case "rar":
                        if (!needToExtract(RarUtils.getRarFileList(srcBook.toFile()))) {
                            copyFile(srcBook, destPath, fileName, data.getFormat(), i, shallowCopy.size());
                        }
                        break;
                    case "pdf":
                    case "djvu":
                    case "cbr":
                    case "cbz":
                    case "epub":
                    case "fb2":
                    case "doc":
                    case "docx":
                    case "xls":
                    case "xlsx":
                    case "rtf":
                    case "jpg":
                    case "png":
                    case "gif":
                    case "scl":
                    case "trd":
                    case "tap":
                    case "chm":
                    case "txt":
                    case "exe":
                        copyFile(srcBook, destPath, fileName, data.getFormat(), i, shallowCopy.size());
                        break;
                    default:
                        throw new RuntimeException(data.toString());
                }
            }
            for (Data data : book.getDataList()) {
                Path srcBook = Paths.get(Config.calibreDbPath).resolve(book.getPath()).resolve(data.getName() + "." + data.getFormat().toLowerCase());
                // может так случиться, что файл был удалён, но в базе он есть.
                if (!Files.exists(srcBook)) {
                    continue;
                }
                switch (data.getFormat().toLowerCase()) {
                    case "zip":
                        if (needToExtract(SevenZipUtils.getZipFileList(srcBook.toFile()))) {
                            SevenZipUtils.extractZip(srcBook, destPath, fileName);
                        }
                        break;
                    case "7z":
                        if (needToExtract(SevenZipUtils.get7zFileList(srcBook.toFile()))) {
                            SevenZipUtils.extract7z(srcBook, destPath, fileName);
                        }
                        break;
                    case "rar":
                        if (needToExtract(RarUtils.getRarFileList(srcBook.toFile()))) {
                            RarUtils.extractArchive(srcBook, destPath, fileName);
                        }
                        break;
                }
            }
            i++;
        }

        // delete empty dirs
        Files.walk(Paths.get(Config.outputPath))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .filter(File::isDirectory)
                .forEach(File::delete);
    }

    private static void copyFile(Path srcPath, Path destPath, String fileName, String ext, int index, int count) {
        System.out.println(String.format("%d of %d: Copy: %s", index, count, srcPath));
        Path destBook = FileUtils.findFreeFileName(destPath, fileName, ext.toLowerCase(), 0);
        try {
            FileUtils.mkdirs(destBook.getParent());
            Files.copy(srcPath, destBook, REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Set<String> IMAGES = new HashSet<>(Arrays.asList("jpeg", "jpg", "png", "tif", "tiff", "gif", "exe", "py", "html", "gs0", "diz"));

    private static boolean needToExtract(List<ArchiveEntry> fileNames) {
        Set<String> exts = fileNames.stream().map(ArchiveEntry::getName).map(FileUtils::getExtension).map(String::toLowerCase).collect(toSet());
        String joined = fileNames.stream().map(ArchiveEntry::getName).collect(joining());
        return Collections.disjoint(exts, IMAGES) && !joined.contains("\\") && !joined.contains("/");
    }

    public static String fixSomeChars(String text) {
        text = text.replace('´', '\'');
        text = text.replace('·', '•');
        text = text.replace('·', '•');
        text = text.replace('˚', '°');
        text = text.replace('–', '-');
        text = text.replace('—', '-');
        text = text.replace((char) 0x0A + "", "");
        return text;
    }

    static class LocalDateAdapter implements JsonSerializer<LocalDate> {
        public JsonElement serialize(LocalDate date, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
        }
    }

    static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {
        public JsonElement serialize(LocalDateTime date, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }
}

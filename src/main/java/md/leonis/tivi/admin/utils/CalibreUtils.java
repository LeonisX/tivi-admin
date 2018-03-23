package md.leonis.tivi.admin.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import md.leonis.tivi.admin.model.ArchiveEntry;
import md.leonis.tivi.admin.model.BookRecord;
import md.leonis.tivi.admin.model.ComparisionResult;
import md.leonis.tivi.admin.model.media.*;
import md.leonis.tivi.admin.model.media.Comment;
import md.leonis.tivi.admin.model.media.links.*;
import md.leonis.tivi.admin.utils.archive.RarUtils;
import md.leonis.tivi.admin.utils.archive.SevenZipUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.FileVisitResult.TERMINATE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.stream.Collectors.*;

public class CalibreUtils {

    public static final String FILES_LIST = "files-list.txt";

    //public static List<BookRecord> bookRecords;
    //public static Map<Long, BookRecord> bookRecordMap;

    /*PRAGMA index_list(books);
    PRAGMA index_xinfo(books_idx);
    PRAGMA stats;
    PRAGMA table_info(books);
    SELECT sql FROM sqlite_master WHERE name='books'*/

    public static String getCreateTableQuery(String tableName) {
        return String.format("SELECT sql FROM sqlite_master WHERE name='%s'", tableName);
    }

    public static List<CalibreBook> readBooks() {
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
                calibreBook.setTextShort(chunks[0].trim());
                if (chunks.length > 1) {
                    calibreBook.setTextMore(chunks[1].trim());
                } else {
                    calibreBook.setTextMore("");
                }
            }
        });

        List<AuthorLink> bookAuthors = selectAllFrom("books_authors_link", AuthorLink.class);
        List<Author> authors = selectAllFrom("authors", Author.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = bookAuthors.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(AuthorLink::getAuthor).collect(toList());
            calibreBook.setAuthors(authors.stream().filter(author -> ids.contains(author.getId())).collect(toList()));
        });

        Integer[] c = {1, 2, 4, 6, 7, 10, 11, 12, 13, 14, 15, 16, 19, 20};

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
            calibreBook.setType(types.stream().filter(i -> ids14.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

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

            List<Long> ids7 = links.get(7).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setScannedBy(scannedBys.stream().filter(i -> ids7.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));

            calibreBook.setPages(pages.stream().filter(a -> a.getBook().equals(calibreBook.getId())).findFirst().map(a -> a.getLongValue().intValue()).orElse(null));
            calibreBook.setOwn(owns.stream().filter(a -> a.getBook().equals(calibreBook.getId())).findFirst().map(Own::getValue).orElse(null));

            calibreBook.setTiviId(tiviIds.stream().filter(a -> a.getBook().equals(calibreBook.getId())).findFirst().map(Link::getLongValue).orElse(null));

            List<Long> ids16 = links.get(16).stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(Link::getLongValue).collect(toList());
            calibreBook.setCpu(cpus.stream().filter(i -> ids16.contains(i.getId())).findFirst().map(CustomColumn::getValue).orElse(null));
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

        calibreBooks.forEach(System.out::println);
        return calibreBooks;
    }

    public static ComparisionResult<CalibreBook> compare(String oldBasePath, String newBasePath) {
        String configUrl = Config.sqliteUrl;
        Config.sqliteUrl = oldBasePath;
        List<CalibreBook> oldBooks = readBooks();
        Config.sqliteUrl = newBasePath;
        List<CalibreBook> newBooks = readBooks();
        Config.sqliteUrl = configUrl;

        Map<Long, CalibreBook> oldIds = oldBooks.stream().collect(Collectors.toMap(CalibreBook::getId, Function.identity()));
        Map<Long, CalibreBook> newIds = newBooks.stream().collect(Collectors.toMap(CalibreBook::getId, Function.identity()));

        Collection<CalibreBook> addedBooks = mapDifference(newIds, oldIds);
        Collection<CalibreBook> deletedBooks = mapDifference(oldIds, newIds);

        List<CalibreBook> allBooks = new ArrayList<>(oldBooks);
        allBooks.addAll(newBooks);
        List<Pair<CalibreBook, CalibreBook>> changed = allBooks.stream().collect(groupingBy(Book::getId))
                .entrySet().stream().filter(e -> e.getValue().size() == 2)
                .filter(e -> !e.getValue().get(0).equals(e.getValue().get(1)))
                .map(e -> new Pair<>(e.getValue().get(0), e.getValue().get(1))).collect(toList());

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

        return new ComparisionResult<>(addedBooks, deletedBooks, changedBooks);
    }

    public static <K, V> Collection<V> mapDifference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right) {
        Map<K, V> difference = new HashMap<>();
        difference.putAll(left);
        difference.putAll(right);
        difference.entrySet().removeAll(right.entrySet());
        return difference.values();
    }

    public static <T> List<T> selectAllFrom(String tableName, Class<T> clazz) {
        return readObjectList(String.format("SELECT * FROM %s", tableName), clazz);
    }

    public static <T> List<T> readObjectList(String sql, Class<T> clazz) {
        List<T> results = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    Object value;
                    switch (metaData.getColumnType(i)) {
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

    public static <T> T readObject(String sql, Class<T> clazz) {
        List<T> results = new ArrayList<>();
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    Object value;
                    switch (metaData.getColumnType(i)) {
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
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public static void executeQuery(String sql) {
        System.out.println(sql);
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Integer executeInsertQuery(String sql) {
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

    public static int executeUpdateQuery(String sql) {
        System.out.println(sql);
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String getInsertQuery(String tableName, Object object) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new BookUtils.LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new BookUtils.LocalDateTimeAdapter())
                .create();
        String json = gson.toJson(object);
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
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
    }

    public static String getInsertQuery(String tableName, Object object, Class<?> clazz) {
        //TODO - common gson
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new BookUtils.LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new BookUtils.LocalDateTimeAdapter())
                .create();
        JsonObject jsonObject = gson.toJsonTree(object, clazz).getAsJsonObject();

        String keys = jsonObject.keySet().stream().collect(Collectors.joining(","));
        List<String> valuesList = jsonObject.entrySet().stream().map(v -> {
            if (v.getValue().getAsJsonPrimitive().isString()) {
                return "'" + escape(v.getValue().getAsJsonPrimitive().getAsString()) + "'";
            } else  {
                return v.getValue().getAsJsonPrimitive().getAsString();
            }
        }).collect(toList());
        String values = valuesList.stream().collect(Collectors.joining(","));

        return "INSERT INTO `" + tableName + "` (" + keys + ") values (" + values + ")";
    }

    public static String escape(String value) {
        return value.replace("'", "''");
    }

    public static String getUpdateQuery(String tableName, Object o1, Object o2) {
        Map<String, Pair<JsonPrimitive, JsonPrimitive>> diff = getDiff(o1, o2);
        String expression = diff.entrySet().stream().map(v -> {
            String k = v.getKey() + "=";
            if (v.getValue().getValue().isString()) {
                k += "'" + escape(v.getValue().getValue().getAsString()) + "'";
            } else  {
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

    public static Map<String, Pair<JsonPrimitive, JsonPrimitive>> getDiff(Object o1, Object o2) {
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

    public static boolean isEquals(Object o1, Object o2) {
        return getDiff(o1, o2).isEmpty();
    }

    private static Connection connect() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(Config.sqliteUrl);
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

    public static void dumpDB() {
        String source = Config.calibreDbPath + Config.calibreDbName;
        String destination = source.replace(".db", "-" + LocalDateTime.now().toString().replace(":", "-") + ".db");
        try {
            Files.copy(Paths.get(source), Paths.get(destination));
            System.out.println("Dumped to: " + destination);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                node.parentNode().childNode(index).replaceWith(node.childNode(0));
            }
        }
        return node;
    }

    public static void dumpImages() throws IOException {
/*
        Config.loadProperties();
        Config.loadProtectedProperties();
*/
        BookUtils.calibreBooks = CalibreUtils.readBooks();

        File coversDir = new File(Config.workPath + "cover");
        File thumbsDir = new File(Config.workPath + "thumb");

        deleteFileOrFolder(coversDir.toPath());
        deleteFileOrFolder(thumbsDir.toPath());

        BookUtils.calibreBooks/*.stream().filter(b -> b.getOwn() != null && b.getOwn())*/.forEach(b -> {
            try {
                File coversSubDir = new File(coversDir, BookUtils.getCategoryByTags(b));
                Path srcCover = Paths.get(Config.calibreDbPath).resolve(b.getPath()).resolve("cover.jpg");
                if (Files.exists(srcCover)) {
                    coversSubDir.mkdirs();
                    Path destCover = coversSubDir.toPath().resolve(b.getCpu() + ".jpg");
                    Files.copy(srcCover, destCover, REPLACE_EXISTING);

                    File thumbsSubDir = new File(thumbsDir, BookUtils.getCategoryByTags(b));
                    thumbsSubDir.mkdirs();
                    Path destThumb = thumbsSubDir.toPath().resolve(b.getCpu() + ".jpg");
                    ImageUtils.saveThumbnail(destCover.toFile(), destThumb.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void dumpBooks(boolean onlyForSite) throws IOException {
        //TODO this for lists for site (stub)
        Map<Long, BookRecord> bookRecordMap = new HashMap<>();
        /*readBookRecords(onlyForSite);
        if (CalibreUtils.bookRecords.stream().anyMatch(b -> b.getChecked() == null)) {
            throw new RuntimeException(CalibreUtils.bookRecords.stream().filter(b -> b.getChecked() == null).map(BookRecord::getName).collect(joining("\n")));
        }*/
        //Config.loadProperties();
        //Config.loadProtectedProperties();

        BookUtils.calibreBooks = CalibreUtils.readBooks();

        File booksDir = new File(Config.workPath + "books");
        File magazinesDir = new File(Config.workPath + "magazines");
        File manualsDir = new File(Config.workPath + "manuals");
        File comicsDir = new File(Config.workPath + "comics");
        File guidesDir = new File(Config.workPath + "guides");
        File docsDir = new File(Config.workPath + "docs");
        File emulatorsDir = new File(Config.workPath + "emulators");

        deleteFileOrFolder(booksDir.toPath());
        deleteFileOrFolder(magazinesDir.toPath());
        deleteFileOrFolder(manualsDir.toPath());
        deleteFileOrFolder(comicsDir.toPath());

        List<CalibreBook> shallowCopy = BookUtils.calibreBooks.stream().filter(b -> b.getOwn() != null && b.getOwn()).collect(toList());
        //Collections.reverse(shallowCopy);
        int i = 1;
        for (CalibreBook b : shallowCopy) {
            String system;
            if (b.getTags().size() > 1) {
                system = "consoles"; //TODO computers
            } else {
                system = b.getTags().get(0).getName();
            }
            Path destPath;
            switch (b.getType()) {
                case "magazine":
                    //TODO may be languages in path
                    destPath = magazinesDir.toPath().resolve(b.getSeries().getName());
                    destPath.toFile().mkdirs();
                    break;
                case "manual":
                    //TODO may be languages in path
                    destPath = manualsDir.toPath().resolve(system);
                    destPath.toFile().mkdirs();
                    break;
                case "comics":
                    //TODO may be languages in path
                    destPath = comicsDir.toPath().resolve(system);
                    destPath.toFile().mkdirs();
                    break;
                case "guide":
                    //TODO may be languages in path
                    destPath = guidesDir.toPath().resolve(system);
                    destPath.toFile().mkdirs();
                    break;
                case "doc":
                    //TODO may be languages in path
                    destPath = docsDir.toPath().resolve(system);
                    destPath.toFile().mkdirs();
                    break;
                case "emulator":
                    //TODO may be languages in path
                    destPath = emulatorsDir.toPath().resolve(system);
                    destPath.toFile().mkdirs();
                    break;
                default:
                    destPath = booksDir.toPath().resolve(system);
                    destPath.toFile().mkdirs();
                    break;
            }
            final String fileName = b.getFileName() == null ? b.getTitle() : b.getFileName();
            for (Data data : b.getDataList()) {
                Path srcBook = Paths.get(Config.calibreDbPath).resolve(b.getPath()).resolve(data.getName() + "." + data.getFormat().toLowerCase());
                switch (data.getFormat().toLowerCase()) {
                    case "zip":
                        if (uncompress(SevenZipUtils.getZipFileList(srcBook.toFile()))) {
                            SevenZipUtils.extractZip(srcBook, destPath, fileName, bookRecordMap);
                        } else {
                            copyFile(srcBook, destPath, fileName, data.getFormat(), onlyForSite, i, shallowCopy.size());
                        }
                        break;
                    case "7z":
                        if (uncompress(SevenZipUtils.get7zFileList(srcBook.toFile()))) {
                            SevenZipUtils.extract7z(srcBook, destPath, fileName, bookRecordMap);
                        } else {
                            copyFile(srcBook, destPath, fileName, data.getFormat(), onlyForSite, i, shallowCopy.size());
                        }
                        break;
                    case "rar":
                        if (uncompress(RarUtils.getRarFileList(srcBook.toFile()))) {
                            RarUtils.extractArchive(srcBook, destPath, fileName, bookRecordMap);
                        } else {
                            copyFile(srcBook, destPath, fileName, data.getFormat(), onlyForSite, i, shallowCopy.size());
                        }
                        break;
                    case "pdf":
                    case "djvu":
                    case "cbr":
                    case "doc":
                    case "docx":
                    case "rtf":
                    case "jpg":
                    case "scl":
                    case "trd":
                    case "chm":
                    case "txt":
                        copyFile(srcBook, destPath, fileName, data.getFormat(), onlyForSite, i, shallowCopy.size());
                        break;
                    default:
                        throw new RuntimeException(data.toString());
                }
            }
            i++;
        }
    }

    /*public static void generateBooksList() throws IOException {
        readBookRecords(false);
        BookUtils.calibreBooks = CalibreUtils.readBooks();

        List<CalibreBook> shallowCopy = BookUtils.calibreBooks.stream().filter(b -> b.getOwn() != null && b.getOwn())
                .sorted(Comparator.comparing(calibreBook -> calibreBook.getType() + calibreBook.getTitle())).collect(toList());
        Collections.reverse(shallowCopy);
        int i = 1;
        List<BookRecord> bookRecords = new ArrayList<>();
        for (CalibreBook b : shallowCopy) {
            final String fileName = b.getFileName() == null ? b.getTitle() : b.getFileName();
            for (Data data : b.getDataList()) {
                final String fileName2 = fileName+ "." + data.getFormat().toLowerCase();
                Path srcBook = Paths.get(Config.calibreDbPath).resolve(b.getPath()).resolve(data.getName() + "." + data.getFormat().toLowerCase());
                switch (data.getFormat().toLowerCase()) {
                    case "zip":
                        List<ArchiveEntry> entries = SevenZipUtils.getZipFileList(srcBook.toFile());
                        if (uncompress(entries)) {
                            entries.forEach(entry -> bookRecords.add(new BookRecord(null, entry.getName(), entry.getCrc32(), entry.getSize(), b.getTitle())));
                        } else {
                            bookRecords.add(new BookRecord(null, fileName + "." + data.getFormat().toLowerCase(), SevenZipUtils.crc32(srcBook), data.getUncompressedSize(), b.getTitle()));
                        }
                        break;
                    case "7z":
                        entries = SevenZipUtils.get7zFileList(srcBook.toFile());
                        if (uncompress(entries)) {
                            entries.forEach(entry -> bookRecords.add(new BookRecord(null, entry.getName(), entry.getCrc32(), entry.getSize(), b.getTitle())));
                        } else {
                            bookRecords.add(new BookRecord(null, fileName + "." + data.getFormat().toLowerCase(), SevenZipUtils.crc32(srcBook), data.getUncompressedSize(), b.getTitle()));
                        }
                        break;
                    case "rar":
                        entries = RarUtils.getRarFileList(srcBook.toFile());
                        if (uncompress(entries)) {
                            entries.forEach(entry -> bookRecords.add(new BookRecord(null, entry.getName(), entry.getCrc32(), entry.getSize(), b.getTitle())));
                        } else {
                            bookRecords.add(new BookRecord(null, fileName + "." + data.getFormat().toLowerCase(), SevenZipUtils.crc32(srcBook), data.getUncompressedSize(), b.getTitle()));
                        }
                        break;
                    case "pdf":
                    case "djvu":
                    case "cbr":
                    case "doc":
                    case "docx":
                    case "rtf":
                    case "jpg":
                    case "scl":
                    case "trd":
                    case "chm":
                    case "txt":
                        System.out.println(String.format("%d of %d: List: %s", i, shallowCopy.size(), srcBook));
                        bookRecords.add(new BookRecord(null, fileName + "." + data.getFormat().toLowerCase(), SevenZipUtils.crc32(srcBook), data.getUncompressedSize(), b.getTitle()));
                        break;
                    default:
                        throw new RuntimeException(data.toString());
                }
            }
            i++;
        }
        File file = new File(Config.calibreDbPath + FILES_LIST);
        if (file.exists()) {
            file.renameTo(new File(Config.calibreDbPath + FILES_LIST + ".bak"));
        }
        if (CalibreUtils.bookRecords.isEmpty()) {
            bookRecords.stream().collect(groupingBy(BookRecord::getBookName)).values().stream().filter(v -> v.size() == 1).forEach(b -> b.get(0).setChecked(true));
        } else {
            bookRecords.forEach(b -> {
                BookRecord existing = CalibreUtils.bookRecordMap.get(b.getCrc32());
                if (existing != null && existing.getChecked() != null) {
                    b.setChecked(existing.getChecked());
                }
            });
        }
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            String lastBookName = "";
            for (BookRecord book : bookRecords) {
                if (!lastBookName.equals(book.getBookName())) {
                    lastBookName = book.getBookName();
                    out.println("#" + lastBookName);
                }
                String val = book.getChecked() == null ? " " : book.getChecked() ? "+" : "-";
                out.println(String.format("%s %10d %10d     %s", val, book.getSize(), book.getCrc32(), book.getName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    // 0123456789012345678901234567890
    // + 1234567890 1234567890 ASDF
    /*public static void readBookRecords(boolean onlyForSite) throws IOException {
        List<BookRecord> newBookRecords = new ArrayList<>();
        File file = new File(Config.calibreDbPath + FILES_LIST);
        if (file.exists()) {
            Files.readAllLines(file.toPath(), Charset.defaultCharset() ).stream().filter(r -> !r.startsWith("#")).forEach(r -> {
                // TODO we need book name here???
                BookRecord bookRecord = new BookRecord();
                switch (r.charAt(0)) {
                    case ' ': bookRecord.setChecked(null); break;
                    case '-': bookRecord.setChecked(false); break;
                    case '+': bookRecord.setChecked(true); break;
                    default: throw  new RuntimeException(r);
                }
                bookRecord.setSize(Long.valueOf(r.substring(2, 12).trim()));
                bookRecord.setCrc32(Long.valueOf(r.substring(12, 24).trim()));
                bookRecord.setName(r.substring(25).trim());
                if (bookRecordMap != null && bookRecordMap.get(bookRecord.getCrc32()) != null && bookRecordMap.get(bookRecord.getCrc32()).getChecked() != null) {
                    bookRecord.setChecked(bookRecordMap.get(bookRecord.getCrc32()).getChecked());
                }
                if (onlyForSite && bookRecord.getChecked()) {
                    newBookRecords.add(bookRecord);
                } else {
                    newBookRecords.add(bookRecord);
                }
            });
            bookRecords = newBookRecords;
            List<List<BookRecord>> s = bookRecords.stream().collect(groupingBy(BookRecord::getCrc32)).values().stream().filter(v -> v.size() != 1).collect(toList());
            bookRecordMap = bookRecords.stream().collect(toMap(BookRecord::getCrc32, Function.identity()));
        } else {
            bookRecords = new ArrayList<>();
            bookRecordMap = new HashMap<>();
        }
    }*/

    private static void copyFile(Path srcBook, Path destPath, String fileName, String ext, boolean onlyForSite, int i, int size) {
        //if (!onlyForSite || bookRecordMap.get(SevenZipUtils.crc32(srcBook)).getChecked()) {
            System.out.println(String.format("%d of %d: Copy: %s", i, size, srcBook));
            Path destBook = SevenZipUtils.findFreeFileName(destPath, fileName, ext.toLowerCase(), 0);
            try {
                Files.copy(srcBook, destBook, REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        /*} else {
            System.out.println(String.format("%d of %d: Skip: %s", i, size, srcBook));
        }*/
    }

    private static Set<String> imgs = new HashSet<>(Arrays.asList("jpeg", "jpg", "png", "tif", "tiff", "gif", "exe", "py", "html", "gs0", "diz"));

    private static boolean uncompress(List<ArchiveEntry> fileNames) {
        Set<String> exts = fileNames.stream().map(ArchiveEntry::getName).map(SevenZipUtils::getExtension).map(String::toLowerCase).collect(toSet());
        String joined = fileNames.stream().map(ArchiveEntry::getName).collect(joining());
        return Collections.disjoint(exts, imgs) && !joined.contains("\\") && !joined.contains("/");
    }



    public static void deleteFileOrFolder(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
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
            public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
                    throws IOException {
                if (e != null) return handleException(e);
                Files.delete(dir);
                return CONTINUE;
            }
        });
    }

}

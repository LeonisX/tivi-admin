package md.leonis.tivi.admin.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import md.leonis.tivi.admin.model.media.*;
import md.leonis.tivi.admin.model.media.links.*;

import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.BookUtils.calibreBooks;

public class CalibreUtils {

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
        });

        List<AuthorLink> bookAuthors = selectAllFrom("books_authors_link", AuthorLink.class);
        List<Author> authors = selectAllFrom("authors", Author.class);
        calibreBooks.forEach(calibreBook -> {
            List<Long> ids = bookAuthors.stream().filter(a -> a.getBook().equals(calibreBook.getId())).map(AuthorLink::getAuthor).collect(toList());
            calibreBook.setAuthors(authors.stream().filter(author -> ids.contains(author.getId())).collect(toList()));
        });

        Integer[] c = {1, 2, 4, 6, 7, 10, 11, 12, 13, 14, 15, 16};

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

        calibreBooks.forEach(System.out::println);
        return calibreBooks;
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

    private static String escape(String value) {
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

    public static Connection conn;

    private static Connection connect() {
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
}

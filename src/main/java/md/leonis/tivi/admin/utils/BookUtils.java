package md.leonis.tivi.admin.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import md.leonis.tivi.admin.model.*;
import md.leonis.tivi.admin.model.media.*;
import md.leonis.tivi.admin.model.mysql.TableStatus;
import md.leonis.tivi.admin.view.media.AuditController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static java.util.stream.Collectors.*;

public class BookUtils {

    public static List<CalibreBook> calibreBooks = new ArrayList<>();

    public static Actions action;

    public static List<BookCategory> categories = new ArrayList<>();

    public static List<VideoView> siteBooks = new ArrayList<>();

    public static ListVideousSettings listBooksSettings = new ListVideousSettings();

    static {
        Type type = new TypeToken<List<TableStatus>>() {
        }.getType();
        tableStatuses = JsonUtils.gson.fromJson(queryRequest("SHOW TABLE STATUS"), type);
    }

    private static Type videosType = new TypeToken<List<Video>>() {
    }.getType();

    public static List<TableStatus> tableStatuses;

    private static ColumnsResolver MediaResolver = new ColumnsResolver("danny_media");


    public static void auditBooks() {
        JavaFxUtils.showPane("media/Audit.fxml");
    }

    public static void compareCalibreDbs() {
        JavaFxUtils.showPane("media/CalibreCompare.fxml");
    }

    public static void compareWithSite() {
        JavaFxUtils.showPane("media/SiteCompare.fxml");
    }


    public static String queryRequest(String query) {
        //System.out.println(query);
        try {
            String requestURL = Config.apiPath + "media.php?to=query&query_string=" + URLEncoder.encode(query, "cp1251");
            if (requestURL.length() > 8000) {
                String fileName = UUID.randomUUID().toString() + ".sql";
                //String result = BookUtils.upload("api2d/backup", fileName, new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
                String result = BookUtils.upload("api2d/backup", fileName, new ByteArrayInputStream(query.getBytes("cp1251")));
                System.out.println(result);
                requestURL = Config.apiPath + "dumper.php?to=restoreRaw&file=" + fileName;
                System.out.println(requestURL);
                result = WebUtils.readFromUrl(requestURL);
                System.out.println(result);
                return result;
            }
            System.out.println(requestURL);
            String jsonString = WebUtils.readFromUrl(requestURL);
            //String jsonString = ascii2Native(WebUtils.readFromUrl(requestURL));
            int len = jsonString.length() > 1024 ? 1024 : jsonString.length();
            //System.out.println(jsonString.substring(0, len));
            return jsonString;
            //videos = JsonUtils.gson.fromJson(jsonString, videosType);
        } catch (IOException e) {
            System.out.println("Error");
        }
        return null;
    }

    public static String rawRawQueryRequest(String query) {
        System.out.println(query);
        try {
            String requestURL = Config.apiPath + "media.php?to=raw_raw_query&query_string=" + URLEncoder.encode(query, Config.encoding);
            String jsonString = WebUtils.readFromUrl(requestURL);
            //String jsonString = ascii2Native(WebUtils.readFromUrl(requestURL));
            int len = jsonString.length() > 1024 ? 1024 : jsonString.length();
            System.out.println(jsonString.substring(0, len));
            return jsonString;
            //videos = JsonUtils.gson.fromJson(jsonString, videosType);
        } catch (IOException e) {
            System.out.println("Error");
        }
        return null;
    }

    public static String rawQueryRequest(String query) {
        //System.out.println(query);
        try {
            String requestURL = Config.apiPath + "media.php?to=raw_query&query_string=" + URLEncoder.encode(query, Config.encoding);
            String result = WebUtils.readFromUrl(requestURL);
            //String result = ascii2Native(WebUtils.readFromUrl(requestURL));
            int len = result.length() > 1024 ? 1024 : result.length();
            //System.out.println(result.substring(0, len));
            return result;
        } catch (IOException e) {
            System.out.println("Error");
        }
        return null;
    }

    /*public static String ascii2Native(String str) {
        StringBuilder sb = new StringBuilder();
        int begin = 0;
        int index = str.indexOf(PREFIX);
        while (index != -1) {
            sb.append(str.substring(begin, index));
            sb.append(ascii2Char(str.substring(index, index + 6)));
            begin = index + 6;
            index = str.indexOf(PREFIX, begin);
        }
        sb.append(str.substring(begin));
        return sb.toString();
    }

    private static String PREFIX = "\\u";

    private static char ascii2Char(String str) {
        if (str.length() != 6) {
            throw new IllegalArgumentException(
                    "Ascii string of a native character must be 6 character.");
        }
        if (!PREFIX.equals(str.substring(0, 2))) {
            throw new IllegalArgumentException(
                    "Ascii string of a native character must start with \"\\u\".");
        }
        String tmp = str.substring(2, 4);
        int code = Integer.parseInt(tmp, 16) << 8;
        tmp = str.substring(4, 6);
        code += Integer.parseInt(tmp, 16);
        return (char) code;
    }*/

    /*public static String getInsertQuery(Object object, Class<?> bookClass) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
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
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
                return "'" + formatter.format((LocalDateTime) v) + "'";
            } else {
                return v.toString();
            }
            //TODO arrays - ignore???
        }).collect(Collectors.joining(","));

        String sql = "INSERT INTO " + object.getClass().getSimpleName().toLowerCase() + "(" + campos + ")values(" + valores + ");";
        return sql;
    }*/

    public static String objectToSqlInsertQuery(Object object, Type clazz, String tableName) {
        String json = JsonUtils.gson.toJson(object, clazz);
        return jsonToSqlInsertQuery("[" + json + "]", tableName, BookUtils.MediaResolver);
    }

    public static String jsonToSqlInsertQuery(String json, String tableName, ColumnsResolver columnsResolver) {
        StringBuilder sb = new StringBuilder();

        boolean isFirst = true;
        Type type = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        List<Map<String, Object>> rows = JsonUtils.gson.fromJson(json, type);

        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String values = row.entrySet().stream().map(columnsResolver::resolve).collect(Collectors.joining(", "));
                if (!isFirst) {
                    sb.append(",\n");
                } else {
                    sb.append(String.format("INSERT INTO `%s` VALUES\n", tableName));
                }
                sb.append("(").append(values).append(")");
                isFirst = false;
            }
        }
        if (!isFirst) {
            sb.append(";\n\n");
        }
        return sb.toString();
    }

    public static String comparisionResultToSqlUpdateQuery(Map.Entry<Video, List<Pair<String, Pair<String, String>>>> entry, String tableName) {
        List<String> ops = new ArrayList<>();
        //TODO dates???
        entry.getValue().forEach(e -> ops.add(e.getKey() + "=" + (e.getValue().getValue() == null ? "NULL" : "'" + e.getValue().getValue().replace("'", "''") + "'")));
        return String.format("UPDATE `%s` SET %s WHERE downid=%d;\n", tableName, ops.stream().collect(joining(", ")), entry.getKey().getId());
    }

    public static void dumpDB() throws FileNotFoundException {
        String tableName = "danny_media";
        String json = dumpBaseAsJson(tableStatuses.stream().filter(t -> t.getName().equals(tableName)).findFirst().get());

        // RAW type -->> ideal for INSERT QUERY generation - all escaped
        String res = jsonToSqlInsertQuery(json, tableName, MediaResolver);

        List<Video> vids = JsonUtils.gson.fromJson(json, videosType);

        // Тоже работает :)
        json = JsonUtils.gson.toJson(vids, videosType);
        res = jsonToSqlInsertQuery(json, tableName, MediaResolver);

        System.out.println(res);
        /*json = JsonUtils.gson.toJson(vids, fieldType);

        json = ascii2Native(json);*/

        String path = Config.workPath + tableName + "-" + LocalDateTime.now().toString().replace(":", "-") + ".txt";
        try (PrintWriter out = new PrintWriter(path)) {
            out.println(json);
        }
        System.out.println("Dumped to: " + path);

        dumpDBAsNativeSql(tableName);
    }


    public static void dumpDBAsNativeSql(String tableName) {
        new File(Config.workPath + "nat").mkdirs();
        int count = 0;
        int maxTries = 15;
        while (true) {
            try {
                String requestURL = Config.apiPath + String.format("dumper.php?to=backup&drop_table=true&create_table=true&tables=%s&format=sql&comp_level=9&comp_method=1", tableName);
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


    public static String dumpBaseAsJson(TableStatus table) {
        new File(Config.workPath + "gen").mkdirs();
        List<String> jsons = new ArrayList<>();
        long offset = 0;
        long limit = 1 + Math.round(1048576 / (table.getAvgRowLength() * 1.7 + 1));
        String result;
        int page = 1;
        do {
            System.out.println("Page : " + (page++) + " of " + (table.getRows() / limit + 1) + " limit: " + limit);
            int count = 0;
            int maxTries = 15;
            while (true) {
                try {
                    String requestURL = Config.apiPath + String.format("dumper.php?to=backup&tables=%s&offset=%d,%d&format=json&comp_level=9&comp_method=1", table.getName(), offset, limit);
                    String queryId = WebUtils.readFromUrl(requestURL);
                    System.out.println(queryId);
                    String fileName = Config.apiPath + "backup/" + queryId + ".sql.gz";
                    result = gunzipIt(fileName);
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
            if (!result.isEmpty()) {
                result = result.substring(1, result.length() - 1).trim();
            }
            jsons.add(result);
            offset += limit;
            System.out.println(result.substring(0, result.length() > 256 ? 256 : result.length()).trim());
        } while (!result.isEmpty());

        //TODO to sql
        return "[" + jsons.stream().filter(s -> !s.isEmpty()).collect(joining(",")) + "]";
    }


    private static void gunzipIt(String fileName, File newFile) {
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


    private static String gunzipIt(String fileName) throws IOException {
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

    static class LocalDateAdapter implements JsonSerializer<LocalDate> {
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
        }
    }

    static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {
        public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    public static void queryOperation(String query) {
        queryRequest(query);
    }

    public static String prepareQuery(String query) {
        return query.replace("\"", "\\\"");
    }

/*    public static void addVideo() {
        String json = JsonUtils.gson.toJson(calibreBook);
        try {
            addVideo(json, calibreBook.getImage(), null, calibreBook.getPreviousImage());
            System.out.println("OK Add/Edit/Clone Video");
        } catch (Throwable e) {
            System.out.println("Error Add/Edit/Clone Video");
            System.out.println(e.getMessage());
            //TODO window with error
        }
    }*/

    public static void addCategory(int parentId, String catCpu) {
        BookCategory bookCategory = new BookCategory(null, parentId, catCpu, getCatName(catCpu), getCatDescription(catCpu),
                0, getIcon(catCpu), Access.all, Sort.ID.getValue(), Order.ASC.getValue(), YesNo.yes, 0);
        String json = JsonUtils.gson.toJson(bookCategory);
        try {
            //"images/video/thumbs/" + VideoUtils.video.getCpu() + ".png"
            String s = addCategory(json, "", null, "");
            System.out.println(s);
            System.out.println("OK Add/Edit/Clone Category");
        } catch (Throwable e) {
            System.out.println("Error Add/Edit/Clone Category");
            System.out.println(e.getMessage());
            //TODO window with error
        }
    }

    private static String getCatName(String catCpu) {
        //TODO
        return catCpu;
    }

    private static String getCatDescription(String catCpu) {
        //TODO
        return catCpu;
    }

    public static String addCategory(String json, String imageName, InputStream inputStream, String deleteName) throws IOException {
        if (!imageName.isEmpty()) deleteName = "";
        String requestURL = Config.apiPath + "media.php?to=catadd";
        /*if (action == BookUtils.Actions.EDIT) {
            requestURL = Config.apiPath + "media.php?to=catsave";
        }*/
        MultipartUtility multipart;
        try {
            multipart = new MultipartUtility(requestURL, "UTF-8");
            multipart.addHeaderField("User-Agent", "TiVi's admin client");
            /*if (!deleteName.isEmpty()) {
                multipart.addFormField("delete", deleteName);
            }*/
            multipart.addJson("json", json);
            /*if (inputStream != null) {
                multipart.addInputStream("image", imageName, inputStream);
            }*/
        } catch (IOException ex) {
            return ex.getMessage();
        }
        return multipart.finish();
    }

    private static String getIcon(String catCpu) {
        return String.format("images/systems/%s.png", catCpu);
    }

    public static List<BookCategory> readCategories() {
        List<BookCategory> bookCategories = new ArrayList<>();
        String requestURL = Config.apiPath + "media.php?to=cat";
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            bookCategories = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<BookCategory>>() {
            }.getType());
        } catch (IOException e) {
            System.out.println("Error in readVideoCategories");
        }
        return bookCategories;
    }

    public static void listBooks() {
        //$count,$page,$cat,$sort,$order;
        List<Video> videos = new ArrayList<>();
        String cat = "";
        if (listBooksSettings.catId != -1) cat = "&cat=" + listBooksSettings.catId;
        String requestURL = Config.apiPath + "media.php?to=list&count=" + /*listBooksSettings.count*/ Integer.MAX_VALUE + "&page=" + listBooksSettings.page + cat + "&sort=" + listBooksSettings.sort + "&order=" + listBooksSettings.order;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            videos = JsonUtils.gson.fromJson(jsonString, videosType);
        } catch (IOException e) {
            System.out.println("Error in listVideos");
        }
        siteBooks = videos.stream().map(VideoView::new).collect(Collectors.toList());
    }

    public static CalibreBook getBook(int id) {
        String requestURL = Config.apiPath + "media.php?to=get&id=" + id;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            return JsonUtils.gson.fromJson(jsonString, CalibreBook.class);
        } catch (IOException e) {
            System.out.println("Error in getVideo");
        }
        return null;
    }

    public static void deleteBook(int id) {
        String requestURL = Config.apiPath + "media.php?to=delete&id=" + id;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            System.out.println(jsonString);
        } catch (IOException e) {
            System.out.println("Error in deleteVideo");
        }
    }

    static boolean checkCpuExist(String cpu) {
        String requestURL = Config.apiPath + "media.php?to=getByCpu&cpu=" + cpu;
        Video vid = null;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            vid = JsonUtils.gson.fromJson(jsonString, Video.class);
        } catch (IOException e) {
            System.out.println("Error in getVideo");
        }
        return vid != null && vid.getCpu().equals(cpu);
    }

    public static String upload(String path, String imageName, InputStream inputStream) throws IOException {
        String requestURL = Config.apiPath + "upload2.php?to=upload&upload_dir=" + path + "&image_name=" + imageName;
        MultipartUtility multipart;
        try {
            multipart = new MultipartUtility(requestURL, "UTF-8");
            multipart.addHeaderField("User-Agent", "TiVi's admin client");
            if (inputStream != null) {
                multipart.addInputStream("image", imageName, inputStream);
            }
        } catch (IOException ex) {
            return ex.getMessage();
        }
        return multipart.finish();
    }


    public static List<Video> getAllBooks() {
        String json = dumpBaseAsJson(tableStatuses.stream().filter(t -> t.getName().equals("danny_media")).findFirst().get());
        return JsonUtils.gson.fromJson(json, videosType);
    }


    private static BookCategory getParentRoot(List<BookCategory> categories, String cpu) {
        //System.out.println(cpu);
        BookCategory cat = getCategoryByCpu(cpu);
        if (cat.getParentid().equals(0)) {
            return cat;
        } else {
            return getParentRoot(categories, cat.getParentid());
        }
    }

    private static BookCategory getParentRoot(List<BookCategory> categories, Integer id) {
        BookCategory cat = categories.stream().filter(c -> c.getCatid().equals(id)).findFirst().get();
        if (cat.getParentid().equals(0)) {
            return cat;
        } else {
            return getParentRoot(categories, cat.getParentid());
        }
    }

    //TODO processing - тут могут сравниваться и журналы, например, GD. Подменять книгу на журнал
    public static ComparisionResult<Video> compare(List<CalibreBook> allCalibreBooks, List<Video> siteBooks, List<BookCategory> categories, String category) {
        BookUtils.categories = categories;
        if (getParentRoot(categories, category).getCatcpu().equals("magazines") && !category.equals("gd")) {
            return compareMagazines(allCalibreBooks, siteBooks, categories, category);
        }
        List<CalibreBook> calibreBooks = allCalibreBooks.stream().filter(b -> b.getType().equals("book"))
                .filter(b -> b.getOwn() != null && b.getOwn()).collect(toList());

        List<String> multi = Arrays.asList("consoles", "computers"); //computers реально не задействован - только для журналов.
        if (multi.contains(category)) {
            // При multi не нужно искать упоминания в журналах
            calibreBooks = calibreBooks.stream().filter(b -> /*b.getTags().size() > 1 ||*/ (b.getTags().size() == 1 && multi.contains(b.getTags().get(0).getName()))).filter(b -> {
                List<String> p = b.getTags().stream().map(Tag::getName)
                        .map(t -> getParentRoot(categories, t)).map(BookCategory::getCatcpu).distinct().collect(toList());
                return p.size() == 1 && p.contains(category);
            }).collect(toList());
        } else {
            calibreBooks = calibreBooks.stream().filter(b -> b.getTags().stream().map(Tag::getName).collect(toList()).contains(category)).collect(toList());
        }

        //TODO подумать как убрать этот хак
        calibreBooks.forEach(b -> {
            if (b.getTextMore() == null) {
                b.setTextMore("");
            }
        });


        siteBooks = siteBooks.stream().filter(b -> b.getCategoryId().equals(getCategoryByCpu(category).getCatid())).collect(toList());

        //Если в Calibre null, 0 - значит добавленные
        Collection<Video> addedBooks = calibreBooks.stream().filter(b -> b.getTiviId() == null || b.getTiviId() < 1).map(b -> calibreToVideo(b, category))
                .peek(b -> b.setCategoryId(getCategoryByCpu(category).getCatid())).collect(toList());

        //calibre -> site
        List<Video> oldBooks = calibreBooks.stream().filter(b -> b.getTiviId() != null && b.getTiviId() > 0).map(b -> calibreToVideo(b, category)).collect(toList());


        //oldbooks - генерить
        // - мануалами (солюшенами) и другими страницами
        for (String type : listTypeTranslationMap.keySet()) { //doc, emu, giude, manual
            generateManualsPage(allCalibreBooks, siteBooks, category, addedBooks, oldBooks, type);
        }
        // - других книгах,
        generateCitationsPage(allCalibreBooks, siteBooks, category, addedBooks, oldBooks);
        // - так же страница с поиском книг
        generateSearchPage(allCalibreBooks, siteBooks, category, addedBooks, oldBooks);
        // - упоминания в журналах
        for (String type : viewTypeTranslationMap.keySet()) { //magazines, comics
            generateMagazinesPage(allCalibreBooks, siteBooks, category, addedBooks, oldBooks, type);
        }

        //Если в Calibre нет нужного ID значит удалённые
        Map<Integer, Video> newIds = oldBooks.stream().collect(Collectors.toMap(Video::getId, Function.identity()));
        Map<Integer, Video> oldIds = siteBooks.stream().filter(b -> b.getCategoryId().equals(getCategoryByCpu(category).getCatid())).collect(Collectors.toMap(Video::getId, Function.identity()));

        Collection<Video> deletedBooks = CalibreUtils.mapDifference(oldIds, newIds);

        //Разницу считаем только у тех, что имеют теги
        List<Video> allBooks = new ArrayList<>(siteBooks);
        allBooks.addAll(oldBooks);
        /*allBooks.addAll(allCalibreBooks.stream()*//*.filter(b -> b.getType().equals("book"))*//*
                .filter(b -> b.getOwn() != null && b.getOwn()).filter(b -> b.getTiviId() != null && b.getTiviId() > 0)
                .map(b -> calibreToVideo(b, category)).collect(toList()));*/
        List<Pair<Video, Video>> changed = allBooks.stream().collect(groupingBy(Video::getId))
                .entrySet().stream().filter(e -> e.getValue().size() == 2)
                .map(e -> {
                    Video siteBook = e.getValue().get(0);
                    Video calibreBook = e.getValue().get(1);
                    calibreBook.setCategoryId(siteBook.getCategoryId());
                    //calibreBook.setCategoryId(getCategoryByCpu(category).getCatid());
                    calibreBook.setImage_align(siteBook.getImage_align());
                    calibreBook.setViews(siteBook.getViews());
                    calibreBook.setLoads(siteBook.getLoads());
                    calibreBook.setLastdown(siteBook.getLastdown());
                    calibreBook.setRated_count(siteBook.getRated_count());
                    calibreBook.setTotal_rating(siteBook.getTotal_rating());
                    calibreBook.setComments(siteBook.getComments());
                    if (Math.abs(siteBook.getDate() - calibreBook.getDate()) <= 24 * 60 * 60) {
                        siteBook.setDate(calibreBook.getDate());
                    }
                    return new Pair<>(siteBook, calibreBook);
                })
                .filter(e -> !e.getKey().equals(e.getValue()))
                .collect(toList());

        Map<Video, List<Pair<String, Pair<String, String>>>> changedBooks = changed.stream().collect(Collectors.toMap(Pair::getKey, pair -> {
            List<Pair<String, Pair<String, String>>> res = new ArrayList<>();
            JsonObject oldJsonObject = JsonUtils.gson.toJsonTree(pair.getKey()).getAsJsonObject();
            JsonObject newJsonObject = JsonUtils.gson.toJsonTree(pair.getValue()).getAsJsonObject();
            oldJsonObject.entrySet().forEach(e -> {
                if (!e.getValue().toString().equals(newJsonObject.get(e.getKey()).toString())) {
                    if (e.getKey().equals("public")) {
                        String oldDate = Long.toString(timestampToDate(e.getValue().getAsLong(), 0).toLocalDate().atStartOfDay(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(0))).toEpochSecond());
                        String newDate = Long.toString(timestampToDate(newJsonObject.get(e.getKey()).getAsLong(), 1).toLocalDate().atStartOfDay(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(0))).toEpochSecond());
                        if (!oldDate.equals(newDate)) {
                            Pair<String, String> value = new Pair<>(oldDate, newDate);
                            res.add(new Pair<>(e.getKey(), value));
                        }
                    } else {
                        Pair<String, String> value = new Pair<>(e.getValue().getAsString(), newJsonObject.get(e.getKey()).isJsonNull() ? null : newJsonObject.get(e.getKey()).getAsString());
                        System.out.println("====");
                        System.out.println(value.getKey());
                        System.out.println(value.getValue());
                        res.add(new Pair<>(e.getKey(), value));
                    }
                }
            });
            return res;
        }));

        return new ComparisionResult<>(addedBooks, deletedBooks, changedBooks);
    }

    public static ComparisionResult<Video> compareMagazines(List<CalibreBook> allCalibreBooks, List<Video> siteBooks, List<BookCategory> categories, String category) {
        List<CalibreBook> calibreMagazines = allCalibreBooks.stream().filter(b -> b.getType().equals("magazine") && !category.equals("gd"))
                //.filter(b -> b.getTags().stream().map(Tag::getName).collect(toList()).contains(category))
                .sorted(Comparator.comparing(Book::getSort))
                /*.filter(b -> b.getOwn() != null && b.getOwn())*/.collect(toList());

        Map<CalibreBook, List<CalibreBook>> groupedMagazines = calibreMagazines.stream()/*.filter(b ->*/
                /*b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                        (b.getAltTags() != null && b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)))*/
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()))
                .entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toMap(entry -> entry.getValue().get(0), Map.Entry::getValue));


        //TODO подумать как убрать этот хак
        groupedMagazines.forEach((key, value) -> {
            if (key.getTextMore() == null) {
                key.setTextMore("");
            }
        });

        siteBooks = siteBooks.stream().filter(b -> b.getCategoryId().equals(getCategoryByCpu(category).getCatid())).collect(toList());

        //Если в Calibre null, 0 - значит добавленные
        Collection<Video> addedBooks = groupedMagazines.entrySet().stream().filter(b -> b.getKey().getTiviId() == null || b.getKey().getTiviId() < 1).map(b -> calibreMagazineToVideo(b, category))
                .peek(b -> b.setCategoryId(getCategoryByCpu(category).getCatid())).collect(toList());

        //calibre -> site
        List<Video> oldBooks = groupedMagazines.entrySet().stream().filter(b -> b.getKey().getTiviId() != null && b.getKey().getTiviId() > 0).map(b -> calibreMagazineToVideo(b, category)).collect(toList());

        // страница с поиском журналов
        generateMagazinesSearchPage(allCalibreBooks, siteBooks, category, addedBooks, oldBooks);

        //Если в Calibre нет нужного ID значит удалённые
        Map<Integer, Video> newIds = oldBooks.stream().collect(Collectors.toMap(Video::getId, Function.identity()));
        Map<Integer, Video> oldIds = siteBooks.stream().collect(Collectors.toMap(Video::getId, Function.identity()));

        Collection<Video> deletedBooks = CalibreUtils.mapDifference(oldIds, newIds);

        //Разницу считаем только у тех, что имеют теги
        List<Video> allBooks = new ArrayList<>(siteBooks);
        allBooks.addAll(oldBooks);
        List<Pair<Video, Video>> changed = allBooks.stream().collect(groupingBy(Video::getId))
                .entrySet().stream().filter(e -> e.getValue().size() == 2)
                .map(e -> {
                    Video siteBook = e.getValue().get(0);
                    Video calibreBook = e.getValue().get(1);
                    calibreBook.setCategoryId(siteBook.getCategoryId());
                    calibreBook.setImage_align(siteBook.getImage_align());
                    calibreBook.setViews(siteBook.getViews());
                    calibreBook.setLoads(siteBook.getLoads());
                    calibreBook.setLastdown(siteBook.getLastdown());
                    calibreBook.setRated_count(siteBook.getRated_count());
                    calibreBook.setTotal_rating(siteBook.getTotal_rating());
                    calibreBook.setComments(siteBook.getComments());
                    if (Math.abs(siteBook.getDate() - calibreBook.getDate()) <= 24 * 60 * 60) {
                        siteBook.setDate(calibreBook.getDate());
                    }
                    return new Pair<>(siteBook, calibreBook);
                })
                .filter(e -> !e.getKey().equals(e.getValue()))
                .collect(toList());

        Map<Video, List<Pair<String, Pair<String, String>>>> changedBooks = changed.stream().collect(Collectors.toMap(Pair::getKey, pair -> {
            List<Pair<String, Pair<String, String>>> res = new ArrayList<>();
            JsonObject oldJsonObject = JsonUtils.gson.toJsonTree(pair.getKey()).getAsJsonObject();
            JsonObject newJsonObject = JsonUtils.gson.toJsonTree(pair.getValue()).getAsJsonObject();
            oldJsonObject.entrySet().forEach(e -> {
                if (!e.getValue().toString().equals(newJsonObject.get(e.getKey()).toString())) {
                    if (e.getKey().equals("public")) {
                        String oldDate = Long.toString(timestampToDate(e.getValue().getAsLong(), 0).toLocalDate().atStartOfDay(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(0))).toEpochSecond());
                        String newDate = Long.toString(timestampToDate(newJsonObject.get(e.getKey()).getAsLong(), 1).toLocalDate().atStartOfDay(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(0))).toEpochSecond());
                        if (!oldDate.equals(newDate)) {
                            Pair<String, String> value = new Pair<>(oldDate, newDate);
                            res.add(new Pair<>(e.getKey(), value));
                        }
                    } else {
                        Pair<String, String> value = new Pair<>(e.getValue().getAsString(), newJsonObject.get(e.getKey()).getAsString());
                        System.out.println("====");
                        System.out.println(value.getKey());
                        System.out.println(value.getValue());
                        res.add(new Pair<>(e.getKey(), value));
                    }
                }
            });
            return res;
        }));

        return new ComparisionResult<>(addedBooks, deletedBooks, changedBooks);
    }

    private static Map<String, TypeTranslation> listTypeTranslationMap = new HashMap<>();
    private static Map<String, TypeTranslation> viewTypeTranslationMap = new HashMap<>();


    static {
        listTypeTranslationMap.put("doc", new TypeTranslation("docs", "Documentation", "Документация", "Документация", ""));
        listTypeTranslationMap.put("emulator", new TypeTranslation("emulators", "Emulators descriptions", "Описания эмуляторов", "Описания эмуляторов", ""));
        listTypeTranslationMap.put("guide", new TypeTranslation("guides", "Solutions", "Прохождения, солюшены", "Описания и прохождения игр", ""));
        listTypeTranslationMap.put("manual", new TypeTranslation("manuals", "Manuals", "Мануалы, учебники", "Мануалы", ""));

        viewTypeTranslationMap.put("comics", new TypeTranslation("comics", "", "", "Комиксы и манга по мотивам игр %s", "<p>Мы собрали небольшую коллекцию комиксов, связанных с %s.</p>"));
        viewTypeTranslationMap.put("magazine", new TypeTranslation("magazines", "", "", "Упоминания %s в журналах", "<p>Информацию об играх для %s так же можно найти в периодических изданиях.</p>"));
    }

    private static void generateManualsPage(List<CalibreBook> allCalibreBooks, List<Video> siteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks, String type) {
        TypeTranslation translation = listTypeTranslationMap.get(type);
        List<CalibreBook> calibreBooks = allCalibreBooks.stream().filter(b -> b.getType().equals(type)).collect(toList());
        calibreBooks = calibreBooks.stream().filter(b -> b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category) ).collect(toList());
        if (calibreBooks.isEmpty()) {
            return;
        }
        Optional<Video> manual = siteBooks.stream().filter(b -> b.getCpu().equals(category + "_" + translation.getPlural())).findFirst();
        if (!manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu(category + "_" + translation.getPlural());
            newManual.setCategoryId(getCategoryByCpu(category).getCatid());
            setManualText(calibreBooks, newManual, category, translation);
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else {
            // change
            Video newManual = new Video(manual.get());
            setManualText(calibreBooks, newManual, category, translation);
            oldBooks.add(newManual);
        }
    }

    private static void setManualText(List<CalibreBook> calibreBooks, Video manual, String category, TypeTranslation translation) {
        manual.setTitle(translation.getShortText() + " " + getCategoryByCpu(category).getCatname() + ".");
        manual.setText(String.format("<p><img style=\"border: 1px solid #aaaaaa; float: right; margin: 5px;\" title=\"%s\" src=\"images/books/%s.jpg\" alt=\"%s\" />%s %s</p>",
                translation.getImageTitle(), translation.getPlural(), translation.getImageAlt(), translation.getShortText(), getCategoryByCpu(category).getCatname()));
        //TODO download or link
        manual.setFullText(calibreBooks.stream().map(b -> {
            String authors = b.getAuthors().stream().map(Author::getName).collect(joining(", ")).replace("|", ",");
            if (authors.equalsIgnoreCase("неизвестный")) {
                authors = b.getPublisher().getName();
            }
            return String.format("<p><a href=\"up/media/%s/%s/%s.%s\"><img style=\"float: left; margin-right: 5px;\" src=\"images/book.png\" alt=\"download\" /></a>%s (C) %s, %s</p>",
                    translation.getPlural(), category, b.getFileName() != null ? b.getFileName() : b.getTitle(), b.getDataList().get(0).getFormat().toLowerCase(),
                    trimHtmlTags(b.getTextMore().replace("\n", "")), formatDate(b.getSignedInPrint()), authors);
        }).collect(joining()));
    }

    private static String trimHtmlTags(String text) {
        Element element = Jsoup.parseBodyFragment(text).body();
        return doTrimHtmlTags(element);
    }

    private static String doTrimHtmlTags(Node node) {
        if (node instanceof TextNode) {
            return "<p>" + ((TextNode) node).text() + "</p>";
        }
        if (node.childNodeSize() > 1) {
            return ((Element) node).childNodes().stream().map(c -> {
                if (c instanceof TextNode) {
                    return ((TextNode) c).text();
                } else {
                    return c.outerHtml();
                }
            }).collect(joining());
        } else {
            return doTrimHtmlTags(node.childNodes().get(0));
        }
    }

    private static void generateCitationsPage(List<CalibreBook> allCalibreBooks, List<Video> siteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        List<CalibreBook> calibreBooks = allCalibreBooks.stream().filter(b -> b.getType().equals("book"))
                .filter(b -> b.getOwn() != null && b.getOwn()).collect(toList());

        calibreBooks = calibreBooks.stream().filter(b -> b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)).collect(toList());

        Optional<Video> manual = siteBooks.stream().filter(b -> b.getCpu().equals(category + "_citation")).findFirst();
        if (!calibreBooks.isEmpty() && !manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu(category + "_citation");
            newManual.setCategoryId(getCategoryByCpu(category).getCatid());
            newManual.setTitle("Упоминания в других книгах");
            newManual.setText(String.format("<p>В этих книгах так же можно найти информацию об играх для %s.</p>", getCategoryName(category)));
            StringBuilder sb = new StringBuilder();
            sb.append("<ul class=\"file-info\">\n");
            calibreBooks.forEach(b -> sb.append(String.format("<li><a href=\"media/open/%s.html\">%s</a></li>", b.getCpu(), b.getTitle())));
            sb.append("</ul>\n");
            newManual.setFullText(sb.toString());
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else if (!calibreBooks.isEmpty()) {
            // change
            Video newManual = new Video(manual.get());
            newManual.setText(String.format("<p>В этих книгах так же можно найти информацию об играх для %s.</p>", getCategoryName(category)));
            StringBuilder sb = new StringBuilder();
            sb.append("<ul class=\"file-info\">\n");
            calibreBooks.forEach(b -> sb.append(String.format("<li><a href=\"media/open/%s.html\">%s</a></li>", b.getCpu(), b.getTitle())));
            sb.append("</ul>\n");
            newManual.setFullText(sb.toString());
            oldBooks.add(newManual);
        }
    }

    public static String getCategoryByTags(CalibreBook book) {
        if (book.getTags().size() == 1) {
            return book.getTags().get(0).getName();
        } else {
            return "consoles"; //TODO detect - consoles or computers
        }
    }

    private static void generateSearchPage(List<CalibreBook> allCalibreBooks, List<Video> siteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        List<CalibreBook> calibreBooks = allCalibreBooks.stream().filter(b -> b.getType().equals("book")).filter(b -> b.getOwn() == null || !b.getOwn()).collect(toList());
        calibreBooks = calibreBooks.stream().filter(b ->
                b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                        (b.getAltTags() != null && b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category))).collect(toList()); //TODO multi??
        Optional<Video> manual = siteBooks.stream().filter(b -> b.getCpu().equals(category + "_search")).findFirst();
        if (!calibreBooks.isEmpty() && !manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu(category + "_search");
            newManual.setCategoryId(getCategoryByCpu(category).getCatid());
            newManual.setTitle("Книги в поиске");
            newManual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже книг.</p>");
            newManual.setFullText(generateTableView(calibreBooks));
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else if (!calibreBooks.isEmpty()) {
            // change
            Video newManual = new Video(manual.get());
            newManual.setTitle("Книги в поиске");
            newManual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже книг.</p>");
            newManual.setFullText(generateTableView(calibreBooks));
            oldBooks.add(newManual);
        }
    }

    private static String generateTableView(List<CalibreBook> books) {
        int counter = 1;
        StringBuilder sb = new StringBuilder();
        StringBuilder sbi = new StringBuilder();
        StringBuilder sbt = new StringBuilder();
        sb.append("<p><table style=\"width:600px;\">");
        for (CalibreBook book : books) {
            if (counter == 1) {
                sb.append("<tr>");
            }
            sbi.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\">");
            sbt.append("<td style=\"text-align:center; padding-top: 5px; padding-bottom: 10px;\">");
            if (book.getHasCover() != 0) {
                String imageLink = String.format("images/books/cover/%s/%s.jpg", BookUtils.getCategoryByTags(book), book.getCpu());
                String imageThumb = String.format("images/books/thumb/%s/%s.jpg", BookUtils.getCategoryByTags(book), book.getCpu());
                String imageTitle = book.getOfficialTitle() == null ? book.getTitle() : book.getOfficialTitle();
                String imageAlt = book.getFileName() == null ? book.getTitle() : book.getFileName();
                sbi.append(String.format("<a href=\"%s\"><img style=\"border: 1px solid #aaaaaa;\" title=\"%s\" src=\"%s\" alt=\"%s\" /></a>", imageLink, imageTitle, imageThumb, imageAlt));
            } else {
                String imageThumb = "images/books/nocover.png";
                String imageTitle = book.getOfficialTitle() == null ? book.getTitle() : book.getOfficialTitle();
                String imageAlt = book.getFileName() == null ? book.getTitle() : book.getFileName();
                sbi.append(String.format("<img style=\"border: 1px solid #aaaaaa;\" title=\"%s\" src=\"%s\" alt=\"%s\" />", imageTitle, imageThumb, imageAlt));
            }
            sbt.append(book.getTitle());
            sbi.append("</td>");
            sbt.append("</td>");
            counter++;
            if (counter > 3) {
                sbi.append("</tr><tr>");
                sbt.append("</tr>");
                sb.append(sbi).append(sbt);
                sbi = new StringBuilder();
                sbt = new StringBuilder();
                counter = 1;
            }
        }
        if (counter != 1) {
            for (int i = counter - 1; i <= 3; i++) {
                sbi.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\"></td>");
                sbt.append("<td style=\"text-align:center;\"></td>");
            }
        }
        if (!sbi.toString().isEmpty()) {
            sb.append(sbi).append("</tr>").append(sbt).append("</tr>");
        }
        sb.append("</table></p>");
        return sb.toString();
    }

    private static void generateMagazinesSearchPage(List<CalibreBook> allCalibreBooks, List<Video> siteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks) {
        List<CalibreBook> calibreMagazines = allCalibreBooks.stream().filter(b -> b.getType().equals("magazine") && !category.equals("gd"))
                //.filter(b -> b.getTags().stream().map(Tag::getName).collect(toList()).contains(category))
                .filter(b -> b.getOwn() == null || !b.getOwn()).sorted(Comparator.comparing(Book::getSort)).collect(toList());

        Map<CalibreBook, List<CalibreBook>> groupedMagazines = calibreMagazines.stream()//.filter(b ->
                //b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                //        (b.getAltTags() != null && b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)))
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()))
                .entrySet().stream().collect(Collectors.toMap(entry -> entry.getValue().get(0), Map.Entry::getValue));

        Optional<Video> manual = siteBooks.stream().filter(b -> b.getCpu().equals("magazines_in_search")).findFirst();
        if (!groupedMagazines.isEmpty() && !manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu("magazines_in_search");
            newManual.setCategoryId(getCategoryByCpu(category).getCatid());
            newManual.setTitle("Разыскиваемые журналы");
            newManual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже журналов.</p>");
            StringBuilder sb = new StringBuilder();
            //TODO link
            //TODO table with images
            groupedMagazines.forEach((key, value) -> {
                sb.append(String.format("<h3>%s</h3>", key.getSeries().getName()));
                sb.append("<ul class=\"file-info\">\n");
                value.forEach(c -> sb.append(String.format("<li>%s</li>", c.getTitle())));
                sb.append("</ul>\n");
            });
            newManual.setFullText(sb.toString());
            //TODO
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else if (!groupedMagazines.isEmpty()) {
            // change
            Video newManual = new Video(manual.get());
            newManual.setTitle("Разыскиваемые журналы");
            newManual.setText("<p>Будем очень признательны, если вы пришлёте в адрес сайта электронные версии представленных ниже журналов.</p>");
            StringBuilder sb = new StringBuilder();
            //TODO link
            //TODO table with images
            groupedMagazines.forEach((key, value) -> {
                sb.append(String.format("<h3>%s</h3>", key.getSeries().getName()));
                sb.append("<ul class=\"file-info\">\n");
                value.forEach(c -> sb.append(String.format("<li>%s</li>", c.getTitle())));
                sb.append("</ul>\n");
            });
            newManual.setFullText(sb.toString());
            oldBooks.add(newManual);
        }
    }

    private static void generateMagazinesPage(List<CalibreBook> allCalibreBooks, List<Video> siteBooks, String category, Collection<Video> addedBooks, List<Video> oldBooks, String type) {
        TypeTranslation translation = viewTypeTranslationMap.get(type);
        List<CalibreBook> calibreBooks = allCalibreBooks.stream().filter(b -> b.getType().equals(type)).filter(b -> b.getOwn() != null && b.getOwn()).collect(toList());
        Map<String, List<CalibreBook>> books = calibreBooks.stream().filter(b ->
                b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                        (b.getAltTags() != null && b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)))
                .peek(b -> {if (b.getSeries() == null) {
            b.setSeries(new PublisherSeries(0L, b.getTitle(), ""));}})
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()));
        Optional<Video> manual = siteBooks.stream().filter(b -> b.getCpu().equals(category + "_" + translation.getPlural())).findFirst();
        if (!books.isEmpty() && !manual.isPresent()) {
            //add
            Video newManual = new Video();
            newManual.setCpu(category + "_" + translation.getPlural());
            newManual.setCategoryId(getCategoryByCpu(category).getCatid());
            setMagazineText(books, newManual, category, translation);
            newManual.setUrl("");
            newManual.setMirror("http://tv-games.ru");
            addedBooks.add(newManual);
        } else if (!books.isEmpty()) {
            // change
            Video newManual = new Video(manual.get());
            setMagazineText(books, newManual, category, translation);
            oldBooks.add(newManual);
        }
    }

    private static void setMagazineText(Map<String, List<CalibreBook>> books, Video manual, String category, TypeTranslation translation) {
        manual.setTitle(String.format(translation.getShortText(), getCategoryByCpu(category).getCatname()));
        manual.setText(String.format(translation.getText(), getCategoryName(category)));
        StringBuilder sb = new StringBuilder();
        sb.append("<ul class=\"file-info\">\n");
        books.forEach((key, value) -> sb.append(String.format("<li><a href=\"media/open/%s.html\">%s</a></li>", generateCpu(key), key)));
        sb.append("</ul>\n");
        manual.setFullText(sb.toString());
    }

    public static String generateCpu(String title) {
        return Normalizer.normalize(Translit.toTranslit(title.toLowerCase()), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "_")
                .replaceAll("_*$", "");
    }

    private static LocalDateTime timestampToDate(long timestamp, int offset) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.ofHours(offset)).truncatedTo(ChronoUnit.DAYS);
    }

    private static Video calibreToVideo(CalibreBook calibreBook, String category) {
        Video video = new Video();
        video.setTitle(calibreBook.getTitle());
        if (calibreBook.getTiviId() != null) {
            video.setId(Math.toIntExact(calibreBook.getTiviId()));
        }
        if (calibreBook.getSignedInPrint() != null) {
            if (calibreBook.getSignedInPrint().isBefore(LocalDateTime.of(LocalDate.of(1970, 1, 1), LocalTime.ofNanoOfDay(0)))) {
                video.setDate(4294967295L + 24*60*60 + calibreBook.getSignedInPrint().toEpochSecond(ZoneOffset.ofHours(-2)));
            } else {
                video.setDate(calibreBook.getSignedInPrint().toEpochSecond(ZoneOffset.ofHours(-2)));
            }
        }
        if (calibreBook.getTags().size() > 1 && calibreBook.getTags().stream().map(Tag::getName).collect(toList()).contains(category)) {
            video.setCpu(category + "_" + calibreBook.getCpu());
        } else {
            video.setCpu(calibreBook.getCpu());
        }
        video.setStartDate(0L);
        video.setEndDatedate(0L);

        //TODO url; // locurl //TODO if year > 2007 ==> external
        //TODO right file
        if (calibreBook.getDataList().isEmpty()) {
            video.setUrl("");
        } else {
            video.setUrl(String.format("up/media/%ss/%s/%s.%s", calibreBook.getType(), category, calibreBook.getFileName() == null ? calibreBook.getTitle() : calibreBook.getFileName(), calibreBook.getDataList().get(0).getFormat().toLowerCase()));
        }
        //TODO upload images, files
        video.setMirror(""); // exturl
        video.setAge(""); // extsize
        video.setDescription(getDescription(calibreBook, category));
        video.setKeywords(getKeywords(calibreBook, category));
        video.setText(getTextShort(calibreBook, calibreBook.getCpu()));
        video.setFullText(getTextMore(calibreBook, category));
        video.setUserText("");
        video.setMirrorsname("");
        video.setMirrorsurl("");
        video.setPlatforms("");
        video.setAuthor("");
        video.setAuthorSite("");
        video.setAuthorEmail("");
        video.setImage("");
        video.setOpenGraphImage(""); //image_thumb
        video.setImageAlt("");
        video.setActive(YesNo.yes);
        video.setAccess(Access.all);
        video.setListid(0);
        // TODO tags = "";
        return video;
    }

    private static Video calibreMagazineToVideo(Map.Entry<CalibreBook, List<CalibreBook>> groupedMagazines, String category) {
        CalibreBook calibreBook = groupedMagazines.getKey();
        Video video = new Video();
        video.setTitle(calibreBook.getSeries().getName());
        if (calibreBook.getTiviId() != null) {
            video.setId(Math.toIntExact(calibreBook.getTiviId()));
        }
        if (calibreBook.getSignedInPrint() != null) {
            video.setDate(calibreBook.getSignedInPrint().toEpochSecond(ZoneOffset.ofHours(-2)));
        }
        video.setCpu(generateCpu(calibreBook.getSeries().getName()));
        video.setStartDate(0L);
        video.setEndDatedate(0L);

        video.setUrl("");
        video.setMirror(""); // exturl
        video.setAge(""); // extsize
        //TODO custom
        video.setDescription(getDescription(calibreBook, category));
        //TODO custom
        video.setKeywords(getKeywords(calibreBook, category));
        //TODO что-то обобщённое. продумать что выводить. нужен издатель, с какого года, платформы (все альт), описание
        if (!calibreBook.getOwn()) {
            calibreBook.setHasCover(0);
        }
        String cpu = calibreBook.getHasCover().equals(0) ? groupedMagazines.getValue().stream()
                .filter(b -> b.getOwn() != null && b.getOwn()).filter(b -> b.getHasCover() > 0)
                .sorted(Comparator.comparing(Book::getSort)).map(CalibreBook::getCpu).findFirst().orElse(null) :calibreBook.getCpu();
        video.setText(getTextShort(calibreBook, cpu));
        /*if (calibreBook.getHasCover().equals(0)) {
            CalibreBook bookWithCover = groupedMagazines.getValue().stream().filter(b -> b.getOwn() != null && b.getOwn()).filter(b -> b.getHasCover() > 0).sorted(Comparator.comparing(Book::getSort)).findFirst().orElse(null);
            if (bookWithCover != null) {
                bookWithCover.setHasCover(0);
                video.setText(video.getText().replace(calibreBook.getCpu(), bookWithCover.getCpu()));
            }
        } else {
            groupedMagazines.getValue().get(0).setHasCover(0);
        }*/
        //TODO custom, generate - for all books
        //TODO right file, if year > 2007 -> remote
        String fullText = groupedMagazines.getValue().stream().filter(b -> b.getOwn() != null && b.getOwn()).sorted(Comparator.comparing(Book::getSort))
                .map(b -> {
                    String imageTitle = b.getOfficialTitle() == null ? b.getTitle() : b.getOfficialTitle();
                    String imageAlt = b.getFileName() == null ? b.getTitle() : b.getFileName();
                    String image = String.format("<img style=\"vertical-align: middle;\" width=\"20\" height=\"20\" title=\"%s\" src=\"images/save.png\" alt=\"%s\" />\n", imageTitle, imageAlt);
                    String textMore = String.format("<h3>%s</h3>", b.getTitle()) + getTextShort(b, b.getCpu().equals(cpu) ? null : b.getCpu())

                            + "<span class=\"spoiler\" style=\"display: none;\">" + getTextMore(b, category) + "</span>";
                    return textMore + String.format("<p>%s<a href=\"up/media/%s/%s/%s.%s\" target=\"_blank\"> Скачать %s</a></p><p><br /></p>", image, category, b.getSeries().getName(),
                            b.getFileName() == null ? b.getTitle() : b.getFileName(), b.getDataList().get(0).getFormat().toLowerCase(), b.getTitle());
                })
                .collect(joining("\n"));

        video.setFullText(fullText);
        video.setUserText("");
        video.setMirrorsname("");
        video.setMirrorsurl("");
        video.setPlatforms("");
        video.setAuthor("");
        video.setAuthorSite("");
        video.setAuthorEmail("");
        video.setImage("");
        video.setOpenGraphImage(""); //image_thumb
        video.setImageAlt("");
        video.setActive(YesNo.yes);
        video.setAccess(Access.all);
        video.setListid(0);
        // TODO tags = "";
        return video;
    }

    private static String getTextShort(CalibreBook book, String cpu) {
        StringBuilder sb = new StringBuilder();
        if (cpu != null) {
            String imageLink = String.format("images/books/cover/%s/%s.jpg", BookUtils.getCategoryByTags(book), cpu);
            String imageThumb = String.format("images/books/thumb/%s/%s.jpg", BookUtils.getCategoryByTags(book), cpu);
            String imageTitle = book.getOfficialTitle() == null ? book.getTitle() : book.getOfficialTitle();
            String imageAlt = book.getFileName() == null ? book.getTitle() : book.getFileName();
            sb.append(String.format("<p><a href=\"%s\">", imageLink));
            sb.append(String.format("<img style=\"border: 1px solid #aaaaaa; float: right; margin-left: 10px; margin-top: 4px;\" title=\"%s\" src=\"%s\" alt=\"%s\" /></a></p>\n", imageTitle, imageThumb, imageAlt));
        }
        sb.append("<ul class=\"file-info\">\n");
        if (book.getOfficialTitle() != null && !book.getOfficialTitle().equals(book.getTitle())) {
            sb.append(String.format("<li><span>Название:</span> %s</li>\n", book.getOfficialTitle()));
        }
        if (book.getFileName() != null && !book.getFileName().equals(book.getTitle()) && !book.getFileName().equals(book.getOfficialTitle())) {
            sb.append(String.format("<li><span>Неофициальное название:</span> %s</li>\n", book.getFileName()));
        }
        if (book.getSeries() != null) {
            //TODO may be number, link in future
            sb.append(String.format("<li><span>Серия:</span> %s</li>\n", book.getSeries().getName()));
        }
        if (book.getCompany() != null) {
            sb.append(String.format("<li><span>Компания:</span> %s</li>\n", book.getCompany()));
        }
        if (book.getAuthors() != null && !book.getAuthors().isEmpty() && !book.getAuthors().get(0).getName().equalsIgnoreCase("неизвестный")) {
            String title = book.getAuthors().size() > 1 || book.getAuthors().stream().map(Author::getName).collect(Collectors.joining()).contains("|") ? "ы" : "";
            sb.append(String.format("<li><span>Автор%s:</span> %s</li>\n", title, book.getAuthors().stream().map(Author::getName).collect(joining(", ")).replace("|", ",")));
        }
        if (book.getPublisher() != null && !book.getPublisher().getName().equals("???")) {
            sb.append(String.format("<li><span>Издательство:</span> %s</li>\n", /*book.getPublisher() == null ? "???" :*/ book.getPublisher().getName()));
        }
        if (book.getSignedInPrint() != null) {
            sb.append(String.format("<li><span>Подписано в печать:</span> %s г.</li>\n", formatDate(book.getSignedInPrint())));
        }
        if (book.getPages() != null && book.getPages() > 0) {
            sb.append(String.format("<li><span>Объём:</span> %s страниц</li>\n", book.getPages()));
        }

        if (book.getIsbn() != null) {
            sb.append(String.format("<li><span>ISBN:</span> %s</li>\n", book.getIsbn()));
        }
        if (book.getBbk() != null) {
            sb.append(String.format("<li><span>ББК:</span> %s</li>\n", book.getBbk()));
        }
        if (book.getUdk() != null) {
            sb.append(String.format("<li><span>УДК:</span> %s</li>\n", book.getUdk()));
        }

        if (book.getEdition() != null && book.getEdition() > 0) {
            sb.append(String.format("<li><span>Тираж:</span> %s</li>\n", book.getEdition()));
        }
        if (book.getFormat() != null) {
            sb.append(String.format("<li><span>Формат:</span> %s</li>\n", book.getFormat()));
        }
        if (book.getScannedBy() != null) {
            sb.append(String.format("<li><span>Сканировал:</span> <a rel=\"nofollow\" href=\"%s\">%s</a>\n", book.getSource(), book.getScannedBy()));
        }
        if (book.getPostprocessing() != null) {
            sb.append(String.format("<li><span>Постобработка:</span>%s\n", book.getPostprocessing()));
        }

        sb.append("</ul>\n");

        if (book.getTextShort() != null) {
            sb.append(book.getTextShort()).append("\n");
        }
        if (book.getReleaseNote() != null) {
            sb.append(String.format("<p>%s</p>\n", book.getReleaseNote()));
        }

        PlatformsTranslation translation = platformsTranslationMap.get(book.getType());
        if (book.getTags() != null && book.getTags().size() > 1) {
            String platforms = book.getTags().stream().map(b -> getCategoryName(b.getName())).collect(joining(", "));
            sb.append(String.format(translation.getPlatforms(), platforms));
        }
        if (book.getAltTags() != null && !book.getAltTags().isEmpty()) {
            String platforms = book.getAltTags().stream().map(b -> getCategoryName(b.getValue())).collect(joining(", "));
            if (book.getTags() == null || book.getTags().size() == 1) {
                sb.append(String.format(translation.getPlatforms(), platforms));
            } else {
                sb.append(String.format(translation.getAltPlatforms(), platforms));
            }
        }
        return sb.toString();
    }

    private static String formatDate(LocalDateTime dateTime) {
        if (dateTime.toLocalDate().isBefore(LocalDate.of(1000, 1, 1))) {
            return "???";
        } else if (dateTime.plusHours(4).getDayOfMonth() == 1 && dateTime.plusHours(4).getMonthValue() == 1) {
            return Integer.toString(dateTime.plusHours(4).getYear());
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            return dateTime.plusHours(4).toLocalDate().format(formatter);
        }
    }

    private static String getTextMore(CalibreBook book, String category) {
        //PlatformsTranslation translation = platformsTranslationMap.get(book.getType());
        String result = book.getTextMore();
        /*if (book.getTags() != null && book.getTags().size() > 1) {
            String platforms = book.getTags().stream().map(b -> getCategoryName(b.getName())).collect(joining(", "));
            result += String.format(translation.getPlatforms(), platforms);
        }
        if (book.getAltTags() != null && !book.getAltTags().isEmpty()) {
            String platforms = book.getAltTags().stream().map(b -> getCategoryName(b.getValue())).collect(joining(", "));
            result += String.format(translation.getAltPlatforms(), platforms);
        }*/
        return result;
    }

    private static BookCategory getCategoryByCpu(String cpu) {
        return categories.stream().filter(c -> c.getCatcpu().equals(cpu)).findFirst().get();
    }

    private static String getCategoryName(String cpu) {
        return getCategoryByCpu(cpu).getCatname();
    }

    private static String getDescription(CalibreBook book, String category) {
        PlatformsTranslation translation = platformsTranslationMap.get(book.getType());
        /*System.out.println(book.getTitle());
        System.out.println(book.getOfficialTitle());
        System.out.println(book.getType());
        System.out.println(translation);*/
        return String.format(translation.getDescription(), ((book.getOfficialTitle() == null) ? book.getTitle() : book.getOfficialTitle()),
                getCategoryName(category));
    }

    private static Map<String, PlatformsTranslation> platformsTranslationMap = new HashMap<>();

    //TODO другие типы
    static {
        platformsTranslationMap.put("book", new PlatformsTranslation("Книга", "<p>В книге представлены описания игр для %s</p>",
                "<p>Так же здесь можно найти описания для %s</p>", "Книга %s с описаниями для %s", "описания, прохождения, пароли, секреты, cheats, walkthrough"));
        platformsTranslationMap.put("magazine", new PlatformsTranslation("Журнал", "<p>В журнале представлены описания игр для %s</p>",
                "<p>Так же здесь можно найти описания для %s</p>", "Журнал %s с описаниями для %s", "описания, прохождения, пароли, секреты, cheats, walkthrough"));
        platformsTranslationMap.put("manual", new PlatformsTranslation("Сервисный мануал", "<p>Этот мануал покрывает платформы %s</p>",
                "<p>Так же здесь можно найти информацию о %s</p>", "Мануал %s с описаниями для %s", "описание, устройство, эксплуатация, управление"));
        platformsTranslationMap.put("guide", new PlatformsTranslation("Мануал", "<p>Этот мануал покрывает платформы %s</p>",
                "<p>Так же здесь можно найти информацию о %s</p>", "Мануал %s с описаниями для %s", "описания, прохождения, пароли, секреты, cheats, walkthrough"));

        platformsTranslationMap.put("doc", new PlatformsTranslation("", "<p>%s</p>", "<p>%s</p>", "%s %s", ""));
        platformsTranslationMap.put("emulator", new PlatformsTranslation("", "<p>%s</p>", "<p>%s</p>", "%s %s", ""));
        platformsTranslationMap.put("comics", new PlatformsTranslation("", "<p>%s</p>", "<p>%s</p>", "%s %s", ""));
    }

    private static String getKeywords(CalibreBook book, String category) {
        PlatformsTranslation translation = platformsTranslationMap.get(book.getType());
        List<String> chunks = new ArrayList<>(Arrays.asList(book.getTitle().toLowerCase().replaceAll("[^\\w\\sА-Яа-я]", "").split(" ")));
        chunks.add(book.getType());
        chunks.add(translation.getName());
        if (book.getPublisher() != null) {
            chunks.addAll(Arrays.asList(book.getPublisher().getName().toLowerCase().replaceAll("[^\\w\\sА-Яа-я]", "").split(" ")));
        }
        if (book.getIsbn() != null) {
            chunks.add(book.getIsbn());
        }
        chunks.addAll(Arrays.asList(book.getAuthors().stream().map(Author::getName).filter(n -> !n.equalsIgnoreCase("неизвестный")).collect(joining(" ")).toLowerCase().replaceAll("[^\\w\\sА-Яа-я]", "").split(" ")));
        chunks.add(category);
        chunks.addAll(new ArrayList<>(Arrays.asList(categories.stream().filter(c -> c.getCatcpu().equals(category)).findFirst().get().getCatname().toLowerCase().replaceAll("[^\\w\\sА-Яа-я]", "").split(" "))));
        chunks.add(category);
        // TODO дополнить
        chunks.add(translation.getName());
        chunks.addAll(Arrays.asList(translation.getKeywords().split(", ")));
        if (book.getAltTags() != null) {
            chunks.addAll(book.getAltTags().stream().map(CustomColumn::getValue).collect(toList()));
        }
        return chunks.stream().filter(s -> !s.isEmpty()).distinct().map(String::toLowerCase).collect(joining(", "));
    }

    public static void syncDataWithSite(ComparisionResult<Video> comparisionResult, List<CalibreBook> allCalibreBooks, String calibreDbDirectory, String category) {
        List<String> insertQueries = comparisionResult.getAddedBooks().stream().map(b -> BookUtils.objectToSqlInsertQuery(b, Video.class, "danny_media")).collect(toList());
        List<String> deleteQueries = comparisionResult.getDeletedBooks().stream().map(b -> "DELETE FROM `danny_media` WHERE downid=" + b.getId() + ";").collect(toList());
        List<String> updateQueries = comparisionResult.getChangedBooks().entrySet().stream().filter(b -> !b.getValue().isEmpty()).map(b -> BookUtils.comparisionResultToSqlUpdateQuery(b, "danny_media")).collect(toList());

        List<String> results = deleteQueries.stream().map(BookUtils::queryRequest).collect(toList());
        results.forEach(System.out::println);
        results = insertQueries.stream().map(BookUtils::queryRequest).collect(toList());
        results.forEach(System.out::println);
        results = updateQueries.stream().map(BookUtils::queryRequest).collect(toList());
        results.forEach(System.out::println);

        //TODO "IN" QUERY ??
        String configUrl = Config.sqliteUrl;
        Config.sqliteUrl = getJdbcString(calibreDbDirectory);
        if (!category.equals("magazines")) {
            comparisionResult.getAddedBooks().forEach(b -> {

                if (!(b.getCpu().startsWith(category + "_")) && !(b.getCpu().endsWith("_manuals")) && !(b.getCpu().endsWith("_comics"))
                        && !(b.getCpu().endsWith("_docs") && !(b.getCpu().endsWith("_guides") && !(b.getCpu().endsWith("_emulators"))))) {
                    List<Video> videoList = JsonUtils.gson.fromJson(BookUtils.queryRequest("SELECT * FROM danny_media WHERE cpu='" + b.getCpu() + "' AND catid=" + b.getCategoryId()), videosType);
                    Integer tiviId = videoList.get(0).getId();
                    System.out.println(tiviId);
                    Long bookId = allCalibreBooks.stream().filter(cb -> cb.getCpu() != null && cb.getCpu().equals(b.getCpu())).findFirst().get().getId();

                    CustomColumn cb = CalibreUtils.readObject("SELECT * FROM `custom_column_17` WHERE book=" + bookId, CustomColumn.class);
                    if (cb == null) {
                        String q = String.format("INSERT INTO `custom_column_17` VALUES (null, %d, %d)", bookId, tiviId);
                        Integer newId = CalibreUtils.executeInsertQuery(q);
                        System.out.println(newId);
                    } else {
                        String q = String.format("UPDATE `custom_column_17` SET value=%d WHERE book=%d", tiviId, bookId);
                        Integer newId = CalibreUtils.executeUpdateQuery(q);
                        System.out.println(newId);
                    }
                }
            });
        } else { // magazines
            comparisionResult.getAddedBooks().forEach(b -> {
                if (!b.getCpu().equals("magazines_in_search")) {
                    Long bookId = allCalibreBooks.stream().filter(cb -> cb.getType().equals("magazine")).filter(cb -> cb.getSeries() != null && generateCpu(cb.getSeries().getName()).equals(b.getCpu())).min(Comparator.comparing(Book::getSort)).get().getId();
                    List<Video> videoList = JsonUtils.gson.fromJson(BookUtils.queryRequest("SELECT * FROM danny_media WHERE cpu='" + b.getCpu() + "' AND catid=" + b.getCategoryId()), videosType);
                    Integer tiviId = videoList.get(0).getId();
                    System.out.println(tiviId);

                    CustomColumn cb = CalibreUtils.readObject("SELECT * FROM `custom_column_17` WHERE book=" + bookId, CustomColumn.class);
                    if (cb == null) {
                        String q = String.format("INSERT INTO `custom_column_17` VALUES (null, %d, %d)", bookId, tiviId);
                        Integer newId = CalibreUtils.executeInsertQuery(q);
                        System.out.println(newId);
                    } else {
                        String q = String.format("UPDATE `custom_column_17` SET value=%d WHERE book=%d", tiviId, bookId);
                        Integer newId = CalibreUtils.executeUpdateQuery(q);
                        System.out.println(newId);
                    }
                }
            });
        }
        Config.sqliteUrl = configUrl;
    }

    public static String getJdbcString(String path) {
        return String.format("jdbc:sqlite:%s%smetadata.db", path, File.separatorChar);
    }

    public static void readBooks(AuditController auditController) {
        ProgressForm pForm = new ProgressForm();

        // In real life this task would do something useful and return
        // some meaningful result:
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                calibreBooks = CalibreUtils.readBooks();
                updateProgress(1, 1);
                return null;
            }
        };

        // binds progress of progress bars to progress of task:
        pForm.activateProgressBar(task);

        // in real life this method would get the result of the task
        // and update the UI based on its value:
        task.setOnSucceeded(event -> {
            pForm.getDialogStage().close();
            auditController.updateStatus(true);
            //startButton.setDisable(false);
        });

        task.setOnFailed(event -> {
            final Text text = new Text(event.getSource().getException().getMessage());
            pForm.label.setText(text.getText());

            pForm.dialogStage.setWidth(text.getLayoutBounds().getWidth() + 10);
            auditController.updateStatus(false);
        });

        //startButton.setDisable(true);
        pForm.getDialogStage().show();

        Thread thread = new Thread(task);
        thread.start();
    }

    public static class ProgressForm {
        private final Stage dialogStage;
        private final ProgressBar pb = new ProgressBar();
        private final ProgressIndicator pin = new ProgressIndicator();
        private final Label label = new Label();
        private final Scene scene;

        ProgressForm() {
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setWidth(250);

            pb.setProgress(-1F);
            pin.setProgress(-1F);

            label.setTextFill(Color.RED);
            //label.setMinWidth(500);

            final HBox hb = new HBox();
            hb.setSpacing(5);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().addAll(pb, pin);

            final VBox vBox = new VBox();
            vBox.setSpacing(5);
            vBox.setAlignment(Pos.CENTER);

            vBox.getChildren().addAll(hb, label);

            scene = new Scene(vBox);
            dialogStage.setScene(scene);
        }

        void activateProgressBar(final Task<?> task) {
            pb.progressProperty().bind(task.progressProperty());
            pin.progressProperty().bind(task.progressProperty());
            //label.textProperty().bind(task.messageProperty());
            dialogStage.show();
        }

        Stage getDialogStage() {
            return dialogStage;
        }
    }

}

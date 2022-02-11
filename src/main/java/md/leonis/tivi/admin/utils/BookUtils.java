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
import md.leonis.tivi.admin.utils.archive.GZipUtils;
import md.leonis.tivi.admin.view.media.AuditController;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static md.leonis.tivi.admin.utils.StringUtils.*;

public class BookUtils {

    private static final String NEWS_FILE = "news_page.html";

    public static List<CalibreBook> calibreBooks = new ArrayList<>();

    public static List<BookCategory> categories = new ArrayList<>();

    public static List<Video> siteBooks = new ArrayList<>();

    public static ListVideousSettings listBooksSettings = new ListVideousSettings();

    public static String cloudStorageLink;

    // on cloud: book, magazine, comics
    public static List<String> onSiteList = Arrays.asList(/*"doc", */"emulator", "guide"/*, "manual"*/);

    static {
        Type type = new TypeToken<List<TableStatus>>() {
        }.getType();
        tableStatuses = JsonUtils.gson.fromJson(queryRequest("SHOW TABLE STATUS"), type);
    }

    private static final Type videosType = new TypeToken<List<Video>>() {
    }.getType();

    public static List<TableStatus> tableStatuses;

    private static final ColumnsResolver MediaResolver = new ColumnsResolver("danny_media");

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
            int len = Math.min(jsonString.length(), 1024);
            //System.out.println(jsonString.substring(0, len));
            return jsonString;
            //videos = JsonUtils.gson.fromJson(jsonString, videosType);
        } catch (Exception e) {
            System.out.println("Error");
        }
        return null;
    }

    /*public static String rawRawQueryRequest(String query) {
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
    }*/

    public static String rawQueryRequest(String query) {
        //System.out.println(query);
        try {
            String requestURL = Config.apiPath + "media.php?to=raw_query&query_string=" + URLEncoder.encode(query, Config.encoding);
            String result = WebUtils.readFromUrl(requestURL);
            //String result = ascii2Native(WebUtils.readFromUrl(requestURL));
            int len = Math.min(result.length(), 1024);
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
        return jsonToSqlInsertQuery("[" + json + "]", tableName);
    }

    public static String jsonToSqlInsertQuery(String json, String tableName) {
        StringBuilder sb = new StringBuilder();

        boolean isFirst = true;
        Type type = new TypeToken<List<Map<String, Object>>>() {
        }.getType();
        List<Map<String, Object>> rows = JsonUtils.gson.fromJson(json, type);

        if (rows != null) {
            for (Map<String, Object> row : rows) {
                String values = row.entrySet().stream().map(MediaResolver::resolve).collect(Collectors.joining(", "));
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
        return String.format("UPDATE `%s` SET %s WHERE downid=%d;\n", tableName, String.join(", ", ops), entry.getKey().getId());
    }

    public static void dumpDB() throws FileNotFoundException {
        String tableName = "danny_media";
        String json = dumpBaseAsJson(tableStatuses.stream().filter(t -> t.getName().equals(tableName)).findFirst().get());

        List<Video> vids = JsonUtils.gson.fromJson(json, videosType);
        json = JsonUtils.gson.toJson(vids, videosType);
        String res = jsonToSqlInsertQuery(json, tableName);

        System.out.println(res);

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
                GZipUtils.gunzipItToFile(fileName, newFile);
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
                    result = GZipUtils.gunzipItToString(fileName);
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
            System.out.println(result.substring(0, Math.min(result.length(), 256)).trim());
        } while (!result.isEmpty());

        return "[" + jsons.stream().filter(s -> !s.isEmpty()).collect(joining(",")) + "]";
    }

    public static List<Pair<BookCategory, BookCategory>> compareCategories() {
        List<Pair<BookCategory, BookCategory>> result = categories.stream().map(c -> new Pair<>(c, new BookCategory(c))).collect(toList());
        result.forEach(p -> p.getValue().setTotal(0));
        result.forEach(p -> {
            List<Video> children = siteBooks.stream().filter(b -> b.getCategoryId().equals(p.getValue().getCatid())).collect(toList());
            if (!children.isEmpty()) {
                p.getValue().setTotal(p.getValue().getTotal() + children.size());
                updateParentTotals(p.getValue(), result);
            }
        });
        //TODO remove GD filter
        result.stream().filter(p -> p.getValue().getCatcpu().equals("magazines")).forEach(c -> c.getValue().setTotal(c.getValue().getTotal() +
                result.stream().filter(p -> p.getValue().getCatcpu().equals("gd")).findFirst().get().getKey().getTotal()));
        return result.stream().filter(p -> !p.getKey().getTotal().equals(p.getValue().getTotal())).filter(p -> !p.getValue().getCatcpu().equals("gd")).collect(toList());
    }

    private static void updateParentTotals(BookCategory bookCategory, List<Pair<BookCategory, BookCategory>> result) {
        if (bookCategory.getParentid() == 0) {
            return;
        }
        BookCategory parent = result.stream().filter(c -> c.getValue().getCatid().equals(bookCategory.getParentid())).map(Pair::getValue).findFirst().get();
        parent.setTotal(parent.getTotal() + bookCategory.getTotal());
        updateParentTotals(parent, result);
    }

    //TODO generic
    public static void updateCategoryTotals(BookCategory bookCategory) {
        String query = String.format("UPDATE danny_media_cat SET total = %d WHERE catid = %d", bookCategory.getTotal(), bookCategory.getCatid());
        System.out.println(query);
        String result = rawQueryRequest(query);
        System.out.println(result);
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

    public static void addCategory(int parentId, String catCpu) {
        BookCategory bookCategory = new BookCategory(null, parentId, catCpu, getCategoryName(catCpu), getCategoryByCpu(catCpu).getCatdesc(),
                0, SiteRenderer.getSystemIconLink(catCpu), Access.all, Sort.ID.getValue(), Order.ASC.getValue(), YesNo.yes, 0);
        try {
            String s = addCategory(JsonUtils.gson.toJson(bookCategory));
            System.out.println(s);
            System.out.println("OK Add/Edit/Clone Category");
        } catch (Throwable e) {
            System.out.println("Error Add/Edit/Clone Category");
            System.out.println(e.getMessage());
            //TODO window with error
        }
    }

    public static String addCategory(String json) throws IOException {
        String requestURL = Config.apiPath + "media.php?to=catadd";
        MultipartUtility multipart;
        try {
            multipart = new MultipartUtility(requestURL, "UTF-8");
            multipart.addHeaderField("User-Agent", "TiVi's admin client");
            multipart.addJson("json", json);
        } catch (IOException ex) {
            return ex.getMessage();
        }
        return multipart.finish();
    }

    public static void readCategories() {
        String jsonString = queryRequest("SELECT * FROM danny_media_cat");
        List<BookCategory> bookCategories = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<BookCategory>>() {
        }.getType());
        categories = bookCategories.stream().sorted(Comparator.comparing(BookCategory::getCatcpu)).collect(toList());

        /*List<BookCategory> bookCategories;
        String requestURL = Config.apiPath + "media.php?to=cat";
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            bookCategories = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<BookCategory>>() {
            }.getType());
            categories = bookCategories.stream().sorted(Comparator.comparing(BookCategory::getCatcpu)).collect(toList());
        } catch (IOException e) {
            System.out.println("Error in readCategories");
        }*/
    }

    public static void listBooks() {
        //$count,$page,$cat,$sort,$order;
        siteBooks = new ArrayList<>();
        String cat = "";
        if (listBooksSettings.catId != -1) cat = "&cat=" + listBooksSettings.catId;
        String requestURL = Config.apiPath + "media.php?to=list&count=" + /*listBooksSettings.count*/ Integer.MAX_VALUE + "&page=" + listBooksSettings.page + cat + "&sort=" + listBooksSettings.sort + "&order=" + listBooksSettings.order;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            siteBooks = JsonUtils.gson.fromJson(jsonString, videosType);
        } catch (IOException e) {
            System.out.println("Error in listVideos");
        }
    }

    //TODO same finctional as listBooks() ???
    public static void getSiteBooks() {
        String json = dumpBaseAsJson(tableStatuses.stream().filter(t -> t.getName().equals("danny_media")).findFirst().get());
        siteBooks = JsonUtils.gson.fromJson(json, videosType);
        //TODO remove this GD hack
        siteBooks = siteBooks.stream().filter(b -> b.getCategoryId() != 163).collect(toList());
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

    private static BookCategory getParentRoot(List<BookCategory> categories, String cpu) {
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

    public static ComparisionResult<Video> compare(String category) { //category == cpu
        ComparisionResult<Video> comparisionResult = null;
        if (category == null) {
            for (int i = 0; i < categories.size(); i++) {
                System.out.println(i);
                System.out.println(categories.get(i));
                System.out.println(categories.get(i).getCatcpu());
                ComparisionResult<Video> result = doCompare(categories.get(i).getCatcpu());
                if (comparisionResult == null) {
                    comparisionResult = result;
                } else {
                    comparisionResult.getAddedBooks().addAll(result.getAddedBooks());
                    comparisionResult.getChangedBooks().putAll(result.getChangedBooks());
                    comparisionResult.getDeletedBooks().addAll(result.getDeletedBooks());
                }
            }
        } else {
            comparisionResult = doCompare(category);
        }
        return comparisionResult;
    }

    public static ComparisionResult<Video> doCompare(String category) {

        System.out.println("============================= doCompare: " + category);
        if (getParentRoot(categories, category).getCatcpu().equals("magazines") && !category.equals("gd")) {
            return compareMagazines(category);
        }
        //List<CalibreBook> filteredCalibreBooks = calibreBooks.stream().filter(b -> !b.getType().equals("magazines"))
        List<CalibreBook> filteredCalibreBooks = calibreBooks.stream().filter(b -> b.getType() != null && b.getType().equals("book"))
                .filter(b -> b.getOwn() != null && b.getOwn()).sorted(Comparator.comparing(Book::getTitle)).collect(toList());

        List<String> multi = Arrays.asList("consoles", "computers"); //computers реально не задействован - только для журналов.
        if (multi.contains(category)) {
            // При multi не нужно искать упоминания в журналах
            filteredCalibreBooks = filteredCalibreBooks.stream().filter(b -> /*b.getTags().size() > 1 ||*/ (b.getTags().size() == 1 && multi.contains(b.getTags().get(0).getName()))).filter(b -> {
                List<String> p = b.getTags().stream().map(Tag::getName)
                        .map(t -> getParentRoot(categories, t)).map(BookCategory::getCatcpu).distinct().collect(toList());
                return p.size() == 1 && p.contains(category);
            }).collect(toList());
        } else {
            filteredCalibreBooks = filteredCalibreBooks.stream().filter(b -> b.getTags().stream().map(Tag::getName).collect(toList()).contains(category)).collect(toList());
        }

        //TODO подумать как убрать этот хак
        filteredCalibreBooks.forEach(b -> {
            if (b.getTextMore() == null) {
                b.setTextMore("");
            }
        });

        List<Video> filteredSiteBooks = siteBooks.stream().filter(b -> b.getCategoryId().equals(getCategoryByCpu(category).getCatid())).collect(toList());

        //Если в Calibre null, 0 - значит добавленные
        Collection<Video> addedBooks = filteredCalibreBooks.stream().filter(b -> b.getTiviId() == null || b.getTiviId() < 1).map(b -> calibreToVideo(b, category))
                .peek(b -> b.setCategoryId(getCategoryByCpu(category).getCatid())).collect(toList());

        //calibre -> site
        List<Video> oldBooks = filteredCalibreBooks.stream().filter(b -> b.getTiviId() != null && b.getTiviId() > 0).map(b -> calibreToVideo(b, category)).collect(toList());

        //oldbooks - генерить
        // - мануалами (солюшенами) и другими страницами
        for (String type : listTypeTranslationMap.keySet()) { //doc, emu, guide, manual
            //SiteRenderer.generateManualsPage(filteredCalibreBooks, filteredSiteBooks, category, addedBooks, oldBooks, type);
            SiteRenderer.generateManualsPage(calibreBooks, filteredSiteBooks, category, addedBooks, oldBooks, type);
        }
        // - других книгах,
        //SiteRenderer.generateCitationsPage(filteredCalibreBooks, filteredSiteBooks, category, addedBooks, oldBooks);
        SiteRenderer.generateCitationsPage(calibreBooks, filteredSiteBooks, category, addedBooks, oldBooks);
        // - так же страница с поиском книг
        //SiteRenderer.generateSearchPage(filteredCalibreBooks, filteredSiteBooks, category, addedBooks, oldBooks);
        SiteRenderer.generateSearchPage(calibreBooks, filteredSiteBooks, category, addedBooks, oldBooks);
        // - упоминания в журналах
        for (String type : viewTypeTranslationMap.keySet()) { //magazines, comics
            //SiteRenderer.generateMagazinesPage(filteredCalibreBooks, filteredSiteBooks, category, addedBooks, oldBooks, type);
            SiteRenderer.generateMagazinesPage(calibreBooks, filteredSiteBooks, category, addedBooks, oldBooks, type);
        }

        //Если в Calibre нет нужного ID значит удалённые
        Map<Integer, Video> newIds = oldBooks.stream().collect(Collectors.toMap(Video::getId, Function.identity()));
        Map<Integer, Video> oldIds = filteredSiteBooks.stream().filter(b -> b.getCategoryId().equals(getCategoryByCpu(category).getCatid())).collect(Collectors.toMap(Video::getId, Function.identity()));

        Collection<Video> deletedBooks = new ArrayList<>(CalibreUtils.mapDifference(oldIds, newIds));

        //Разницу считаем только у тех, что имеют теги
        List<Video> allBooks = new ArrayList<>(filteredSiteBooks);
        allBooks.addAll(oldBooks);
        /*allBooks.addAll(allCalibreBooks.stream()*//*.filter(b -> b.getType().equals("book"))*//*
                .filter(b -> b.getOwn() != null && b.getOwn()).filter(b -> b.getTiviId() != null && b.getTiviId() > 0)
                .map(b -> calibreToVideo(b, category)).collect(toList()));*/
        List<Pair<Video, Video>> changed = allBooks.stream().collect(groupingBy(Video::getId))
                .values().stream().filter(videos -> videos.size() == 2)
                .map(videos -> {
                    Video siteBook = videos.get(0);
                    Video calibreBook = videos.get(1);
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
                String ov = e.getValue().isJsonNull() ? "" : clean(e.getValue().getAsString());
                String nv = newJsonObject.get(e.getKey()).isJsonNull() ? "" : clean(newJsonObject.get(e.getKey()).getAsString());
                if (!ov.equals(nv)) {
                    /*try {
                        Files.write(Paths.get("1.txt"), ov.getBytes());
                        Files.write(Paths.get("2.txt"), nv.getBytes());
                    } catch (Exception ee) {

                    }*/
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
        })).entrySet().stream().filter(e -> !e.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ComparisionResult<>(addedBooks, deletedBooks, changedBooks);
    }

    private static String clean(String str) {
        return str.trim().replace("\\r", "").replace("\\n", "")
                .replace("\r", "").replace("\n", "")
                .replace("\u0000", "").replace(" ", "")
                .replace("<p></p><p>", "<p>").replace("</p><p></p>", "</p>");
    }

    public static ComparisionResult<Video> compareMagazines(String category) {
        List<CalibreBook> calibreMagazines = calibreBooks.stream().filter(b -> b.getType().equals(category.equals("magazines") ? "magazine" : category) && !category.equals("gd"))
                //.filter(b -> b.getTags().stream().map(Tag::getName).collect(toList()).contains(category))
                .sorted(Comparator.comparing(Book::getSort))
                /*.filter(b -> b.getOwn() != null && b.getOwn())*/.collect(toList());

        Map<CalibreBook, List<CalibreBook>> groupedMagazines = calibreMagazines.stream()/*.filter(b ->*/
                /*b.getTags().stream().map(Tag::getName).collect(toList()).contains(category) ||
                        (b.getAltTags() != null && b.getAltTags().stream().map(CustomColumn::getValue).collect(toList()).contains(category)))*/
                .peek(b -> {
                    if (b.getSeries() == null) {
                        b.setSeries(new PublisherSeries(0L, b.getTitle(), ""));
                    }
                })
                .collect(groupingBy(calibreBook -> calibreBook.getSeries().getName()))
                .entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .collect(Collectors.toMap(entry -> entry.getValue().get(0), Map.Entry::getValue));

        List<Video> siteMagazines = siteBooks.stream().filter(b -> b.getCategoryId().equals(getCategoryByCpu(category).getCatid())).collect(toList());

        //Если в Calibre null, 0 - значит добавленные
        Collection<Video> addedBooks = groupedMagazines.entrySet().stream().filter(b -> b.getKey().getTiviId() == null || b.getKey().getTiviId() < 1).map(b -> calibreMagazineToVideo(b, category))
                .peek(b -> b.setCategoryId(getCategoryByCpu(category).getCatid())).collect(toList());

        //calibre -> site
        List<Video> oldBooks = groupedMagazines.entrySet().stream().filter(b -> b.getKey().getTiviId() != null && b.getKey().getTiviId() > 0).map(b -> calibreMagazineToVideo(b, category)).collect(toList());

        // страница с поиском журналов
        SiteRenderer.generateMagazinesSearchPage(calibreMagazines, siteMagazines, category, addedBooks, oldBooks);

        //Если в Calibre нет нужного ID значит удалённые
        Map<Integer, Video> newIds = oldBooks.stream().collect(Collectors.toMap(Video::getId, Function.identity()));
        Map<Integer, Video> oldIds = siteMagazines.stream().collect(Collectors.toMap(Video::getId, Function.identity()));

        Collection<Video> deletedBooks = CalibreUtils.mapDifference(oldIds, newIds);

        //Разницу считаем только у тех, что имеют теги
        List<Video> allBooks = new ArrayList<>(siteMagazines);
        allBooks.addAll(oldBooks);
        List<Pair<Video, Video>> changed = allBooks.stream().collect(groupingBy(Video::getId))
                .values().stream().filter(videos -> videos.size() == 2)
                .map(videos -> {
                    Video siteBook = videos.get(0);
                    Video calibreBook = videos.get(1);
                    calibreBook.setCategoryId(siteBook.getCategoryId());
                    calibreBook.setImage_align(siteBook.getImage_align());
                    calibreBook.setViews(siteBook.getViews());
                    calibreBook.setLoads(siteBook.getLoads());
                    calibreBook.setLastdown(siteBook.getLastdown());
                    calibreBook.setRated_count(siteBook.getRated_count());
                    calibreBook.setTotal_rating(siteBook.getTotal_rating());
                    calibreBook.setComments(siteBook.getComments());
                    /*calibreBook.setStartDate(siteBook.getStartDate());
                    calibreBook.setEndDatedate(siteBook.getEndDatedate());
                    calibreBook.setDate(siteBook.getDate());*/
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
                if (!clean(e.getValue().toString()).equals(clean(newJsonObject.get(e.getKey()).toString()))) {
                    /*try {
                        Files.write(Paths.get("1.txt"), clean(e.getValue().toString()).getBytes());
                        Files.write(Paths.get("2.txt"), clean(newJsonObject.get(e.getKey()).toString()).getBytes());
                    } catch (Exception ee) {

                    }*/
                    if (e.getKey().equals("public")) {
                        String oldDate = Long.toString(timestampToDate(e.getValue().getAsLong(), 0).toLocalDate().atStartOfDay(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(0))).toEpochSecond());
                        String newDate = Long.toString(timestampToDate(newJsonObject.get(e.getKey()).getAsLong(), 1).toLocalDate().atStartOfDay(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(0))).toEpochSecond());
                        if (!oldDate.equals(newDate)) {
                            res.add(new Pair<>(e.getKey(), new Pair<>(oldDate, newDate)));
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
        })).entrySet().stream().filter(e -> !e.getValue().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ComparisionResult<>(addedBooks, deletedBooks, changedBooks);
    }


    public static String getCategoryByTags(CalibreBook book) {
        if (book.getTags().size() == 1) {
            return book.getTags().get(0).getName();
        } else {
            return "consoles"; //TODO detect - consoles or computers
        }
    }


    public static String generateCpu(String title) {
        return Normalizer.normalize(StringUtils.toTranslit(title.toLowerCase()), Normalizer.Form.NFD)
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
                video.setDate(4294967295L + 24 * 60 * 60 + calibreBook.getSignedInPrint().toEpochSecond(ZoneOffset.ofHours(-2)));
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

        List<Data> files = getDatasWithFileName(calibreBook);

        if (calibreBook.getDataList().isEmpty()) {
            video.setUrl("");
            video.setMirror("");
        } else {
            if (onSiteList.contains(calibreBook.getType())) {
                video.setUrl(SiteRenderer.getDownloadLink(calibreBook, category, files.get(0)));
                files.remove(0);
            } else {
                video.setUrl("");
                video.setMirror(cloudStorageLink);
                files.clear();
            }
        }
        if (calibreBook.getExternalLink() != null && !calibreBook.getExternalLink().isEmpty()) {
            video.setMirror(calibreBook.getExternalLink());
        } else {
            video.setMirror(cloudStorageLink); // exturl
        }
        video.setAge(""); // extsize
        video.setDescription(getDescription(calibreBook, category));
        video.setKeywords(getKeywords(calibreBook, category));
        video.setText(SiteRenderer.getTextShort(calibreBook, calibreBook.getCpu()));
        //TODO list other files
        video.setFullText(calibreBook.getTextMore());
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

    private static List<Data> getDatasWithFileName(CalibreBook calibreBook) {
        Set<String> fileNames = new HashSet<>();
        if (calibreBook.getDataList() == null) {
            return new ArrayList<>();
        }
        System.out.println(calibreBook.getDataList());
        return calibreBook.getDataList().stream()
                .peek(data -> data.setFileName(findFreeFileName(fileNames, calibreBook.getFileName(), data.getFormat().toLowerCase(), 0))).collect(toList());
    }

/*
    private static boolean getChecked(Data data) {
        if (CalibreUtils.bookRecordMap.get(data.getCrc32()) != null) {
            return CalibreUtils.bookRecordMap.get(data.getCrc32()).getChecked();
        }
        return CalibreUtils.bookRecords.stream().filter(b -> b.getName().equals(data.getFileName())).
    }
*/

    public static String findFreeFileName(Set<String> fileNames, String fileName, String ext, int incr) {
        String result = fileName + incrToString(incr) + "." + ext;
        if (fileNames.contains(result)) {
            return findFreeFileName(fileNames, fileName, ext, ++incr);
        }
        return result;
    }

    private static String incrToString(int incr) {
        switch (incr) {
            case 0:
                return "";
            case 1:
                return " (alt)";
            default:
                return " (alt" + incr + ")";
        }
    }

    private static Video calibreMagazineToVideo(Map.Entry<CalibreBook, List<CalibreBook>> groupedMagazines, String category) {
        CalibreBook calibreBook = groupedMagazines.getValue().get(0);
        Video video = new Video();
        video.setTitle(calibreBook.getSeries().getName());
        if (calibreBook.getTiviId() != null) {
            video.setId(Math.toIntExact(calibreBook.getTiviId()));
        }
        if (calibreBook.getSignedInPrint() != null) {
            video.setDate(calibreBook.getSignedInPrint().toEpochSecond(ZoneOffset.ofHours(-2)));
        }
        if (category.equalsIgnoreCase("comics")) {
            video.setCpu(calibreBook.getCpu());
        } else {
            video.setCpu(generateCpu(calibreBook.getSeries().getName()));
        }
        video.setStartDate(0L);
        video.setEndDatedate(0L);

        if (groupedMagazines.getValue().size() != 1 || calibreBook.getDataList().isEmpty()) {
            video.setUrl("");
        } else {
            //List<Data> files = getDatasWithFileName(calibreBook);
            /*if (onSiteList.contains(calibreBook.getType())) {
                video.setUrl(SiteRenderer.getDownloadLink(calibreBook, category, files.get(0)));
                files.remove(0);
            } else {*/
            video.setUrl(cloudStorageLink);
            //files.clear();
            //}
        }
        if (calibreBook.getExternalLink() != null && !calibreBook.getExternalLink().isEmpty()) {
            video.setMirror(calibreBook.getExternalLink());
        } else {
            video.setMirror(""); // exturl
        }
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
                .sorted(Comparator.comparing(Book::getSort)).map(CalibreBook::getCpu).findFirst().orElse(null) : calibreBook.getCpu();
        video.setText(SiteRenderer.getTextShort(calibreBook, cpu));
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
        //TODO list other (all) files
        video.setFullText(SiteRenderer.getMagazineFullText(groupedMagazines, category, cpu));
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

    public static BookCategory getCategoryByCpu(String cpu) {
        return categories.stream().filter(c -> c.getCatcpu().equals(cpu)).findFirst().get();
    }

    public static BookCategory getCategoryById(Integer id) {
        return categories.stream().filter(c -> c.getCatid().equals(id)).findFirst().get();
    }

    public static String getCategoryName(String cpu) {
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

    public static void syncDataWithSite(ComparisionResult<Video> comparisionResult, String calibreDbDirectory, String cat) {
        //TODO in this situation we will process books as magazines :(
        //We don't need to use category at all
        String category = cat == null ? "" : cat;
        List<String> insertQueries = comparisionResult.getAddedBooks().stream().map(b -> BookUtils.objectToSqlInsertQuery(b, Video.class, "danny_media")).collect(toList());
        List<String> deleteQueries = comparisionResult.getDeletedBooks().stream().map(b -> "DELETE FROM `danny_media` WHERE downid=" + b.getId() + ";").collect(toList());
        List<String> updateQueries = comparisionResult.getChangedBooks().entrySet().stream().filter(b -> !b.getValue().isEmpty()).map(b -> BookUtils.comparisionResultToSqlUpdateQuery(b, "danny_media")).collect(toList());

        generateNewsPage(comparisionResult.getAddedBooks(), comparisionResult.getChangedBooks());

        List<String> results = deleteQueries.stream().map(BookUtils::queryRequest).collect(toList());
        results.forEach(System.out::println);
        results = insertQueries.stream().map(BookUtils::queryRequest).collect(toList());
        results.forEach(System.out::println);
        results = updateQueries.stream().map(BookUtils::queryRequest).collect(toList());
        results.forEach(System.out::println);

        //TODO "IN" QUERY ??
        String configUrl = Config.sqliteUrl;
        Config.sqliteUrl = getJdbcString(calibreDbDirectory);
        //!!!
        if (!category.equals("magazines")) {
            comparisionResult.getAddedBooks().forEach(b -> {

                //!!!
                if (!(b.getCpu().startsWith(category + "_")) && !(b.getCpu().endsWith("_manuals")) && !(b.getCpu().endsWith("_comics"))
                        && !(b.getCpu().endsWith("_docs") && !(b.getCpu().endsWith("_guides") && !(b.getCpu().endsWith("_emulators"))))) {
                    List<Video> videoList = JsonUtils.gson.fromJson(BookUtils.queryRequest("SELECT * FROM danny_media WHERE cpu='" + b.getCpu() + "' AND catid=" + b.getCategoryId()), videosType);
                    Integer tiviId = videoList.get(0).getId();
                    System.out.println(tiviId);
                    System.out.println(b.getCpu());
                    Long bookId = calibreBooks.stream().filter(cb -> cb.getCpu() != null && cb.getCpu().equals(b.getCpu())).findFirst().get().getId();

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
                //TODO we don't have update for tiviId for comics.
                //May be this code don't work
                //!!!
                if (!b.getCpu().equals("magazines_in_search")) {
                    Long bookId = calibreBooks.stream().filter(cb -> cb.getType().equals("magazine")).filter(cb -> cb.getSeries() != null && generateCpu(cb.getSeries().getName()).equals(b.getCpu())).min(Comparator.comparing(Book::getSort)).get().getId();
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

    //TODO in renderer
    private static void generateNewsPage(Collection<Video> addedBooks, Map<Video, List<Pair<String, Pair<String, String>>>> changedBooks) {
        StringBuilder sb = new StringBuilder();
        if (!addedBooks.isEmpty()) {
            sb.append("<h4>Добавленные книги:</h4>\n");
            sb.append("<ul>\n");
            addedBooks.forEach(b -> sb.append(String.format("<li><a href=\"http://tv-games.ru/media/open/%s.html\">%s</a></li>\n", b.getCpu(), b.getTitle())));
            sb.append("</ul>\n");
        }
        if (!changedBooks.isEmpty()) {
            sb.append("<h4>Изменённые книги:</h4>\n");
            sb.append("<ul>\n");
            changedBooks.forEach((b, l) -> sb.append(String.format("<li><a href=\"http://tv-games.ru/media/open/%s.html\">%s</a></li>\n", b.getCpu(), b.getTitle())));
            sb.append("</ul>\n");
        }
        Collection<Video> allBooks = new ArrayList<>(addedBooks);
        allBooks.addAll(changedBooks.entrySet().stream().map(Map.Entry::getKey).collect(toList()));
        sb.append("<br />\n");
        int counter = 1;
        sb.append("<p><table style=\"width:600px;\">\n");
        for (Video book : allBooks) {
            if (counter == 1) {
                sb.append("<tr>\n");
            }
            sb.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\">\n");
            String imageLink = String.format("http://tv-games.ru/media/open/%s.html", book.getCpu());
            String imageThumb = String.format("images/books/thumb/%s/%s.jpg", BookUtils.getCategoryById(book.getCategoryId()), book.getCpu());
            sb.append(String.format("<a href=\"%s\"><img style=\"border: 1px solid #aaaaaa;\" title=\"%s\" src=\"%s\" alt=\"%s\" /></a>\n", imageLink, book.getTitle(), imageThumb, book.getTitle()));
            sb.append("</td>\n");
            counter++;
            if (counter > 3) {
                sb.append("</tr><tr>\n");
                counter = 1;
            }
        }
        if (counter != 1) {
            for (int i = counter - 1; i <= 3; i++) {
                sb.append("<td style=\"vertical-align:bottom;text-align:center;width:200px\"></td>\n");
            }
        }
        sb.append("</tr>\n");
        sb.append("</table></p>\n");
        // save
        File file = new File(Config.calibreDbPath + NEWS_FILE);
        if (file.exists()) {
            file.renameTo(new File(Config.calibreDbPath + NEWS_FILE + ".bak"));
        }
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

            Scene scene = new Scene(vBox);
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

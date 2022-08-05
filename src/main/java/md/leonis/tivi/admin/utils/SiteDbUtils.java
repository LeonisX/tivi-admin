package md.leonis.tivi.admin.utils;

import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import md.leonis.tivi.admin.model.*;
import md.leonis.tivi.admin.model.danneo.*;
import md.leonis.tivi.admin.model.mysql.TableStatus;
import md.leonis.tivi.admin.utils.archive.GZipUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class SiteDbUtils {

    private static final String TABLE_NAME = "danny_media";

    private static final Type videosType = new TypeToken<List<Video>>() {
    }.getType();

    private static final ColumnsResolver mediaResolver = new ColumnsResolver(TABLE_NAME);

    public static ListVideosSettings listBooksSettings = new ListVideosSettings();

    public static List<TableStatus> tableStatuses;

    static {
        Type type = new TypeToken<List<TableStatus>>() {
        }.getType();
        tableStatuses = JsonUtils.gson.fromJson(queryRequest("SHOW TABLE STATUS"), type);
    }

    public static String queryRequest(String query) {
        //System.out.println(query);
        try {
            String requestURL = Config.apiPath + "media.php?to=query&query_string=" + URLEncoder.encode(query, "cp1251");
            if (requestURL.length() > 8000) {
                String fileName = UUID.randomUUID().toString() + ".sql";
                //String result = BookUtils.upload("api2d/backup", fileName, new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8)));
                String result = upload("api2d/backup", fileName, new ByteArrayInputStream(query.getBytes("cp1251")));
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
                String values = row.entrySet().stream().map(mediaResolver::resolve).collect(Collectors.joining(", "));
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
        String json = dumpBaseAsJson(TABLE_NAME);

        List<Video> vids = JsonUtils.gson.fromJson(json, videosType);
        json = JsonUtils.gson.toJson(vids, videosType);
        String res = jsonToSqlInsertQuery(json, TABLE_NAME);

        System.out.println(res);

        String path = Config.outputPath + TABLE_NAME + "-" + LocalDateTime.now().toString().replace(":", "-") + ".txt";
        try (PrintWriter out = new PrintWriter(path)) {
            out.println(json);
        }
        System.out.println("Dumped to: " + path);

        dumpDBAsNativeSql(TABLE_NAME);
    }


    public static void dumpDBAsNativeSql(String tableName) {
        FileUtils.mkdirs(Config.outputPath);
        int count = 0;
        int maxTries = 15;
        while (true) {
            try {
                String requestURL = Config.apiPath + String.format("dumper.php?to=backup&drop_table=true&create_table=true&tables=%s&format=sql&comp_level=9&comp_method=1", tableName);
                String queryId = WebUtils.readFromUrl(requestURL);
                System.out.println(queryId);
                String fileName = Config.apiPath + "backup/" + queryId + ".sql.gz";
                File newFile = new File(Config.outputPath + File.separatorChar + tableName + "-" + LocalDateTime.now().toString().replace(":", "-") + ".sql");
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

    public static String dumpBaseAsJson(String tableName) {
        TableStatus table = tableStatuses.stream().filter(t -> t.getName().equals(tableName)).findFirst()
                .orElseThrow(() -> new RuntimeException("TableStatus is null"));
        FileUtils.mkdirs(Config.outputPath);
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

    public static void updateParentTotals(BookCategory bookCategory, List<Pair<BookCategory, BookCategory>> result) {
        if (bookCategory.getParentid() == 0) {
            return;
        }
        BookCategory parent = result.stream().filter(c -> c.getValue().getCatid().equals(bookCategory.getParentid()))
                .map(Pair::getValue).findFirst().orElseThrow(() -> new RuntimeException("BookCategory is null"));
        parent.setTotal(parent.getTotal() + bookCategory.getTotal());
        updateParentTotals(parent, result);
    }

    //TODO generic
    public static void updateCategoryTotals(BookCategory bookCategory) {
        String query = String.format("UPDATE %s_cat SET total = %d WHERE catid = %d", TABLE_NAME, bookCategory.getTotal(), bookCategory.getCatid());
        System.out.println(query);
        String result = SiteDbUtils.rawQueryRequest(query);
        System.out.println(result);
    }

    public static void addCategory(int parentId, String catCpu) {
        BookCategory bookCategory = new BookCategory(null, parentId, catCpu, BookUtils.getCategoryName(catCpu), BookUtils.getCategoryByCpu(catCpu).getCatdesc(),
                0, SiteRenderer.generateSystemIconLink(catCpu), Access.all, Sort.ID.getValue(), Order.ASC.getValue(), YesNo.yes, 0);
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

    public static List<BookCategory> readCategories() {
        String jsonString = SiteDbUtils.queryRequest(String.format("SELECT * FROM %s_cat", TABLE_NAME));
        List<BookCategory> bookCategories = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<BookCategory>>() {
        }.getType());
        assert bookCategories != null;
        return bookCategories.stream().sorted(Comparator.comparing(BookCategory::getCatcpu)).collect(toList());

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

    public static List<Video> listBooks() {
        //$count,$page,$cat,$sort,$order;
        String cat = "";
        if (listBooksSettings.catId != -1) cat = "&cat=" + listBooksSettings.catId;
        String requestURL = Config.apiPath + "media.php?to=list&count=" + /*listBooksSettings.count*/ Integer.MAX_VALUE + "&page=" + listBooksSettings.page + cat + "&sort=" + listBooksSettings.sort + "&order=" + listBooksSettings.order;
        try {
            String jsonString = WebUtils.readFromUrl(requestURL);
            return JsonUtils.gson.fromJson(jsonString, videosType);
        } catch (IOException e) {
            System.out.println("Error in listVideos");
        }
        return new ArrayList<>();
    }

    //TODO same finctional as listBooks() ???
    public static List<Video> getSiteBooks() {
        String json = SiteDbUtils.dumpBaseAsJson(TABLE_NAME);
        List<Video> siteBooks = JsonUtils.gson.fromJson(json, videosType);
        //TODO remove this GD hack
        siteBooks = siteBooks.stream().filter(b -> b.getCategoryId() != 163).collect(toList());
        return siteBooks;
    }

    public static List<Video> listBooks(String cpu, Integer catId) {
        return JsonUtils.gson.fromJson(SiteDbUtils.queryRequest(String.format("SELECT * FROM %s WHERE cpu='%s' AND catid=%s", TABLE_NAME, cpu, catId)), videosType);
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
}

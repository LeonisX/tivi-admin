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
import md.leonis.tivi.admin.model.*;
import md.leonis.tivi.admin.model.media.CalibreBook;
import md.leonis.tivi.admin.view.media.AuditController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookUtils {

    public static List<CalibreBook> calibreBooks = new ArrayList<>();

    public static Actions action;

    public static List<BookCategory> categories = new ArrayList<>();

    public static List<VideoView> siteBooks = new ArrayList<>();

    public static ListVideousSettings listBooksSettings = new ListVideousSettings();

    public static void auditBooks() {
        JavaFxUtils.showPane("media/Audit.fxml");
    }

    public static void compareCalibreDbs() {
        JavaFxUtils.showPane("media/CalibreCompare.fxml");
    }

    public static void compareSiteDbs() {
        JavaFxUtils.showPane("media/SiteCompare.fxml");
    }


    public static String queryRequest(String query) {
        //System.out.println(query);
        try {
            String requestURL = Config.apiPath + "media.php?to=query&query_string=" + URLEncoder.encode(query, Config.encoding);
            String jsonString = WebUtils.readFromUrl(requestURL);
            //String jsonString = ascii2Native(WebUtils.readFromUrl(requestURL));
            int len = jsonString.length() > 1024 ? 1024 : jsonString.length();
            //System.out.println(jsonString.substring(0, len));
            return jsonString;
            //videos = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<Video>>(){}.getType());
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
            //videos = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<Video>>(){}.getType());
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

    /**
     * Ascii to native string. It's same as execut native2ascii.exe -reverse.
     *
     * @param str ascii string
     * @return native string
     */
    public static String ascii2Native(String str) {
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

    /**
     * Ascii to native character.
     *
     * @param str ascii string
     * @return native character
     */
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
    }

    public static String getInsertQuery(Object object, Class<?> bookClass) {
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
    }

    public static void dumpDB() {

        int from = 0;
        int LIMIT = 1; // Mb
        //TODO
        //long limit = 1 + LIMIT * 1048576 / table.getAvgRowLength() + 1;
        long limit = 400;
        int count;

        List<Video> videos = new ArrayList<>();

        do {
            String query = String.format("SELECT * FROM `%s` LIMIT %d, %d", "danny_media", from, limit);
            String result = queryRequest(query);

            List<Video> vv = JsonUtils.gson.fromJson(result, new TypeToken<List<Video>>() {}.getType());

            System.out.println(result);

            videos.addAll(vv);
            from += limit;
            count = vv.size();
        } while (count > 0);

        String destination = Config.calibreDbPath + "danny_media" + "-"
                + LocalDateTime.now().toString().replace(":", "-") + ".json";

        try {
            FileWriter file = new FileWriter(destination);
            file.write(JsonUtils.gson.toJson(videos).replace("\\u003d", "=").replace("\\u003c", "<").replace("\\u003e", ">"));
            file.flush();
            file.close();
            System.out.println("Dumped to: " + destination);
        } catch (IOException e) {
            e.printStackTrace();
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
            videos = JsonUtils.gson.fromJson(jsonString, new TypeToken<List<Video>>() {
            }.getType());
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

    public static String addVideo(String json, String imageName, InputStream inputStream, String deleteName) throws IOException {
        if (!imageName.isEmpty()) deleteName = "";
        String requestURL = Config.apiPath + "media.php?to=add";
        if (action == Actions.EDIT) {
            requestURL = Config.apiPath + "media.php?to=save";
        }
        MultipartUtility multipart;
        try {
            multipart = new MultipartUtility(requestURL, "UTF-8");
            multipart.addHeaderField("User-Agent", "TiVi's admin client");
            if (!deleteName.isEmpty()) {
                multipart.addFormField("delete", deleteName);
            }
            multipart.addJson("json", json);
            if (inputStream != null) {
                multipart.addInputStream("image", imageName, inputStream);
            }
        } catch (IOException ex) {
            return ex.getMessage();
        }
        return multipart.finish();
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

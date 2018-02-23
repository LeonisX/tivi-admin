package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.view.MainStageController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static String apiPath;
    public static String sitePath;
    public static String sampleVideo;
    public static String encoding;
    public static String sqliteUrl;
    public static String calibreDbPath;
    public static String workPath;
    public static String calibreDbName = "metadata.db";

    static String serverSecret;

    static final String resourcePath = "/" + MainStageController.class.getPackage().getName().replaceAll("\\.", "/") + "/";

    public static void loadProperties() throws IOException {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) throw new FileNotFoundException("Property file not found...");
            Properties prop = new Properties();
            prop.load(inputStream);
            sampleVideo = prop.getProperty("sample.video");
        }
    }

    public static void loadProtectedProperties() throws IOException {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("protected.properties")) {
            Properties prop = new Properties();
            prop.load(inputStream);
            String apiDir = prop.getProperty("api.dir") + "/";
            sitePath = prop.getProperty("site.path") + "/";
            encoding = prop.getProperty("encoding");
            serverSecret = prop.getProperty("server.secret");
            sqliteUrl = prop.getProperty("sqlite.url");
            calibreDbPath = prop.getProperty("calibre.db.path") + File.separatorChar;
            workPath = prop.getProperty("work.path") + File.separatorChar;
            apiPath = sitePath + apiDir;
        }
    }
}

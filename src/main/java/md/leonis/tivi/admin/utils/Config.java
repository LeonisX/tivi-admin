package md.leonis.tivi.admin.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config {

    public static String sitePath;
    public static String apiPath;
    public static String encoding;
    public static String calibreDbFullPath;
    public static String calibreDbPath;
    public static String calibreDbName = "metadata.db";
    public static String outputPath;
    public static boolean debugMode;

    public static String sampleVideo;

    static String serverSecret;

    public static Path home = Paths.get(".");

    public static void loadProperties() throws IOException {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
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
            sitePath = prop.getProperty("site.path");
            apiPath = sitePath +  "/" + apiDir;
            encoding = prop.getProperty("encoding");
            serverSecret = prop.getProperty("server.secret");
            calibreDbPath = prop.getProperty("calibre.db.path") + File.separatorChar;
            calibreDbName = prop.getProperty("calibre.db.name");
            calibreDbFullPath = String.format("%s%s", calibreDbPath, calibreDbName);
            outputPath = prop.getProperty("output.path") + File.separatorChar;
            debugMode = Boolean.parseBoolean(prop.getProperty("debug.mode"));
        }
    }
}

package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.view.MainStageController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    static String apiPath;
    public static String sitePath;
    static String sampleVideo;

    static String serverSecret;
    public static String testDbPassword;

    static final String resourcePath = "/" + MainStageController.class.getPackage().getName().replaceAll("\\.", "/") + "/";

    public static void loadProperties() throws IOException {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) throw new FileNotFoundException("Property file not found...");
            Properties prop = new Properties();
            prop.load(inputStream);
            apiPath = prop.getProperty("api.path");
            sitePath = prop.getProperty("site.path");
            sampleVideo = prop.getProperty("sample.video");
        }
    }

    public static void loadProtectedProperties() throws IOException {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("protected.properties")) {
            Properties prop = new Properties();
            prop.load(inputStream);
            serverSecret = prop.getProperty("server.secret");
            testDbPassword = prop.getProperty("test.db.password");
        }
    }
}

package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.view.MainStageController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    static String apiPath;
    static String sampleVideo;

    static final String resourcePath = "/" + MainStageController.class.getPackage().getName().replaceAll("\\.", "/") + "/";

    public static void loadProperties() throws IOException {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) throw new FileNotFoundException("Property file not found...");
            Properties prop = new Properties();
            prop.load(inputStream);
            apiPath = prop.getProperty("api.path");
            sampleVideo = prop.getProperty("sample.video");
        }
    }
}

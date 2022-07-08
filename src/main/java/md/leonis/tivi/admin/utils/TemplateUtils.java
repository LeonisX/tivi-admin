package md.leonis.tivi.admin.utils;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TemplateUtils {

    public static List<String> processTemplateToFile(Map<String, Object> root, String templateName, Path path) {
        try {
            Template template = loadTemplate(templateName);
            // обработка шаблона и модели данных
            //Writer streamWriter = new OutputStreamWriter(System.out);
            FileUtils.backupFile(path);
            Writer fileWriter = new FileWriter(path.toString());
            Writer stringWriter = new StringWriter();
            // вывод в консоль
            //template.process(root, streamWriter);
            template.process(root, fileWriter);
            template.process(root, stringWriter);
            return Arrays.asList(stringWriter.toString().split(System.lineSeparator()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String processTemplateToString(Map<String, Object> root, String templateName) {
        try {
            Template template = loadTemplate(templateName);
            // обработка шаблона и модели данных
            //Writer streamWriter = new OutputStreamWriter(System.out);
            Writer stringWriter = new StringWriter();
            // вывод в консоль
            //template.process(root, streamWriter);
            template.process(root, stringWriter);
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Template loadTemplate(String templateName) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setAPIBuiltinEnabled(true);
        cfg.setTemplateLoader(new FileTemplateLoader(new File(TemplateUtils.class.getResource("/").getFile())));
        return cfg.getTemplate(String.format("templates/%s.ftl", templateName));
    }
}

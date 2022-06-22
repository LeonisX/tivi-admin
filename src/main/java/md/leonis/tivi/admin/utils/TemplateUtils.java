package md.leonis.tivi.admin.utils;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import javafx.scene.control.TextArea;
import md.leonis.tivi.admin.model.template.ChangelogItem;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static md.leonis.tivi.admin.utils.StringUtils.plural;

public class TemplateUtils {

    public static void main(String[] args) {

        Map<String, Object> root = new HashMap<>();
        root.put("fromDate", "01.01.2022");
        root.put("toDate", "30.06.2022");
        root.put("editedCount", 12345);
        root.put("editedRecordsString", plural("запись", 12345));

        root.put("totalRecords", 12340);
        root.put("totalRecordsString", plural("запись", 12340));


        List<ChangelogItem> changelog = new ArrayList<>();
        //changelog.add(new ChangelogItem("Книг игровой тематики", 800, 20));
        //changelog.add(new ChangelogItem("Комиксов", 15, 0));
        root.put("changelog", changelog);

        TemplateUtils.processTemplateToFile(root, "changelogReport", "changelog.html");
    }

    public static void processTemplateToTextArea(Map<String, Object> root, String templateName, TextArea textArea) {
        try {
            Template template = loadTemplate(templateName);
            // обработка шаблона и модели данных
            Writer writer = new TextAreaWriter(textArea);
            textArea.clear();
            // вывод в TextArea
            template.process(root, writer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void processTemplateToFile(Map<String, Object> root, String templateName, String fileName) {
        try {
            Template template = loadTemplate(templateName);
            // обработка шаблона и модели данных
            Writer writer = new OutputStreamWriter(System.out);
            Path path = Paths.get(Config.calibreDbPath).resolve(fileName);
            FileUtils.backupFile(path);
            Writer writer2 = new FileWriter(path.toString());
            // вывод в консоль
            template.process(root, writer);
            template.process(root, writer2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Template loadTemplate(String templateName) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setTemplateLoader(new FileTemplateLoader(new File(TemplateUtils.class.getResource("/").getFile())));
        return cfg.getTemplate(String.format("templates/%s.ftl", templateName));
    }

    public static class TextAreaWriter extends Writer {

        private TextArea textArea;

        public TextAreaWriter(TextArea textArea) {
            super(textArea);
            this.textArea = textArea;
        }

        @Override
        public void write(char[] cbuf, int off, int len) {
            textArea.appendText(String.valueOf(cbuf));
        }

        @Override
        public void flush() {
            // Do nothing
        }

        @Override
        public void close() {
            textArea = null;
        }
    }
}

package md.leonis.tivi.admin.utils;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import javafx.scene.control.TextArea;
import md.leonis.tivi.admin.model.template.ChangelogItem;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
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



        TemplateUtils.test2(root);
    }

    public static void test(Map<String, Object> root, TextArea textArea) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
            cfg.setTemplateLoader(new FileTemplateLoader(new File(TemplateUtils.class.getResource("/").getFile())));
            // шаблон
            Template temp = cfg.getTemplate("templates/changelogReport.ftl");
            // обработка шаблона и модели данных
            Writer out = new TextAreaWriter(textArea);
            textArea.clear();
            // вывод в консоль
            temp.process(root, out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void test2(Map<String, Object> root) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
            cfg.setTemplateLoader(new FileTemplateLoader(new File(TemplateUtils.class.getResource("/").getFile())));
            // шаблон
            Template temp = cfg.getTemplate("templates/changelogReport.ftl");
            // обработка шаблона и модели данных
            Writer out = new OutputStreamWriter(System.out);
            Writer out2 = new FileWriter(new File(TemplateUtils.class.getResource("/").getFile()).toPath().resolve("changelog.html").toString());
            // вывод в консоль
            temp.process(root, out);
            temp.process(root, out2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

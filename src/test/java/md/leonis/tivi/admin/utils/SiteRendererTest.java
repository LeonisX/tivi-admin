package md.leonis.tivi.admin.utils;

import org.junit.jupiter.api.Test;

public class SiteRendererTest {

    @Test
    public void test() {

        String text = "[b]Final Fantasy 8 (VIII)[/b] - прохождение на русском языке, с секретами и бонусами";

        int count = (text.length() - text.replace("[b]", "").length()) / 3;

        System.out.println("====================");
        System.out.println(text);
        System.out.println(count);

        if (count == 1) {
            System.out.println(text.indexOf("[b]"));
            System.out.println(text.indexOf("[/b]"));
            text = text.substring(text.indexOf("[b]"), text.indexOf("[/b]") + 4);
        }
        System.out.println(text);
    }
}

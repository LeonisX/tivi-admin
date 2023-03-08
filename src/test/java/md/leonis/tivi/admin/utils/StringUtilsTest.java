package md.leonis.tivi.admin.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StringUtilsTest {

    String ascii1 = " \u0001\u0002\u0003\u0004\u0005\u0006\t\n" +
            "\u000E\u000F\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007F";

    String ascii2 = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008A\u008B\u008C\u008D\u008E\u008F\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009A\u009B\u009C\u009D\u009E\u009F ¡¢£¤¥¦§¨©ª«¬\u00AD®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ";

    String cyrillic = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    @Test
    void generateBooksCpu() {
        assertEquals(" 0123456789 ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz ", ascii1.replaceAll("[^\\p{Alnum}]+", " ")); //  !"#$%&'()*+,-./_:;<=>?@_[\]^_`_{|}~
        assertEquals(" ", ascii2.replaceAll("[^\\p{Alnum}]+", " ")); //  ¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ
        assertEquals(" ", cyrillic.replaceAll("[^\\p{Alnum}]+", " ")); // АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя

        assertEquals("0123456789_abcdefghijklmnopqrstuvwxyz_abcdefghijklmnopqrstuvwxyz", StringUtils.generateBooksCpu(ascii1));
        assertEquals("aaaaaa_ceeeeiiii_nooooo_uuuuy_aaaaaa_ceeeeiiii_nooooo_uuuuy_y", StringUtils.generateBooksCpu(ascii2));
        assertEquals("abvgdeejziiklmnoprstufhcchshscyeiuiaabvgdeejziiklmnoprstufhcchshscyeiuia", StringUtils.generateBooksCpu(cyrillic));

        assertEquals("aaeeiioooouuuu_aaeeiioooouuuu", StringUtils.generateBooksCpu("aáeéiíoóöőuúüű AÁEÉIÍOÓÖŐUÚÜŰ"));
        assertEquals("", StringUtils.generateBooksCpu("'\"().,&!?$@#%^*=/\\[];:|<>{}"));
        assertEquals("", StringUtils.generateBooksCpu("-"));
        assertEquals("", StringUtils.generateBooksCpu("_"));
        assertEquals("an", StringUtils.generateBooksCpu("-AN"));
        assertEquals("an", StringUtils.generateBooksCpu("_an"));
        assertEquals("normal_text_123", StringUtils.generateBooksCpu("Normal  Text   123 "));
        assertEquals("russkii_tekst", StringUtils.generateBooksCpu(" Русский     текст"));
        assertEquals("bugs_life", StringUtils.generateBooksCpu("A Bugs Life, The"));
        assertEquals("never_ending_story_end", StringUtils.generateBooksCpu("The Never Ending A Story The End"));
    }

    @Test
    void generateCpu() {
        assertEquals(" 0123456789 ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz ", ascii1.replaceAll("[^\\p{Alnum}]+", " ")); //  !"#$%&'()*+,-./_:;<=>?@_[\]^_`_{|}~
        assertEquals(" ", ascii2.replaceAll("[^\\p{Alnum}]+", " ")); //  ¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ
        assertEquals(" ", cyrillic.replaceAll("[^\\p{Alnum}]+", " ")); // АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя

        assertEquals("0123456789 abcdefghijklmnopqrstuvwxyz abcdefghijklmnopqrstuvwxyz", StringUtils.generateCpu(ascii1));
        assertEquals("aaaaaa ceeeeiiii nooooo uuuuy aaaaaa ceeeeiiii nooooo uuuuy y", StringUtils.generateCpu(ascii2));
        assertEquals("abvgdeejziiklmnoprstufhcchshscyeiuiaabvgdeejziiklmnoprstufhcchshscyeiuia", StringUtils.generateCpu(cyrillic));

        assertEquals("aaeeiioooouuuu aaeeiioooouuuu", StringUtils.generateCpu("aáeéiíoóöőuúüű AÁEÉIÍOÓÖŐUÚÜŰ"));
        assertEquals("", StringUtils.generateCpu("'\"().,&!?$@#%^*=/\\[];:|<>{}"));
        assertEquals("", StringUtils.generateCpu("-"));
        assertEquals("", StringUtils.generateCpu("_"));
        assertEquals("a", StringUtils.generateCpu("-A"));
        assertEquals("a", StringUtils.generateCpu("_a"));
        assertEquals("normal text 123", StringUtils.generateCpu("Normal  Text   123 "));
        assertEquals("russkii tekst", StringUtils.generateCpu(" Русский   текст"));
    }

    @Test
    void cleanString() {
        assertEquals(" 0123456789 ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz ", ascii1.replaceAll("[^\\p{Alnum}]+", " ")); //  !"#$%&'()*+,-./_:;<=>?@_[\]^_`_{|}~
        assertEquals(" ", ascii2.replaceAll("[^\\p{Alnum}]+", " ")); //  ¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ
        assertEquals(" ", cyrillic.replaceAll("[^\\p{Alnum}]+", " ")); // АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя

        assertEquals("0123456789 ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz", StringUtils.cleanString(ascii1));
        assertEquals("AAAAAA CEEEEIIII NOOOOO UUUUY aaaaaa ceeeeiiii nooooo uuuuy y", StringUtils.cleanString(ascii2));
        assertEquals("ABVGDEEJZIIKLMNOPRSTUFHCCHSHSCYEIUIAabvgdeejziiklmnoprstufhcchshscyeiuia", StringUtils.cleanString(cyrillic));

        assertEquals("aaeeiioooouuuu AAEEIIOOOOUUUU", StringUtils.cleanString("aáeéiíoóöőuúüű AÁEÉIÍOÓÖŐUÚÜŰ"));
        assertEquals("", StringUtils.cleanString("'\"().,&!?$@#%^*=/\\[];:|<>{}"));
        assertEquals("", StringUtils.cleanString("-"));
        assertEquals("", StringUtils.cleanString("_"));
        assertEquals("A", StringUtils.cleanString("-A"));
        assertEquals("a", StringUtils.cleanString("_a"));
        assertEquals("Normal Text 123", StringUtils.cleanString("Normal Text 123 "));
        assertEquals("Russkii tekst", StringUtils.cleanString(" Русский текст"));
    }
}

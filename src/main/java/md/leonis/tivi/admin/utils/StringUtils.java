package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.media.PlatformsTranslation;
import md.leonis.tivi.admin.model.media.TypeTranslation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс переводит русский текст в транслит. Например, строка "Текст" будет
 * преобразована в "Tekst".
 * User: Deady
 * Date: 04.12.2007
 * Time: 15:56:47
 */
public class StringUtils {


    private static final String[] charTable = new String[81];

    private static final char START_CHAR = 'Ё';

    static {
        charTable['А' - START_CHAR] = "A";
        charTable['Б' - START_CHAR] = "B";
        charTable['В' - START_CHAR] = "V";
        charTable['Г' - START_CHAR] = "G";
        charTable['Д' - START_CHAR] = "D";
        charTable['Е' - START_CHAR] = "E";
        charTable['Ё' - START_CHAR] = "E";
        charTable['Ж' - START_CHAR] = "ZH";
        charTable['З' - START_CHAR] = "Z";
        charTable['И' - START_CHAR] = "I";
        charTable['Й' - START_CHAR] = "Y";
        charTable['К' - START_CHAR] = "K";
        charTable['Л' - START_CHAR] = "L";
        charTable['М' - START_CHAR] = "M";
        charTable['Н' - START_CHAR] = "N";
        charTable['О' - START_CHAR] = "O";
        charTable['П' - START_CHAR] = "P";
        charTable['Р' - START_CHAR] = "R";
        charTable['С' - START_CHAR] = "S";
        charTable['Т' - START_CHAR] = "T";
        charTable['У' - START_CHAR] = "U";
        charTable['Ф' - START_CHAR] = "F";
        charTable['Х' - START_CHAR] = "H";
        charTable['Ц' - START_CHAR] = "C";
        charTable['Ч' - START_CHAR] = "CH";
        charTable['Ш' - START_CHAR] = "SH";
        charTable['Щ' - START_CHAR] = "SH";
        charTable['Ъ' - START_CHAR] = ""; // "
        charTable['Ы' - START_CHAR] = "Y";
        charTable['Ь' - START_CHAR] = ""; // '
        charTable['Э' - START_CHAR] = "E";
        charTable['Ю' - START_CHAR] = "U";
        charTable['Я' - START_CHAR] = "YA";

        for (int i = 0; i < charTable.length; i++) {
            char idx = (char) ((char) i + START_CHAR);
            char lower = new String(new char[]{idx}).toLowerCase().charAt(0);
            if (charTable[i] != null) {
                charTable[lower - START_CHAR] = charTable[i].toLowerCase();
            }
        }
    }


    /**
     * Переводит русский текст в транслит. В результирующей строке
     * каждая русская буква будет заменена на соответствующую английскую.
     * Не русские символы останутся прежними.
     *
     * @param text исходный текст с русскими символами
     * @return результат
     */
    public static String toTranslit(String text) {
        char charBuffer[] = text.toCharArray();
        StringBuilder sb = new StringBuilder(text.length());
        for (char symbol : charBuffer) {
            int i = symbol - START_CHAR;
            if (i >= 0 && i < charTable.length) {
                String replace = charTable[i];
                sb.append(replace == null ? symbol : replace);
            } else {
                sb.append(symbol);
            }
        }
        return sb.toString();
    }

    public static String choosePluralMerge(long number, String caseOne, String caseTwo, String caseFive) {
        /* Выбирает правильную форму существительного в зависимости от числа.
           Чтобы легко запомнить, в каком порядке указывать варианты, пользуйтесь мнемоническим правилом:
           один-два-пять - один гвоздь, два гвоздя, пять гвоздей.
           [url]http://pyobject.ru/blog/2006/09/02/pytils/[/url]

           in: число и слово в трёх падежах.
           out: строка (число + существительное в нужном падеже).
         */

        String str = Long.toString(number) + " ";
        number = Math.abs(number);

        if (number % 10 == 1 && number % 100 != 11) {
            str += caseOne;
        } else if (number % 10 >= 2 && number % 10 <= 4 && (number % 100 < 10 || number % 100 >= 20)) {
            str += caseTwo;
        } else {
            str += caseFive;
        }

        return str;
    }

    private static List<Declension> declensions = new ArrayList<>();
    private static Map<String, Declension> declensionMap;

    static {
        declensions.add(new Declension("компьютеры", "компьютеров", "компьютерам", "компьютеры", "компьютерами", "о компьютерах"));
        declensions.add(new Declension("приставки", "приставок", "приставкам", "приставки", "приставками", "о приставках"));
        declensions.add(new Declension("arcade", "игровых автоматов", "игровым автоматам", "игровые автоматы", "игровыми автоматами", "об игровых автоматах"));

        // 1. Именительный - КТО ? ЧТО ?
        // 2. Родительный - КОГО ? ЧЕГО ?
        // 3. Дательный - КОМУ ? ЧЕМУ ?
        // 4. Винительный - КОГО ? ЧТО ?
        // 5. Творительный - КЕМ ? ЧЕМ ?
        // 6. Предложный - О КОМ ? О ЧЕМ ?

        declensionMap = declensions.stream().collect(Collectors.toMap(Declension::getIm, item -> item));
    }

    public static Declension getDeclension(String term) {
        return declensionMap.getOrDefault(term.toLowerCase(), new Declension(term, term, term, term, term, term));
    }

    public static Map<String, TypeTranslation> listTypeTranslationMap = new HashMap<>();
    public static Map<String, TypeTranslation> viewTypeTranslationMap = new HashMap<>();

    static {
        listTypeTranslationMap.put("doc", new TypeTranslation("docs", "Documentation for", "Документация для", "Документация для", "")); // род
        listTypeTranslationMap.put("emulator", new TypeTranslation("emulators", "Emulators descriptions for", "Описания эмуляторов", "Описания эмуляторов", "")); // род
        listTypeTranslationMap.put("guide", new TypeTranslation("guides", "Solutions for", "Прохождения, солюшены игр для", "Описания и прохождения игр", "")); // род
        listTypeTranslationMap.put("manual", new TypeTranslation("manuals", "Manuals for", "Мануалы, учебники для", "Мануалы для", "")); // род

        viewTypeTranslationMap.put("comics", new TypeTranslation("comics", "", "", "Комиксы и манга по мотивам игр %s", "<p>Мы собрали небольшую коллекцию комиксов, связанных с %s.</p>"));
        viewTypeTranslationMap.put("magazine", new TypeTranslation("magazines", "", "", "Упоминания %s в журналах", "<p>Информацию об играх для %s так же можно найти в периодических изданиях.</p>"));
    }

    public static Map<String, PlatformsTranslation> platformsTranslationMap = new HashMap<>();

    //TODO другие типы
    static {
        platformsTranslationMap.put("book", new PlatformsTranslation("Книга", "<p>В книге представлены описания игр для %s</p>",
                "<p>Так же здесь можно найти описания для %s</p>", "Книга %s с описаниями для %s", "описания, прохождения, пароли, секреты, cheats, walkthrough"));
        platformsTranslationMap.put("magazine", new PlatformsTranslation("Журнал", "<p>В журнале представлены описания игр для %s</p>",
                "<p>Так же здесь можно найти описания для %s</p>", "Журнал %s с описаниями для %s", "описания, прохождения, пароли, секреты, cheats, walkthrough"));
        platformsTranslationMap.put("manual", new PlatformsTranslation("Сервисный мануал", "<p>Этот мануал покрывает платформы %s</p>",
                "<p>Так же здесь можно найти информацию о %s</p>", "Мануал %s с описаниями для %s", "описание, устройство, эксплуатация, управление"));
        platformsTranslationMap.put("guide", new PlatformsTranslation("Мануал", "<p>Этот мануал покрывает платформы %s</p>",
                "<p>Так же здесь можно найти информацию о %s</p>", "Мануал %s с описаниями для %s", "описания, прохождения, пароли, секреты, cheats, walkthrough"));

        platformsTranslationMap.put("doc", new PlatformsTranslation("", "<p>%s</p>", "<p>%s</p>", "%s %s", ""));
        platformsTranslationMap.put("emulator", new PlatformsTranslation("", "<p>%s</p>", "<p>%s</p>", "%s %s", ""));
        platformsTranslationMap.put("comics", new PlatformsTranslation("", "<p>%s</p>", "<p>%s</p>", "%s %s", ""));
    }

}
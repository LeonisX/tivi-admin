package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.media.PlatformsTranslation;
import md.leonis.tivi.admin.model.media.TypeTranslation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Класс переводит русский текст в транслит. Например, строка "Текст" будет
 * преобразована в "Tekst".
 */
class StringUtils {

    private static final Map<Integer, String> CHAR_MAP;

    static {
        Map<Character, String> upperCharMap = new HashMap<>();
        upperCharMap.put('А', "A");
        upperCharMap.put('Б', "B");
        upperCharMap.put('В', "V");
        upperCharMap.put('Г', "G");
        upperCharMap.put('Д', "D");
        upperCharMap.put('Е', "E");
        upperCharMap.put('Ё', "E");
        upperCharMap.put('Ж', "ZH");
        upperCharMap.put('З', "Z");
        upperCharMap.put('И', "I");
        upperCharMap.put('Й', "Y");
        upperCharMap.put('К', "K");
        upperCharMap.put('Л', "L");
        upperCharMap.put('М', "M");
        upperCharMap.put('Н', "N");
        upperCharMap.put('О', "O");
        upperCharMap.put('П', "P");
        upperCharMap.put('Р', "R");
        upperCharMap.put('С', "S");
        upperCharMap.put('Т', "T");
        upperCharMap.put('У', "U");
        upperCharMap.put('Ф', "F");
        upperCharMap.put('Х', "H");
        upperCharMap.put('Ц', "C");
        upperCharMap.put('Ч', "CH");
        upperCharMap.put('Ш', "SH");
        upperCharMap.put('Щ', "SH");
        upperCharMap.put('Ъ', ""); // "
        upperCharMap.put('Ы', "Y");
        upperCharMap.put('Ь', ""); // '
        upperCharMap.put('Э', "E");
        upperCharMap.put('Ю', "U");
        upperCharMap.put('Я', "YA");

        Map<Character, String> lowerCharMap = upperCharMap.entrySet().stream()
                .collect(Collectors.toMap(e -> Character.toLowerCase(e.getKey()), e -> e.getValue().toLowerCase()));

        CHAR_MAP = Stream.concat(upperCharMap.entrySet().stream(), lowerCharMap.entrySet().stream())
                .collect(Collectors.toMap(e -> (int) e.getKey(), Map.Entry::getValue));
    }

    /**
     * Переводит русский текст в транслит. В результирующей строке
     * каждая русская буква будет заменена на соответствующую английскую.
     * Не русские символы останутся прежними.
     *
     * @param text исходный текст с русскими символами
     * @return результат
     */
    static String toTranslit(String text) {
        return text.chars().mapToObj(c -> {
            String replace = CHAR_MAP.get(c);
            return (replace == null) ? Character.valueOf((char) c).toString() : replace;
        }).collect(Collectors.joining());
    }

    static String choosePluralMerge(long number, String caseOne, String caseTwo, String caseFive) {
        /* Выбирает правильную форму существительного в зависимости от числа.
           Чтобы легко запомнить, в каком порядке указывать варианты, пользуйтесь мнемоническим правилом:
           один-два-пять - один гвоздь, два гвоздя, пять гвоздей.
           [url]http://pyobject.ru/blog/2006/09/02/pytils/[/url]

           in: число и слово в трёх падежах.
           out: строка (число + существительное в нужном падеже).
         */

        String str = number + " ";
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

    private static final List<Declension> declensions = new ArrayList<>();
    private static final Map<String, Declension> declensionMap;

    static {
        declensions.add(new Declension("компьютеры", "компьютеров", "компьютерам", "компьютеры", "компьютерами", "о компьютерах"));
        declensions.add(new Declension("приставки", "приставок", "приставкам", "приставки", "приставками", "о приставках"));
        declensions.add(new Declension("arcade", "игровых автоматов", "игровым автоматам", "игровые автоматы", "игровыми автоматами", "об игровых автоматах"));
        declensions.add(new Declension("калькуляторы", "калькуляторов", "калькуляторам", "калькуляторы", "калькуляторами", "о калькуляторах"));
        //declensions.add(new Declension("сайт и всё околоигровое", "другими тематиками", "другим тематикам", "других тематик", "других тематик", "о других тематиках"));
        declensions.add(new Declension("мобильные устройства", "мобильных устройств", "мобильным устройствам", "мобильные устройства", "мобильными устройствами", "о мобильных устройствах"));

        //declensions.add(new Declension("cdi", "Philips CD-i", "Philips CD-i", "Philips CD-i", "Philips CD-i", "Philips CD-i"));

        // 1. Именительный - КТО ? ЧТО ?
        // 2. Родительный - КОГО ? ЧЕГО ?
        // 3. Дательный - КОМУ ? ЧЕМУ ?
        // 4. Винительный - КОГО ? ЧТО ?
        // 5. Творительный - КЕМ ? ЧЕМ ?
        // 6. Предложный - О КОМ ? О ЧЕМ ?

        declensionMap = declensions.stream().collect(Collectors.toMap(Declension::getIm, item -> item));
    }

    static Declension getDeclension(String term) {
        return declensionMap.getOrDefault(term.toLowerCase(), new Declension(term, term, term, term, term, term));
    }

    static Map<String, TypeTranslation> listTypeTranslationMap = new HashMap<>();
    static Map<String, TypeTranslation> viewTypeTranslationMap = new HashMap<>();

    static {
        listTypeTranslationMap.put("doc", new TypeTranslation("docs", "Documentation for", "Документация для", "Документация для", "")); // род
        listTypeTranslationMap.put("emulator", new TypeTranslation("emulators", "Emulators descriptions for", "Описания эмуляторов", "Описания эмуляторов", "")); // род
        listTypeTranslationMap.put("guide", new TypeTranslation("guides", "Solutions for", "Прохождения, солюшены игр для", "Описания и прохождения игр", "")); // род
        listTypeTranslationMap.put("manual", new TypeTranslation("manuals", "Manuals for", "Мануалы, учебники для", "Мануалы для", "")); // род

        viewTypeTranslationMap.put("comics", new TypeTranslation("comics", "", "", "Комиксы и манга по мотивам игр %s", "<p>Мы собрали небольшую коллекцию комиксов, связанных с %s.</p>"));
        viewTypeTranslationMap.put("magazine", new TypeTranslation("magazines", "", "", "Упоминания %s в журналах", "<p>Игры для %s так же рассмотрены в следующих журналах.</p>"));
    }

    static Map<String, PlatformsTranslation> platformsTranslationMap = new HashMap<>();

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

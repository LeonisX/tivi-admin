package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.model.Declension;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.PlatformsTranslation;
import md.leonis.tivi.admin.model.calibre.TypeTranslation;
import org.apache.commons.text.StringEscapeUtils;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static md.leonis.tivi.admin.model.Type.*;

/**
 * Класс переводит русский текст в транслит. Например, строка "Текст" будет преобразована в "Tekst".
 * <p>
 * Так же учится склонять и возвращать множественное число
 */
public class StringUtils {

    static List<String> tails = Arrays.asList("unl", "ii", "iii", "iv", "v", "vi", "vii", "viii", "ix");
    static List<String> tailsReplacement = Arrays.asList("", "2", "3", "4", "5", "6", "7", "8", "9");
    static List<String> mids = Arrays.asList("ii", "iii", "iv");
    static List<String> midsReplacement = Arrays.asList("2", "3", "4");

    // ЧПУ для страниц книг
    public static String generateBooksCpu(String text) {
        return Arrays.stream(generateCpu(text).split(" ")).filter(s -> !s.equals("a") && !s.equals("the")).collect(Collectors.joining("_"));
    }

    // ЧПУ, слова разделены пробелами
    public static String generateCpu(String text) {
        text = cleanString(text).toLowerCase();

        for (int i = 0; i < tails.size(); i++) {
            text = replaceFromTail(" " + tails.get(i), " " + tailsReplacement.get(i), text);
        }

        for (int i = 0; i < mids.size(); i++) {
            text = replaceFromTail(" " + mids.get(i) + " ", " " + midsReplacement.get(i) + " ", text);
        }

        return text;
    }

    public static String cleanString(String string) {
        for (char c : " -_+~".toCharArray()) {
            string = string.replace("" + c, " ");
        }
        return Normalizer.normalize(StringUtils.toTranslit(string), Normalizer.Form.NFD) // repair aáeéiíoóöőuúüű AÁEÉIÍOÓÖŐUÚÜŰ
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "") // repair aáeéiíoóöőuúüű AÁEÉIÍOÓÖŐUÚÜŰ
                .replaceAll("[^\\p{Alnum}]+", " ") // all, except [a-zA-Z0-9] convert to a single " "
                .trim().replace("  ", " ");
    }

    public static String replaceFromTail(String substr, String replace, String string) {
        if (string.toLowerCase().endsWith(substr.toLowerCase())) {
            return string.substring(0, string.length() - substr.length()) + replace;
        } else {
            return string;
        }
    }

    public static String sid(String title) {
        if (title.startsWith("Замечание")) {
            return "1";
        } else if (StringUtils.isNotBlank(title)) {
            if (title.endsWith("(Hack)") || title.endsWith(" Hack)") || title.contains("(Hack ")) {
                return "hak";
            } else if (title.contains("(PD)")) {
                return "pd";
            } else if ("abcdefghijklmnopqrstuvwxyz".contains(title.substring(0, 1).toLowerCase())) {
                return title.substring(0, 1).toLowerCase();
            } else {
                // todo russian
                return "num";
            }
        } else {
            throw new RuntimeException("Blank game title!");
        }
    }

    // Транслитерация по правилам международных телеграмм
    private static final List<Character> RUS = Arrays.asList(
            'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й',
            'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф',
            'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я');

    private static final List<String> LAT = Arrays.asList(
            "a", "b", "v", "g", "d", "e", "e", "j", "z", "i", "i",
            "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f",
            "h", "c", "ch", "sh", "sc", "", "y", "", "e", "iu", "ia");

    private static final Map<Integer, String> TRANSLIT_MAP = new HashMap<>();

    static {
        for (int i = 0; i < RUS.size(); i++) {
            TRANSLIT_MAP.put((int) RUS.get(i), LAT.get(i));
            TRANSLIT_MAP.put((int) RUS.get(i).toString().toUpperCase().charAt(0), LAT.get(i).toUpperCase());
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
        return text.chars().mapToObj(c -> {
            String replace = TRANSLIT_MAP.get(c);
            return (replace == null) ? Character.valueOf((char) c).toString() : replace;
        }).collect(Collectors.joining());
    }

    public static String choosePluralMerge(long number, String caseOne, String caseTwo, String caseFive) {
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

    public static Declension getDeclension(String term) {
        return declensionMap.getOrDefault(term.toLowerCase(), new Declension(term, term, term, term, term, term));
    }

    public static Map<Type, TypeTranslation> typeTranslationMap = new HashMap<>();

    public static String PR = "# ";

    static {
        typeTranslationMap.put(BOOK, new TypeTranslation(BOOK.getValue() + "s", "", "", PR + "Упоминания %s в других книгах", "")); // нужно только для генерации коллекции

        typeTranslationMap.put(DOC, new TypeTranslation(DOC.getValue() + "s", "Documentation for", "Документация для", PR + "Документация для", "")); // род
        typeTranslationMap.put(EMULATOR, new TypeTranslation(EMULATOR.getValue() + "s", "Emulators descriptions for", "Описания эмуляторов", PR + "Описания эмуляторов", "")); // род
        typeTranslationMap.put(GUIDE, new TypeTranslation(GUIDE.getValue() + "s", "Solutions for", "Прохождения, солюшены игр для", PR + "Описания и прохождения игр", "")); // род
        typeTranslationMap.put(MANUAL, new TypeTranslation(MANUAL.getValue() + "s", "Manuals for", "Мануалы, учебники для", PR + "Мануалы для", "")); // род

        typeTranslationMap.put(COMICS, new TypeTranslation(COMICS.getValue(), "Comics for", "Комиксы для", PR + "Комиксы и манга по мотивам игр %s", "<p>Мы собрали небольшую коллекцию комиксов, связанных с %s.</p>"));
        typeTranslationMap.put(MAGAZINE, new TypeTranslation(MAGAZINE.getValue() + "s", "Magazines for", "Периодические издания для", PR + "Упоминания %s в периодических изданиях", "<p>Игры для %s так же рассмотрены в следующих периодических и сериальных изданиях:</p>"));
    }

    public static Map<Type, PlatformsTranslation> platformsTranslationMap = new HashMap<>();

    static {
        platformsTranslationMap.put(BOOK, new PlatformsTranslation("Книга", "<p>В книге представлены описания игр для %s.</p>",
                "<p>Так же здесь можно найти описания для %s</p>", "Книга %s с описаниями для %s", "описания, прохождения, пароли, секреты, cheats, walkthrough"));
        platformsTranslationMap.put(MAGAZINE, new PlatformsTranslation("Журнал", "<p>Описания игр для %s.</p>",
                "<p>Так же здесь можно найти описания для %s</p>", "Периодическое издание %s с описаниями для %s", "описания, прохождения, пароли, секреты, cheats, walkthrough"));
        platformsTranslationMap.put(MANUAL, new PlatformsTranslation("Сервисный мануал", "<p>Этот мануал охватывает платформы: %s.</p>",
                "<p>Так же здесь можно найти информацию о %s</p>", "Мануал %s с описаниями для %s", "описание, устройство, эксплуатация, управление"));
        platformsTranslationMap.put(GUIDE, new PlatformsTranslation("Мануал", "<p>Этот мануал охватывает платформы: %s</p>",
                "<p>Так же здесь можно найти информацию о %s</p>", "Мануал %s с описаниями для %s", "описания, прохождения, пароли, секреты, cheats, walkthrough"));

        platformsTranslationMap.put(DOC, new PlatformsTranslation("Документация", "<p>%s</p>", "<p>%s</p>", "%s %s", ""));
        platformsTranslationMap.put(EMULATOR, new PlatformsTranslation("Эмулятор", "<p>%s</p>", "<p>%s</p>", "%s %s", ""));
        platformsTranslationMap.put(COMICS, new PlatformsTranslation("Комикс", "<p>%s</p>", "<p>%s</p>", "%s %s", "комикс, comics"));
    }


    //TODO plural function https://www.irlc.msu.ru/irlc_projects/speak-russian/time_new/rus/grammar/

    public static String plural(String word, int count) {
        if (word.endsWith("сь") || word.endsWith("бь") || word.endsWith("дь") || word.endsWith("рь")) {
            return pluraljm(word, count);
        } else if (word.endsWith("а")) {
            return pluralj(word, count);
        } else {
            return pluralm(word, count);
        }
    }

    //                                       1   2-4  6...11,...
    private static final String[] RULE_J = {"a", "и", ""};
    private static final String[] RULE_JM = {"ь", "и", "ей"};
    private static final String[] RULE_M = {"", "а", "ов"};

    private static String pluralj(String word, int count) { // книга
        return plural(word.substring(0, word.length() - 1), RULE_J, count);
    }

    private static String pluraljm(String word, int count) { // запись
        return plural(word.substring(0, word.length() - 1), RULE_JM, count);
    }

    private static String pluralm(String word, int count) { // журнал
        return plural(word, RULE_M, count);
    }

    private static String plural(String word, String[] rule, int count) {
        if (count >= 11 & count <= 19) {
            return word + rule[2];
        }
        switch (count % 10) {
            case 1:
                return word + rule[0];
            case 2:
            case 3:
            case 4:
                return word + rule[1];
            default:
                return word + rule[2];
        }
    }

    // Просто множественное число

    public static String pluralWords(String word) {
        return Arrays.stream(word.split(" ")).map(StringUtils::plural).collect(Collectors.joining(" "));
    }

    public static String plural(String word) {
        if (word.endsWith("сь") || word.endsWith("бь") || word.endsWith("дь") || word.endsWith("рь") || word.endsWith("а") || word.endsWith("я")) {
            return word.substring(0, word.length() - 1) + "и"; // запись, книга, документация
        } else if (word.endsWith("о")) {
            return word.substring(0, word.length() - 1) + "а"; // окно
        } else if (word.endsWith("ый")) {
            return word.substring(0, word.length() - 1) + "е"; // сервисный
        } else {
            return word + "ы"; // журнал
        }
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    // JDK 11: "a".repeat(N);
    public static String repeat(char chr, int count) {
        char[] charArray = new char[count];
        for (int i = 0; i < count; i++) {
            charArray[i] = chr;
        }
        return new String(charArray);
    }
}

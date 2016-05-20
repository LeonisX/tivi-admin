package md.leonis.tivi.admin.utils;

import java.util.ArrayList;
import java.util.List;

public class CheckUtils {
    private List<String> notes = new ArrayList<>();

    public CheckUtils() {
        notes.clear();
    }

    public void checkLength(String text, int length) {
        if (text.length() > length) notes.add("Длина строки `" + text + "` больше " + length);
    }

    public void checkCpu(String text) {
        if (!text.matches("^[a-zA-Z0-9_]*$")) notes.add("В строке `" + text + "` используются запрещённые символы" );
    }

    public void checkAge(String text) {
        if (!text.isEmpty() && !text.chars().allMatch( Character::isDigit )) notes.add("Возраст следует указывать цифрами" );
    }

    public void checkNumber(String text) {
        if (text.isEmpty() || !text.chars().allMatch( Character::isDigit )) notes.add("`" + text + "`" + " не является числом" );
    }

    public boolean isOk() {
        return notes.isEmpty();
    }

    public String getErrors() {
        String errorString = "";
        for (String note: notes) {
            errorString += note + "\n\n";
        }
        return errorString;
    }
}

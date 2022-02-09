package md.leonis.tivi.admin.utils;

import md.leonis.tivi.admin.model.Actions;

import java.util.ArrayList;
import java.util.List;

public class CheckUtils {

    private final List<String> notes = new ArrayList<>();

    public CheckUtils() {
        notes.clear();
    }

    public void checkLength(String text, int length) {
        if (text.length() > length) notes.add("Длина строки `" + text + "` больше " + length);
    }

    public void checkCpu(String text) {
        if (!text.matches("^[a-zA-Z0-9_]*$")) notes.add("В строке `" + text + "` используются запрещённые символы" );
    }

    public void checkCpuExist(String text) {
        if (VideoUtils.action != Actions.EDIT)
            if (VideoUtils.checkCpuExist(text)) notes.add("CPU `" + text + "` уже используется" );
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
        StringBuilder errorString = new StringBuilder();
        for (String note: notes) {
            errorString.append(note).append("\n\n");
        }
        return errorString.toString();
    }
}

package md.leonis.tivi.admin.model;

import java.util.Arrays;

public enum  Type {

    BOOK("book"),
    MAGAZINE("magazine"),
    COMICS("comics"),
    GUIDE("guide"),
    MANUAL("manual"),
    DOC("doc"),
    EMULATOR("emulator");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Type fromString(String type) {
        return Arrays.stream(Type.values()).filter(t -> t.getValue().equals(type)).findFirst().orElseThrow(() -> new RuntimeException("Wrong type: " + type));
    }

    @Override
    public String toString() {
        return value;
    }
}

package md.leonis.tivi.admin.model.danneo;

public enum Order {

    ASC("asc"), DESC("desc");

    private final String value;

    Order(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

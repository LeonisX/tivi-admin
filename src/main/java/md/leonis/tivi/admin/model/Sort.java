package md.leonis.tivi.admin.model;

public enum Sort {

    PUBLIC("public"), ID("newsid"), TITLE("title"), RATING("hits");
    private String value;

    Sort(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
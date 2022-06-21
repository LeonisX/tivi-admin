package md.leonis.tivi.admin.model.template;

public class PlatformItem {

    private int count;
    private String book;
    private String title;
    private String uri;

    public PlatformItem(int count, String book, String title, String uri) {
        this.count = count;
        this.book = book;
        this.title = title;
        this.uri = uri;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}

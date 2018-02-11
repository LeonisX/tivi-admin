package md.leonis.tivi.admin.model.media.links;

public class LanguageLink {

    private Long id;
    private Long book;
    private Long langCode;
    private Long itemOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBook() {
        return book;
    }

    public void setBook(Long book) {
        this.book = book;
    }

    public Long getLangCode() {
        return langCode;
    }

    public void setLangCode(Long langCode) {
        this.langCode = langCode;
    }

    public Long getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(Long itemOrder) {
        this.itemOrder = itemOrder;
    }
}

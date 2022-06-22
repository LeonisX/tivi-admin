package md.leonis.tivi.admin.model.danneo;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BookCategory {
    private Integer catid;
    private Integer parentid = 0;
    private String catcpu;
    private String catname;
    private String catdesc;
    private Integer posit = 0;
    private String icon;
    private Access access = Access.all;
    private String sort = "downid";
    private String ord = Order.ASC.getValue();
    private YesNo rss = YesNo.yes;
    private Integer total = 0;

    public BookCategory(BookCategory other) {
        this.catid = other.catid;
        this.parentid = other.parentid;
        this.catcpu = other.catcpu;
        this.catname = other.catname;
        this.catdesc = other.catdesc;
        this.posit = other.posit;
        this.icon = other.icon;
        this.access = other.access;
        this.sort = other.sort;
        this.ord = other.ord;
        this.rss = other.rss;
        this.total = other.total;
    }
}

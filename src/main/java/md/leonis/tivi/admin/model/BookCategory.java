package md.leonis.tivi.admin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
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
    private Order ord = Order.asc;
    private YesNo rss = YesNo.yes;
    private Integer total = 0;
}

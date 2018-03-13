package md.leonis.tivi.admin.model;

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
}

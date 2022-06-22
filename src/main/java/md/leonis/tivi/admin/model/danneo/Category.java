package md.leonis.tivi.admin.model.danneo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Category {

    private Integer catid;
    private Integer parentid = 0;
    private String catcpu;
    private String catname;
    private String catdesc;
    private Integer posit = 0;
    private String icon;
    private Access access = Access.all;
    private String sort = "newsid";
    private String ord = Order.ASC.getValue();
    private YesNo rss = YesNo.yes;
    private Integer total = 0;
}

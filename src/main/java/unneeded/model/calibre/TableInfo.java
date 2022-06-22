package unneeded.model.calibre;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableInfo {

    private Integer cid;
    private String name;
    private String type;
    private Boolean notnull;
    private String dflt_value;
    private Boolean pk;
}

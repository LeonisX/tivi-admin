package md.leonis.tivi.admin.model.calibre;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
public class Identifier {

    private Long id;
    private Long book;
    private String type;
    private String val;

}

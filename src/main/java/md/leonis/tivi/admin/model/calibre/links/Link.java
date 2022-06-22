package md.leonis.tivi.admin.model.calibre.links;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Link {

    private Long id;
    private Long book;
    private String value;

    public Long getLongValue() {
        return Long.valueOf(value);
    }

}

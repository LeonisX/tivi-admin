package md.leonis.tivi.admin.model.calibre;

import lombok.*;

@Getter
@Setter
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
public class Own {

    private Long id;
    private Long book;
    private Boolean value;

}

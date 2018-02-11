package md.leonis.tivi.admin.model.media;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
public class Comment {

    private Long id;
    private Long book;
    private String text;

}

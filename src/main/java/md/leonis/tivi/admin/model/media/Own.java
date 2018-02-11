package md.leonis.tivi.admin.model.media;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString(exclude = "book")
@EqualsAndHashCode(exclude = "book")
public class Own {

    private Long id;
    private Long book;
    private Boolean value;

}

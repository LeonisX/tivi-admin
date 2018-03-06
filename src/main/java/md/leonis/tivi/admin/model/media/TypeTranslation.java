package md.leonis.tivi.admin.model.media;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TypeTranslation {

    private String plural;
    private String imageTitle;
    private String imageAlt;
    private String shortText;
    private String text;
}

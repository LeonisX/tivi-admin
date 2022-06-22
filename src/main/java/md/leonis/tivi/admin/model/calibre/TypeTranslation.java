package md.leonis.tivi.admin.model.calibre;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TypeTranslation {

    private final String plural;
    private final String imageTitle;
    private final String imageAlt;
    private final String shortText;
    private final String text;
}

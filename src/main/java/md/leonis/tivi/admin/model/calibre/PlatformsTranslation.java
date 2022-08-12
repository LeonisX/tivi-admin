package md.leonis.tivi.admin.model.calibre;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlatformsTranslation {

    private final String name;
    private final String platforms;
    private final String altPlatforms;
    private final String description;
    private final String keywords;

    //TODO keywords here
}

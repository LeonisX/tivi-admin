package md.leonis.tivi.admin.model.media.links;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LanguageLink {

    private Long id;
    private Long book;
    @SerializedName("lang_code")
    private Long code;
    @SerializedName("item_order")
    private Long itemOrder;

}

package md.leonis.tivi.admin.model.media;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Language {

    private Long id;
    @SerializedName("lang_code")
    private String code;

}

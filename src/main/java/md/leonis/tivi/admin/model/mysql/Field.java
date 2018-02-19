package md.leonis.tivi.admin.model.mysql;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Field {

    @SerializedName("Field")
    private String field;

    @SerializedName("Type")
    private String type;

    @SerializedName("Null")
    private String isNull;

    @SerializedName("Key")
    private String key;

    @SerializedName("Default")
    private String defaultValue;

    @SerializedName("Extra")
    private String extra;
}

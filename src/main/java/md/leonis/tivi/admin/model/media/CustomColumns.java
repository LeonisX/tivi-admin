package md.leonis.tivi.admin.model.media;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class CustomColumns {

    private Long id;
    private String value;
    private String label;
    private String name;
    @SerializedName("datatype")
    private String dataType;
    @SerializedName("mark_for_delete")
    private Boolean markForDelete;
    private Boolean editable;
    private String display;
    @SerializedName("is_multiple")
    private Boolean isMultiple;
    private Boolean normalized;

}

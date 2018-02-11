package md.leonis.tivi.admin.model.media;

import lombok.Data;

@Data
public class CustomColumns {

    private Long id;
    private String value;
    private String label;
    private String name;
    private String dataType;
    private Boolean markForDelete;
    private Boolean editable;
    private String display;
    private Boolean isMultiple;
    private Boolean normalized;

}

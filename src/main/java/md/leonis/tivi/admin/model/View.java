package md.leonis.tivi.admin.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class View {

    public final String title;
    public final String leftValue;
    public final String rightValue;

    public View(String title) {
        this.title = title;
        this.leftValue = "";
        this.rightValue = "";
    }
}

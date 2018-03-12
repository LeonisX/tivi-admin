package md.leonis.tivi.admin.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Declension {

    private final String im; // КТО? ЧТО?
    private final String rod; // КОГО? ЧЕГО?
    private final String dat; // КОМУ? ЧЕМУ?
    private final String vin; // КОГО? ЧТО?
    private final String tv; // КЕМ? ЧЕМ?
    private final String pred; // О КОМ? О ЧЕМ?

}

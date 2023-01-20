package md.leonis.tivi.admin.model.template;

import com.google.gson.JsonObject;
import md.leonis.tivi.admin.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

public class ModelBookTest {

    @Test
    public void test(){
        System.out.println(((JsonObject) JsonUtils.gson.toJsonTree(new ModelBook())).keySet().stream().map(k -> String.format("\"%s\"", k)).collect(Collectors.joining(", ")));
    }
}
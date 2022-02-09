package md.leonis.tivi.admin.utils;

import com.google.gson.reflect.TypeToken;
import md.leonis.tivi.admin.model.mysql.Field;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.BookUtils.queryRequest;

public class ColumnsResolver {

    private final List<String> numericColumns;
    private final List<String> floatColumns;
    private final List<String> blobColumns;

    public ColumnsResolver(String tableName) {
        String result = queryRequest(String.format("SHOW COLUMNS FROM `%s`", tableName));
        Type fieldType = new TypeToken<List<Field>>() {}.getType();
        List<Field> fields = JsonUtils.gson.fromJson(result, fieldType);
        numericColumns = fields.stream().filter(t -> t.getType().matches("^(\\w*int.*)")).map(Field::getField).collect(toList());
        floatColumns = fields.stream().filter(t -> t.getType().matches("^(float.*|real.*|double.*|dec.*|fixed.*|decimal.*|numeric.*)")).map(Field::getField).collect(toList());
        blobColumns = fields.stream().filter(t -> t.getType().matches("^(\\w*blob.*)")).map(Field::getField).collect(toList());
    }

    public String resolve(Map.Entry<String, Object> field) {
        if (floatColumns.contains(field.getKey())) {
            if (field.getValue() == null) {
                return "NULL";
            }
            String v = field.getValue().toString();
            if (v.isEmpty()) {
                return "NULL";
            }
            Double value = Double.valueOf(v);
            if (value.equals((double) value.longValue())) {
                return "'" + value.longValue() + "'";
            } else {
                return "'" + value + "'";
            }
        } else if (numericColumns.contains(field.getKey())) {
            if (field.getValue() == null) {
                return "NULL";
            }
            String v = field.getValue().toString();
            Double value = v.isEmpty() ? null : Double.valueOf(v);
            if (value == null) {
                return "NULL";
            }
            return Long.valueOf(value.longValue()).toString();
        } else if (blobColumns.contains(field.getKey())) {
            if (field.getValue() == null) {
                return "NULL";
            }
            return "'" + field.getValue().toString().replace("\"", "\\\"") + "'";
        } else {
            if (field.getValue() == null) {
                return "NULL";
            }
            return "'" + field.getValue().toString()
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("'", "\\'")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("" + ((char) 0), "\\0")
                    + "'";
        }
    }
}

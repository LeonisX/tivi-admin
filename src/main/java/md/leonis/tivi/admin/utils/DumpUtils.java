package md.leonis.tivi.admin.utils;

import com.google.gson.reflect.TypeToken;
import md.leonis.tivi.admin.model.mysql.Field;
import md.leonis.tivi.admin.model.mysql.TableStatus;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static md.leonis.tivi.admin.utils.BookUtils.queryRequest;
import static md.leonis.tivi.admin.utils.BookUtils.rawQueryRequest;

public class DumpUtils {

    // Fixed charset
    public static void dumpDB0() {
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream("E:\\dump.txt"), StandardCharsets.UTF_8))) {
            //queryRequest("SHOW COLUMNS FROM `danny_media`");
            //queryRequest("SHOW TABLE STATUS");
            doDump0(out);
            //out.println(rawQueryRequest("SHOW CREATE TABLE `danny_media`"));
            queryRequest("SHOW TABLES");
            queryRequest("SHOW TABLE STATUS");
            queryRequest("SHOW CREATE TABLE `danny_media`");
            queryRequest("SHOW COLUMNS FROM `danny_media`");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //TODO какие-то проверки
    }

    // AUtomatic change charset
    public static void dumpDB() {
        try (FileOutputStream fos = new FileOutputStream("E:\\dump.txt")) {
            //queryRequest("SHOW COLUMNS FROM `danny_media`");
            //queryRequest("SHOW TABLE STATUS");
            doDump(fos);
            //out.println(rawQueryRequest("SHOW CREATE TABLE `danny_media`"));
            queryRequest("SHOW TABLES");
            queryRequest("SHOW TABLE STATUS");
            queryRequest("SHOW CREATE TABLE `danny_media`");
            queryRequest("SHOW COLUMNS FROM `danny_media`");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO какие-то проверки
    }

    private static void doDump(FileOutputStream fos) throws IOException {
        Type type = new TypeToken<List<TableStatus>>() {
        }.getType();
        List<TableStatus> tableStatuses = JsonUtils.gson.fromJson(queryRequest("SHOW TABLE STATUS"), type);


        for (TableStatus table : tableStatuses) {
            //TODO filter if need
            if (!table.getName().startsWith("vv_social")) {
                continue;
            }
            if (!table.getName().startsWith("vv_")) {
                continue;
            }

            // TODO Выставляем кодировку соединения соответствующую кодировке таблицы
            // Создание таблицы
            String charset = table.getCollation().split("_")[0];
            // Fix bad charset ;)
            if (table.getName().startsWith("danny_")) {
                charset = "cp1251";
            }
            String result = rawQueryRequest(String.format("SHOW CREATE TABLE `%s`", table.getName()));
            result = result.replaceAll("(?i)(DEFAULT CHARSET=\\w+|COLLATE=\\w+)", "/*!40101 $1 */;");
            result = result.replaceAll("(?i)(default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP|collate \\w+|character set \\w+)", "/*!40101 $1 */");
            fos.write(String.format("DROP TABLE IF EXISTS `%s`;\n", table.getName()).getBytes(charset));
            fos.write((result + "\n\n").getBytes(charset));
            // Опредеделяем типы столбцов
            result = queryRequest(String.format("SHOW COLUMNS FROM `%s`", table.getName()));
            Type fieldType = new TypeToken<List<Field>>() {
            }.getType();
            List<Field> fields = JsonUtils.gson.fromJson(result, fieldType);
            //TODO other numeric https://dev.mysql.com/doc/refman/5.7/en/numeric-types.html
            List<String> numericColumns = fields.stream().filter(t -> t.getType().matches("^(\\w*int.*)")).map(Field::getField).collect(toList());
            List<String> blobColumns = fields.stream().filter(t -> t.getType().matches("^(\\w*blob.*)")).map(Field::getField).collect(toList());

            int from = 0;
            int LIMIT = 1;
            //TODO
            //long limit = 1 + LIMIT * 1048576 / table.getAvgRowLength() + 1;
            long limit = 400;
            //System.out.println(limit);
            int count = 0;
            //$limit2 = round($limit / 3);

            int i = 0;
            boolean isFirst = true;
            do {
                String query = String.format("SELECT * FROM `%s` LIMIT %d, %d", table.getName(), from, limit);
                result = queryRequest(query);
                //System.out.println(result);
                type = new TypeToken<List<Map<String, Object>>>() {
                }.getType();
                List<Map<String, Object>> rows = JsonUtils.gson.fromJson(result, type);
                //System.out.println(rows);

                for (Map<String, Object> row : rows) {
                    String values = row.entrySet().stream().map(field -> {
                        if (numericColumns.contains(field.getKey())) {
                            //$row[$k] = isset($row[$k]) ? $row[$k] : "NULL";
                            if (field.getValue() == null) { return "NULL"; }
                            String v = field.getValue().toString();
                            Long value = v.isEmpty() ? null : Long.valueOf(v);
                            if (value == null) {
                                return "NULL";
                            }
                            return value.toString();
                        } else if (blobColumns.contains(field.getKey())) {
                            if (field.getValue() == null) {
                                return "NULL";
                            }
                            return "'" + field.getValue().toString().replace("\"", "\\\"") + "'";
                        } else {
                            //TODO
                            //$row[$k] = isset($row[$k]) ? "'".mysql_escape_string($row[$k]). "'" :"NULL";
                            if (field.getValue() == null) { return "NULL"; }
                            return "'" + field.getValue().toString()
                                    .replace("\\", "\\\\")
                                    .replace("\"", "\\\"")
                                    .replace("'", "\\'")
                                    .replace("\r\n", "\\r\\n")
                                    .replace("\r\n", "\\r\\n")
                                    .replace("\n", "\\n")
                                    .replace("" + ((char) 0), "\\0")
                                    + "'";
                        }
                    }).collect(Collectors.joining(", "));

                    if (!isFirst) {
                        fos.write((",\n").getBytes(charset));
                    } else {
                        fos.write(String.format("INSERT INTO `%s` VALUES\n", table.getName()).getBytes(charset));
                    }
                    fos.write(("(" + values + ")").getBytes(charset));
                    isFirst = false;
                }
                from += limit;
                count = rows.size();
            } while (count > 0);
            if (!isFirst) {
                fos.write((";\n\n").getBytes(charset));
            }
        }
    }

    private static void doDump0(PrintWriter out) {
        Type type = new TypeToken<List<TableStatus>>() {
        }.getType();
        List<TableStatus> tableStatuses = JsonUtils.gson.fromJson(queryRequest("SHOW TABLE STATUS"), type);

        //TODO filter
        for (TableStatus table : tableStatuses) {
            if (!table.getName().startsWith("vv_")) {
                continue;
            }
            // TODO Выставляем кодировку соединения соответствующую кодировке таблицы
            // Создание таблицы
            String result = rawQueryRequest(String.format("SHOW CREATE TABLE `%s`", table.getName()));
            result = result.replaceAll("(?i)(DEFAULT CHARSET=\\w+|COLLATE=\\w+)", "/*!40101 $1 */;");
            result = result.replaceAll("(?i)(default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP|collate \\w+|character set \\w+)", "/*!40101 $1 */");
            out.println(String.format("DROP TABLE IF EXISTS `%s`;", table.getName()));
            out.println(result);
            out.println();
            // Опредеделяем типы столбцов
            result = queryRequest(String.format("SHOW COLUMNS FROM `%s`", table.getName()));
            Type fieldType = new TypeToken<List<Field>>() {
            }.getType();
            List<Field> fields = JsonUtils.gson.fromJson(result, fieldType);
            //TODO other numeric https://dev.mysql.com/doc/refman/5.7/en/numeric-types.html
            List<String> numericColumns = fields.stream().filter(t -> t.getType().matches("^(\\w*int.*)")).map(Field::getField).collect(toList());

            int from = 0;
            int LIMIT = 1;
            //TODO
            //long limit = 1 + LIMIT * 1048576 / table.getAvgRowLength() + 1;
            long limit = 400;
            //System.out.println(limit);
            int count = 0;
            //$limit2 = round($limit / 3);

            int i = 0;
            boolean isFirst = true;
            do {
                String query = String.format("SELECT * FROM `%s` LIMIT %d, %d", table.getName(), from, limit);
                result = queryRequest(query);
                //System.out.println(result);
                type = new TypeToken<List<Map<String, Object>>>() {
                }.getType();
                List<Map<String, Object>> rows = JsonUtils.gson.fromJson(result, type);
                //System.out.println(rows);

                for (Map<String, Object> row : rows) {
                    String values = row.entrySet().stream().map(field -> {
                        if (numericColumns.contains(field.getKey())) {
                            //$row[$k] = isset($row[$k]) ? $row[$k] : "NULL";
                            String v = field.getValue().toString();
                            Long value = v.isEmpty() ? null : Long.valueOf(v);
                            if (value == null) {
                                return "NULL";
                            }
                            return value.toString();
                        } else {
                            //TODO BLOB

                            if (field.getValue() == null) { return "NULL"; }
                            return "'" + field.getValue().toString()
                                    .replace("\\", "\\\\")
                                    .replace("\"", "\\\"")
                                    .replace("'", "\\'")
                                    .replace("\r\n", "\\r\\n")
                                    .replace("\r\n", "\\r\\n")
                                    .replace("\n", "\\n")
                                    + "'";
                        }
                    }).collect(Collectors.joining(", "));

                    if (!isFirst) {
                        out.println(",");
                    } else {
                        out.println(String.format("INSERT INTO `%s` VALUES", table.getName()));
                    }
                    out.print("(" + values + ")");
                    isFirst = false;
                }
                from += limit;
                count = rows.size();
            } while (count > 0);
            if (!isFirst) {
                out.println(";\n");
            }
        }
    }
}

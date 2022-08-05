package md.leonis.tivi.admin.renderer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import md.leonis.tivi.admin.model.Type;
import md.leonis.tivi.admin.model.calibre.CalibreBook;
import md.leonis.tivi.admin.model.template.ModelBook;
import md.leonis.tivi.admin.utils.Config;
import md.leonis.tivi.admin.utils.JsonUtils;
import md.leonis.tivi.admin.utils.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static md.leonis.tivi.admin.utils.StringUtils.platformsTranslationMap;

public class CatalogsRenderer {

    private final List<CalibreBook> calibreBooks;

    private static final List<String> fields = Arrays.asList("id", "own", /*"type", */"title", "sort", "officialTitle", "cpu", "series", "serieIndex", /*"hasCover",*/ /*"lastModified",*/
            /*"textShort", "textMore", "comment", "releaseNote",*/ "publisher", "company","signedInPrint",  /*"rating", */
            "tags", "altTags", "isbn", "bbk", "udk", "edition", "format", "pages",
            "fileName", "externalLink", "authors", "languages", "scannedBy", "postprocessing", "source", "tiviId"
    );

    public CatalogsRenderer(List<CalibreBook> calibreBooks) {
        this.calibreBooks = calibreBooks;
    }

    public void generateXlsx() {
        XSSFWorkbook workbook = new XSSFWorkbook();

        Arrays.stream(Type.values()).forEach(type -> generateSheet(workbook, type));

        try {
            workbook.write(Files.newOutputStream(getReportPath()));
            workbook.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void generateSheet(XSSFWorkbook workbook, Type type) {
        //System.out.println(type);
        Sheet sheet = workbook.createSheet(StringUtils.pluralWords(platformsTranslationMap.get(type).getName()));
        //sheet.setColumnWidth(0, 6000);
        //sheet.setColumnWidth(1, 4000);

        Row header = sheet.createRow(0);

        /*CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);*/

        for (int x = 0; x < fields.size(); x++) {
            Cell headerCell = header.createCell(x);
            headerCell.setCellValue(fields.get(x));
            //headerCell.setCellStyle(headerStyle);
        }

        List<ModelBook> books = calibreBooks.stream().filter(b -> b.getType().equals(type)).map(ModelBook::new).collect(Collectors.toList());

        for (int y = 0; y < books.size(); y++) {
            JsonObject jsonObject = ((JsonObject) JsonUtils.gson.toJsonTree(books.get(y)));
            Row row = sheet.createRow(y + 1);

            for (int x = 0; x < fields.size(); x++) {
                JsonElement element = jsonObject.get(fields.get(x));
                String value = element.isJsonNull() ? "" : element.getAsString();

                //CellStyle style = workbook.createCellStyle();
                //style.setWrapText(false);

                Cell cell = row.createCell(x);
                cell.setCellValue(value);
                //cell.setCellStyle(style);
            }
        }

        for (int x = 0; x < fields.size(); x++) {
            sheet.autoSizeColumn(x);
        }

        sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, fields.size()));
        sheet.createFreezePane(0, 1);
    }

    public Path getReportPath() {
        return Paths.get(Config.outputPath).resolve("catalog.xlsx");
    }
}

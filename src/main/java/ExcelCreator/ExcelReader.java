package ExcelCreator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.util.*;

public class ExcelReader {
    private final AppConfig config;
    public ExcelReader(AppConfig config) { this.config = config; }

    public List<Map<String, Object>> readStatusFile(String filePath) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(MasterData.STATUS_SHEET);
            if (sheet == null) return rows;

            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) headers.add(cell.getStringCellValue().trim());

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Map<String, Object> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    rowData.put(headers.get(j), getCellValue(row.getCell(j), evaluator));
                }
                Date ctrl = ExcelUtils.extractDate(rowData.get(MasterData.COL_CONTROL_DATE));
                Date cre = ExcelUtils.extractDate(rowData.get(MasterData.COL_CREATED));
                if (isWithinRange(ctrl) || isWithinRange(cre)) rows.add(rowData);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return rows;
    }

    public List<String> readSnapshotTickets(String filePath) {
        List<String> tickets = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(config.snapshotSheetName);
            if (sheet == null) return tickets;

            int colIndex = -1;
            Row header = sheet.getRow(0);
            for(Cell c : header) {
                if(c.getStringCellValue().trim().equals(MasterData.COL_FUTURENOW_TICKET)) {
                    colIndex = c.getColumnIndex();
                    break;
                }
            }

            if (colIndex == -1) return tickets;

            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                if (r != null) {
                    String val = formatter.formatCellValue(r.getCell(colIndex)).trim();
                    if (!val.isEmpty()) tickets.add(val);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return tickets;
    }

    private boolean isWithinRange(Date d) {
        return d != null && !d.before(config.startDate) && !d.after(config.endDate);
    }

    private Object getCellValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
            case BOOLEAN: return cell.getBooleanCellValue();
            case FORMULA:
                CellValue cv = evaluator.evaluate(cell);
                if (cv.getCellType() == CellType.NUMERIC) return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cv.getNumberValue();
                return cv.getStringValue();
            default: return null;
        }
    }
}
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
                rows.add(rowData);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return rows;
    }

    public Map<String, Incident> readSnapshotData(String filePath) {
        Map<String, Incident> snapshotMap = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(config.snapshotSheetName);
            if (sheet == null) return snapshotMap;
            Row header = sheet.getRow(0);
            Map<String, Integer> colMap = new HashMap<>();
            for (Cell c : header) colMap.put(c.getStringCellValue().trim(), c.getColumnIndex());
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                if (r == null) continue;
                Incident inc = new Incident();
                inc.id = ExcelUtils.canonical(getCellValue(r.getCell(colMap.getOrDefault(MasterData.HEADER_ID, -1)), evaluator));
                if (inc.id == null || inc.id.isEmpty()) continue;
                inc.status = ExcelUtils.canonical(getCellValue(r.getCell(colMap.getOrDefault(MasterData.HEADER_STATUS, -1)), evaluator));
                inc.lastChangedOn = getCellValue(r.getCell(colMap.getOrDefault(MasterData.HEADER_LAST_CHANGED_ON, -1)), evaluator);
                snapshotMap.put(inc.id, inc);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return snapshotMap;
    }

    private Object getCellValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cell.getNumericCellValue();
            default: return null;
        }
    }
}
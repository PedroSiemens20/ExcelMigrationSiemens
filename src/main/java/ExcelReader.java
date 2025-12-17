
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelReader {
    private final String statusFile;
    private final String snapshotsFile;
    private final String statusSheet;
    private final String snapshotSheet;

    public ExcelReader(String statusFile, String snapshotsFile, String statusSheet, String snapshotSheet) {
        this.statusFile = statusFile;
        this.snapshotsFile = snapshotsFile;
        this.statusSheet = statusSheet;
        this.snapshotSheet = snapshotSheet;
    }

    public List<Map<String, Object>> readStatusFile() {
        List<Map<String, Object>> statusData = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(statusFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(statusSheet);
            if (sheet == null) throw new RuntimeException("Sheet " + statusSheet + " not found!");

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new RuntimeException("Header row not found!");

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            int controlDateIndex = headers.indexOf(MasterData.COL_CONTROL_DATE);
            int createdIndex     = headers.indexOf(MasterData.COL_CREATED);
            if (controlDateIndex == -1) throw new RuntimeException("Column '" + MasterData.COL_CONTROL_DATE + "' not found!");
            if (createdIndex     == -1) throw new RuntimeException("Column '" + MasterData.COL_CREATED + "' not found!");

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter formatter    = new DataFormatter(Locale.getDefault());
            SimpleDateFormat sdf       = new SimpleDateFormat(MasterData.DATE_PATTERN_DISPLAY);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Control Date
                Date controlDate = null;
                Cell controlCell = row.getCell(controlDateIndex);
                if (controlCell != null) {
                    if (controlCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(controlCell)) {
                        controlDate = controlCell.getDateCellValue();
                    } else {
                        String formatted = formatter.formatCellValue(controlCell, evaluator).trim();
                        try { controlDate = sdf.parse(formatted); } catch (Exception ignored) {}
                    }
                }

                // Created
                Date createdDate = null;
                Cell createdCell = row.getCell(createdIndex);
                if (createdCell != null) {
                    if (createdCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(createdCell)) {
                        createdDate = createdCell.getDateCellValue();
                    } else {
                        String formatted = formatter.formatCellValue(createdCell, evaluator).trim();
                        try { createdDate = sdf.parse(formatted); } catch (Exception ignored) {}
                    }
                }

                boolean includeRow =
                        (controlDate != null && !controlDate.before(MasterData.START_DATE) && !controlDate.after(MasterData.END_DATE)) ||
                                (createdDate != null && !createdDate.before(MasterData.START_DATE) && !createdDate.after(MasterData.END_DATE));

                if (includeRow) {
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        Object value = getCellValueTyped(cell, evaluator);
                        rowData.put(headers.get(j), value);
                    }
                    statusData.add(rowData);
                }
            }

            System.out.println("Filtered Status Data (Control Date OR Created between " + MasterData.START_DATE + " and " + MasterData.END_DATE + "):");
            for (Map<String, Object> row : statusData) {
                System.out.println(row);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return statusData;
    }

    public List<String> readSnapshotTickets() {
        List<String> snapshotTickets = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(snapshotsFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(snapshotSheet);
            if (sheet == null) throw new RuntimeException("Sheet " + snapshotSheet + " not found!");

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) throw new RuntimeException("Header row not found!");

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            int futureNowIndex = headers.indexOf(MasterData.COL_FUTURENOW_TICKET);
            if (futureNowIndex == -1) throw new RuntimeException("Column '" + MasterData.COL_FUTURENOW_TICKET + "' not found!");

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter formatter    = new DataFormatter(Locale.getDefault());

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell cell = row.getCell(futureNowIndex);
                if (cell != null) {
                    String displayed = formatter.formatCellValue(cell, evaluator).trim();
                    if (!displayed.isEmpty()) {
                        snapshotTickets.add(displayed);
                    }
                }
            }

            System.out.println("Snapshot Tickets read: " + snapshotTickets.size());
            System.out.println("Tickets: " + snapshotTickets);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return snapshotTickets;
    }

    private Object getCellValueTyped(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue(); // Date
                } else {
                    return cell.getNumericCellValue(); // Double
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                CellValue cv = evaluator.evaluate(cell);
                if (cv == null) return null;
                switch (cv.getCellType()) {
                    case STRING:  return cv.getStringValue();
                    case NUMERIC: return DateUtil.isCellDateFormatted(cell) ? cell.getDateCellValue() : cv.getNumberValue();
                    case BOOLEAN: return cv.getBooleanValue();
                    default: return null;
                }
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return null;
        }
    }
}

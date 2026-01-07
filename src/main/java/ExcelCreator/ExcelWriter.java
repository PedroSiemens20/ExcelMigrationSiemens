package ExcelCreator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class ExcelWriter {
    public void writeFull(String outputPath, List<Incident> migrated, List<Incident> newEntries, List<Incident> identical, List<Incident> bugs) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            createNormalSheet(workbook, "Migrated Data", migrated);
            createNormalSheet(workbook, "New Entries", newEntries);
            createNormalSheet(workbook, "Already Up-to-Date", identical);
            new BugProcessor().createBugTableSheet(workbook, bugs);

            try (FileOutputStream fos = new FileOutputStream(outputPath)) { workbook.write(fos); }
            Desktop.getDesktop().open(new File(outputPath));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void createNormalSheet(XSSFWorkbook wb, String name, List<Incident> data) {
        XSSFSheet sheet = wb.createSheet(name);
        Row header = sheet.createRow(0);
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont(); font.setBold(true); style.setFont(font);
        for (int i = 0; i < MasterData.OUTPUT_HEADERS.length; i++) {
            Cell c = header.createCell(i); c.setCellValue(MasterData.OUTPUT_HEADERS[i]); c.setCellStyle(style);
        }
        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(MasterData.DATE_PATTERN_DISPLAY));
        int rowIdx = 1;
        for (Incident inc : data) {
            Row row = sheet.createRow(rowIdx++);
            writeCell(row, 0, inc.id, null);
            writeCell(row, 1, inc.are, null);
            writeCell(row, 2, inc.createdOn, dateStyle);
            writeCell(row, 3, inc.reportedBy, null);
            writeCell(row, 4, inc.lastChangedOn, dateStyle);
            writeCell(row, 5, inc.priority, null);
            writeCell(row, 6, inc.status, null);
            writeCell(row, 7, inc.description, null);
        }
        for (int i = 0; i < MasterData.OUTPUT_HEADERS.length; i++) sheet.autoSizeColumn(i);
    }

    private void writeCell(Row row, int col, Object value, CellStyle dStyle) {
        Cell cell = row.createCell(col);
        if (value == null) return;
        if (value instanceof Date) {
            cell.setCellValue((Date) value); if (dStyle != null) cell.setCellStyle(dStyle);
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
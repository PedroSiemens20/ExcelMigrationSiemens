package ExcelCreator;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

public class ExcelWriter {
    public void writeWithIdentical(String outputPath, List<Incident> migrated, List<Incident> newEntries, List<Incident> identical) {
        try (Workbook workbook = new XSSFWorkbook()) {
            createSheet(workbook, "Migrated Data", migrated);
            createSheet(workbook, "New Entries", newEntries);
            createSheet(workbook, "Already Up-to-Date", identical); // Nova Sheet

            try (FileOutputStream fos = new FileOutputStream(outputPath)) { workbook.write(fos); }
            Desktop.getDesktop().open(new File(outputPath));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void createSheet(Workbook wb, String name, List<Incident> data) {
        Sheet sheet = wb.createSheet(name);
        Row header = sheet.createRow(0);
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont(); font.setBold(true);
        style.setFont(font);

        for (int i = 0; i < MasterData.OUTPUT_HEADERS.length; i++) {
            Cell c = header.createCell(i);
            c.setCellValue(MasterData.OUTPUT_HEADERS[i]);
            c.setCellStyle(style);
        }

        CellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat(MasterData.DATE_PATTERN_DISPLAY));

        int rowIdx = 1;
        for (Incident inc : data) {
            Row row = sheet.createRow(rowIdx++);
            writeCell(row, 0, inc.id, null);
            writeCell(row, 1, inc.futureNowTicket, null);
            writeCell(row, 3, inc.are, null);
            writeCell(row, 4, inc.createdOn, dateStyle);
            writeCell(row, 6, inc.reportedBy, null);
            writeCell(row, 8, inc.lastChangedOn, dateStyle);
            writeCell(row, 10, inc.priority, null);
            writeCell(row, 11, inc.status, null);
            writeCell(row, 12, inc.description, null);
        }
        for(int i=0; i<MasterData.OUTPUT_HEADERS.length; i++) sheet.autoSizeColumn(i);
    }

    private void writeCell(Row row, int col, Object value, CellStyle dStyle) {
        Cell cell = row.createCell(col);
        if (value == null) return;
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
            if (dStyle != null) cell.setCellStyle(dStyle);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
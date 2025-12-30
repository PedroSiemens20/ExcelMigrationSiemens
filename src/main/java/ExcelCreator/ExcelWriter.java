package ExcelCreator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExcelWriter {

    public void writeToExcel(String outputFile,
                             List<Map<String, Object>> migratedData,
                             List<Map<String, Object>> newEntries) {
        try (Workbook workbook = new XSSFWorkbook()) {

            createSheetWithData(workbook, "Migrated Data", migratedData);
            createSheetWithData(workbook, "New Entries", newEntries);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }
            System.out.println("Process completed. Output saved to " + outputFile);

            try {
                Desktop.getDesktop().open(new File(outputFile));
                System.out.println("Excel file opened successfully.");
            } catch (IOException e) {
                System.out.println("Could not open the Excel file automatically.");
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSheetWithData(Workbook workbook, String sheetName, List<Map<String, Object>> data) {
        Sheet sheet = workbook.createSheet(sheetName);

        // Header style
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < MasterData.OUTPUT_HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(MasterData.OUTPUT_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }

        if (data == null || data.isEmpty()) {
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("No data available");
            return;
        }

        // Date style (display format)
        CellStyle dateStyle = workbook.createCellStyle();
        short fmt = workbook.getCreationHelper().createDataFormat().getFormat(MasterData.DATE_PATTERN_DISPLAY);
        dateStyle.setDataFormat(fmt);

        // Optional: ARE mask (e.g., leading zeros)
        // CellStyle areNumberStyle = workbook.createCellStyle();
        // areNumberStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("00000"));

        for (int i = 0; i < data.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> rowData = data.get(i);

            for (int j = 0; j < MasterData.OUTPUT_HEADERS.length; j++) {
                String header = MasterData.OUTPUT_HEADERS[j];
                Object value = rowData.get(header);
                Cell cell = row.createCell(j);

                if (value == null) {
                    cell.setBlank();
                    continue;
                }

                if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                    // if (ExcelCreator.MasterData.HEADER_ARE.equals(header)) {
                    //     cell.setCellStyle(areNumberStyle);
                    // }
                } else if (value instanceof Boolean) {
                    cell.setCellValue((Boolean) value);
                } else if (value instanceof Date) {
                    cell.setCellValue((Date) value);
                    cell.setCellStyle(dateStyle);
                } else {
                    cell.setCellValue(value.toString());
                }
            }
        }

        for (int i = 0; i < MasterData.OUTPUT_HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}

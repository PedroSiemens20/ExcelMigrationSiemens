package ExcelSnapshotWriter;

import ExcelCreator.AppConfig;
import ExcelCreator.BugProcessor;
import ExcelCreator.Incident;
import ExcelCreator.MasterData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelSnapshotWriter {

    private final AppConfig config;

    public ExcelSnapshotWriter(AppConfig config) {
        this.config = config;
    }

    public void updateExistingSnapshot(String filePath, List<Incident> migrated, List<Incident> newEntries, List<Incident> bugsList) throws Exception {
        System.out.println("Updating original snapshot: " + filePath);

        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            // 1. Verificação de Segurança: A aba de Bugs existe?
            XSSFSheet bugSheet = workbook.getSheet(MasterData.SHEET_BUGS);
            if (bugSheet == null) {
                String error = "ERRO: A aba '" + MasterData.SHEET_BUGS + "' não foi encontrada no ficheiro original!";
                System.err.println(error);
                JOptionPane.showMessageDialog(null, error, "Erro de Aba", JOptionPane.ERROR_MESSAGE);
                return; // Interrompe tudo para não atualizar apenas metade
            }

            // 2. Atualizar a Aba Principal (VIM-Incidents)
            Sheet mainSheet = workbook.getSheet(config.snapshotSheetName);
            if (mainSheet == null) {
                System.err.println("Sheet " + config.snapshotSheetName + " not found!");
                return;
            }

            Row headerRow = mainSheet.getRow(0);
            Map<String, Integer> colMap = new HashMap<>();
            for (Cell cell : headerRow) {
                colMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
            }

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(MasterData.DATE_PATTERN_DISPLAY));

            updateMigratedRows(mainSheet, colMap, migrated, dateStyle);
            appendNewRows(mainSheet, colMap, newEntries, dateStyle);

            // 3. Atualizar a Aba de Bugs (Remover e Re-criar para limpar dados e manter Tabela)
            int bugIndex = workbook.getSheetIndex(bugSheet);
            workbook.removeSheetAt(bugIndex);
            new BugProcessor().createBugTableSheet(workbook, bugsList);

            // 4. Salvar
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            System.out.println("Snapshot updated successfully!");

        } catch (Exception e) {
            System.err.println("Error updating snapshot file.");
            throw e; // Lança para o MainApp tratar
        }
    }

    private void updateMigratedRows(Sheet sheet, Map<String, Integer> colMap, List<Incident> migrated, CellStyle dateStyle) {
        Integer idCol = colMap.get(MasterData.HEADER_ID);
        if (idCol == null) return;

        for (Incident inc : migrated) {
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell cell = row.getCell(idCol);
                String currentId = (cell == null) ? "" : cell.toString().trim();
                if (currentId.equals(inc.id)) {
                    fillRowData(row, colMap, inc, dateStyle);
                    break;
                }
            }
        }
    }

    private void appendNewRows(Sheet sheet, Map<String, Integer> colMap, List<Incident> newEntries, CellStyle dateStyle) {
        int lastRow = sheet.getLastRowNum();
        for (Incident inc : newEntries) {
            Row newRow = sheet.createRow(++lastRow);
            fillRowData(newRow, colMap, inc, dateStyle);
        }
    }

    private void fillRowData(Row row, Map<String, Integer> colMap, Incident inc, CellStyle dateStyle) {
        writeSafely(row, colMap.get(MasterData.HEADER_ID), inc.id, null);
        writeSafely(row, colMap.get(MasterData.HEADER_ARE), inc.are, null);
        writeSafely(row, colMap.get(MasterData.HEADER_CREATED_ON), inc.createdOn, dateStyle);
        writeSafely(row, colMap.get(MasterData.HEADER_REPORTED_BY), inc.reportedBy, null);
        writeSafely(row, colMap.get(MasterData.HEADER_LAST_CHANGED_ON), inc.lastChangedOn, dateStyle);
        writeSafely(row, colMap.get(MasterData.HEADER_PRIORITY), inc.priority, null);
        writeSafely(row, colMap.get(MasterData.HEADER_STATUS), inc.status, null);
        writeSafely(row, colMap.get(MasterData.HEADER_DESCRIPTION), inc.description, null);
    }

    private void writeSafely(Row row, Integer colIdx, Object value, CellStyle style) {
        if (colIdx == null || colIdx < 0 || value == null) return;
        Cell cell = row.getCell(colIdx);
        if (cell == null) cell = row.createCell(colIdx);

        if (value instanceof Date) {
            cell.setCellValue((Date) value);
            if (style != null) cell.setCellStyle(style);
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }
}
package ExcelCreator;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import java.util.List;

public class BugProcessor {
    public static boolean isBug(String category) {
        if (category == null) return false;
        String cat = category.toLowerCase();
        for (String bugCat : MasterData.BUG_CATEGORIES) {
            if (cat.contains(bugCat)) return true;
        }
        return false;
    }

    public void createBugTableSheet(XSSFWorkbook wb, List<Incident> bugData) {
        XSSFSheet sheet = wb.createSheet(MasterData.SHEET_BUGS);
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < MasterData.BUG_HEADERS.length; i++) {
            headerRow.createCell(i).setCellValue(MasterData.BUG_HEADERS[i]);
        }

        int rowIdx = 1;
        for (Incident inc : bugData) {
            XSSFRow row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(inc.id);
            row.createCell(1).setCellValue(inc.ticketNr);
            row.createCell(2).setCellValue(inc.recurrentTech);
            row.createCell(3).setCellValue(inc.recurrentBusiness);
        }

        if (rowIdx > 1) {
            AreaReference reference = wb.getCreationHelper().createAreaReference(
                    new CellReference(0, 0), new CellReference(rowIdx - 1, MasterData.BUG_HEADERS.length - 1));
            XSSFTable table = sheet.createTable(reference);
            table.setName("BugsTable");
            table.setDisplayName("BugsTable");
            table.getCTTable().addNewTableStyleInfo().setName("TableStyleMedium2");
            table.getCTTable().getTableStyleInfo().setShowRowStripes(true);
            table.getCTTable().addNewAutoFilter();
        }
        for (int i = 0; i < MasterData.BUG_HEADERS.length; i++) sheet.autoSizeColumn(i);
    }
}
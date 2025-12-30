package ExcelCreator;

import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        // 1. Seleção de ficheiros
        Path statusPath = pickFile("Select Status File");
        Path snapPath = pickFile("Select Snapshot File");
        if (statusPath == null || snapPath == null) return;

        // 2. Inputs com preenchimento automático (Default Values)
        String sheetName = (String) JOptionPane.showInputDialog(null,
                "Snapshot Sheet Name:", "Input",
                JOptionPane.QUESTION_MESSAGE, null, null,
                MasterData.DEFAULT_SNAPSHOT_SHEET);

        Date start = promptDate("Start Date (dd/MM/yyyy):", MasterData.DEFAULT_START_DATE);
        Date end = promptDate("End Date (dd/MM/yyyy):", MasterData.DEFAULT_END_DATE);

        if (start == null || end == null || sheetName == null) {
            JOptionPane.showMessageDialog(null, "Process cancelled or invalid inputs.");
            return;
        }

        // 3. Processamento
        AppConfig config = new AppConfig(start, end, sheetName);
        ExcelReader reader = new ExcelReader(config);
        IncidentMapper mapper = new IncidentMapper(config);

        List<Map<String, Object>> statusRaw = reader.readStatusFile(statusPath.toString());
        List<String> snapTickets = reader.readSnapshotTickets(snapPath.toString());

        List<Incident> migrated = new ArrayList<>();
        List<Incident> newEntries = new ArrayList<>();

        for (Map<String, Object> row : statusRaw) {
            Incident inc = mapper.mapFromStatusRow(row);
            if (snapTickets.contains(inc.futureNowTicket)) {
                migrated.add(inc);
            } else {
                newEntries.add(inc);
            }
        }

        // 4. Output
        try {
            Path outDir = Paths.get("excelFiles");
            Files.createDirectories(outDir);
            String outPath = outDir.resolve("Migrated_Snapshot.xlsx").toString();
            new ExcelWriter().write(outPath, migrated, newEntries);
            System.out.println("Success! Saved to: " + outPath);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error saving file: " + e.getMessage());
        }
    }

    private static Path pickFile(String title) {
        FileDialog dialog = new FileDialog((Frame)null, title, FileDialog.LOAD);
        dialog.setVisible(true);
        if (dialog.getFile() == null) return null;
        return Paths.get(dialog.getDirectory(), dialog.getFile());
    }

    /**
     * Pede uma data ao utilizador, pré-preenchendo o campo com defaultValue.
     */
    private static Date promptDate(String msg, String defaultValue) {
        String in = (String) JOptionPane.showInputDialog(null,
                msg, "Date Input",
                JOptionPane.QUESTION_MESSAGE, null, null,
                defaultValue);

        if (in == null || in.trim().isEmpty()) return null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(MasterData.DATE_PATTERN_DISPLAY);
            sdf.setLenient(false);
            return sdf.parse(in.trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid date format: " + in);
            return null;
        }
    }
}
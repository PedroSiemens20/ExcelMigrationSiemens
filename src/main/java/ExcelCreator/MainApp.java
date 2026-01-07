package ExcelCreator;

import ExcelSnapshotWriter.ExcelSnapshotWriter;
import javax.swing.*;
import java.awt.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        Path statusPath = pickFile("Select Status File");
        Path snapPath = pickFile("Select Snapshot File");
        if (statusPath == null || snapPath == null) return;

        String sheetName = (String) JOptionPane.showInputDialog(null, "Snapshot Sheet Name:", "Input", JOptionPane.QUESTION_MESSAGE, null, null, MasterData.DEFAULT_SNAPSHOT_SHEET);
        Date start = promptDate("Start Date (dd/MM/yyyy):", MasterData.DEFAULT_START_DATE);
        Date end = promptDate("End Date (dd/MM/yyyy):", MasterData.DEFAULT_END_DATE);
        if (start == null || end == null || sheetName == null) return;

        AppConfig config = new AppConfig(start, end, sheetName);
        ExcelReader reader = new ExcelReader(config);
        IncidentMapper mapper = new IncidentMapper(config);

        List<Map<String, Object>> allRows = reader.readStatusFile(statusPath.toString());
        Map<String, Incident> snapData = reader.readSnapshotData(snapPath.toString());

        List<Incident> migrated = new ArrayList<>();
        List<Incident> newEntries = new ArrayList<>();
        List<Incident> identical = new ArrayList<>();
        List<Incident> bugsList = new ArrayList<>();

        for (Map<String, Object> row : allRows) {
            Incident inc = mapper.mapFromStatusRow(row);

            if (BugProcessor.isBug(inc.category)) {
                bugsList.add(inc);
            }

            Date ctrl = ExcelUtils.extractDate(inc.lastChangedOn);
            Date cre = ExcelUtils.extractDate(inc.createdOn);

            if (isWithinRange(ctrl, config) || isWithinRange(cre, config)) {
                if (snapData.containsKey(inc.id)) {
                    Incident current = snapData.get(inc.id);
                    if (isIdentical(inc, current)) identical.add(inc);
                    else migrated.add(inc);
                } else {
                    newEntries.add(inc);
                }
            }
        }

        try {
            String outPath = Paths.get("excelFiles", "Migrated_Snapshot.xlsx").toString();
            Files.createDirectories(Paths.get("excelFiles"));
            new ExcelWriter().writeFull(outPath, migrated, newEntries, identical, bugsList);
            System.out.println("Draft file generated at: " + outPath);

            // --- CONFIRMAÇÃO PARA ATUALIZAR ORIGINAL ---
            int response = JOptionPane.showConfirmDialog(null,
                    "Migrated_Snapshot gerado com sucesso!\nDeseja atualizar o ficheiro Snapshot ORIGINAL agora?",
                    "Update Original Snapshot",
                    JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                ExcelSnapshotWriter updater = new ExcelSnapshotWriter(config);
                // Enviamos a bugsList também
                updater.updateExistingSnapshot(snapPath.toString(), migrated, newEntries, bugsList);
                JOptionPane.showMessageDialog(null, "Snapshot original atualizado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Processo concluído. O Snapshot original não foi alterado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ERRO: Certifique-se de que o Excel está FECHADO.\n" + e.getMessage());
        }
    }

    private static boolean isWithinRange(Date d, AppConfig config) {
        return d != null && !d.before(config.startDate) && !d.after(config.endDate);
    }

    private static boolean isIdentical(Incident n, Incident s) {
        return Objects.equals(n.status, s.status) &&
                Objects.equals(ExcelUtils.canonical(n.lastChangedOn), ExcelUtils.canonical(s.lastChangedOn));
    }

    private static Path pickFile(String t) {
        FileDialog d = new FileDialog((Frame)null, t, FileDialog.LOAD); d.setVisible(true);
        return (d.getFile() == null) ? null : Paths.get(d.getDirectory(), d.getFile());
    }

    private static Date promptDate(String m, String def) {
        String in = (String) JOptionPane.showInputDialog(null, m, "Date", JOptionPane.QUESTION_MESSAGE, null, null, def);
        try { return new java.text.SimpleDateFormat("dd/MM/yyyy").parse(in); } catch (Exception e) { return null; }
    }
}
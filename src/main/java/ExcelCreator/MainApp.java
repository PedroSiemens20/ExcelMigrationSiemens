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

        List<Map<String, Object>> statusRaw = reader.readStatusFile(statusPath.toString());
        Map<String, Incident> snapData = reader.readSnapshotData(snapPath.toString());

        List<Incident> migrated = new ArrayList<>();
        List<Incident> newEntries = new ArrayList<>();
        List<Incident> identical = new ArrayList<>();

        for (Map<String, Object> row : statusRaw) {
            Incident newInc = mapper.mapFromStatusRow(row);

            if (snapData.containsKey(newInc.futureNowTicket)) {
                Incident currentInc = snapData.get(newInc.futureNowTicket);
                if (isIdentical(newInc, currentInc)) {
                    identical.add(newInc);
                } else {
                    migrated.add(newInc);
                }
            } else {
                newEntries.add(newInc);
            }
        }

        try {
            Path outDir = Paths.get("excelFiles");
            Files.createDirectories(outDir);
            String outPath = outDir.resolve("Migrated_Snapshot.xlsx").toString();
            new ExcelWriter().writeWithIdentical(outPath, migrated, newEntries, identical);

            // Removi o JOptionPane daqui para o fluxo não parar
            System.out.println("Draft file generated at: " + outPath);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao gerar Migrated_Snapshot: " + e.getMessage());
            return; // Se falhou o rascunho, melhor não tentar o original
        }

        // Pergunta final
        int response = JOptionPane.showConfirmDialog(null,
                "Migrated_Snapshot gerado com sucesso!\nDeseja atualizar o ficheiro Snapshot ORIGINAL agora?",
                "Update Original Snapshot",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            try {
                ExcelSnapshotWriter updater = new ExcelSnapshotWriter(config);
                updater.updateExistingSnapshot(snapPath.toString(), migrated, newEntries);
                JOptionPane.showMessageDialog(null, "Snapshot original atualizado com sucesso!");
            } catch (Exception e) {
                // Se o ficheiro estiver aberto no Excel, o erro será apanhado aqui
                JOptionPane.showMessageDialog(null, "ERRO: Certifique-se de que o ficheiro Excel está FECHADO antes de atualizar.\n" + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Processo concluído. O Snapshot original não foi alterado.");
        }
    }

    // Compara se os dados processados são iguais aos que já estão no snapshot
    private static boolean isIdentical(Incident newInc, Incident snapInc) {
        return Objects.equals(newInc.status, snapInc.status) &&
                Objects.equals(newInc.are, snapInc.are) &&
                Objects.equals(newInc.priority, snapInc.priority) &&
                Objects.equals(ExcelUtils.canonical(newInc.lastChangedOn), ExcelUtils.canonical(snapInc.lastChangedOn));
    }

    private static Path pickFile(String title) {
        FileDialog dialog = new FileDialog((Frame)null, title, FileDialog.LOAD);
        dialog.setVisible(true);
        return (dialog.getFile() == null) ? null : Paths.get(dialog.getDirectory(), dialog.getFile());
    }

    private static Date promptDate(String msg, String def) {
        String in = (String) JOptionPane.showInputDialog(null, msg, "Date Input", JOptionPane.QUESTION_MESSAGE, null, null, def);
        try { return new java.text.SimpleDateFormat("dd/MM/yyyy").parse(in); } catch (Exception e) { return null; }
    }
}
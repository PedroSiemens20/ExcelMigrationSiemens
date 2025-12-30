
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class MainApp {
    public static void main(String[] args) {
        // 1) Pedir ficheiros via diálogo nativo do sistema
        Path statusSourcePath = pickExcelFileNative("Select Status Excel file");
        if (statusSourcePath == null) {
            System.err.println("Status file not selected. Exiting.");
            return;
        }

        Path snapshotsSourcePath = pickExcelFileNative("Select Snapshots Excel file");
        if (snapshotsSourcePath == null) {
            System.err.println("Snapshots file not selected. Exiting.");
            return;
        }

        // 1.1) Pedir ao utilizador o nome da sheet do Snapshot
        String snapshotSheet = promptSnapshotSheetName(MasterData.SNAPSHOT_SHEET);
        if (snapshotSheet == null || snapshotSheet.isBlank()) {
            System.err.println("Snapshot sheet name not provided. Exiting.");
            return;
        }
        // Guardar no MasterData para o resto do código usar
        MasterData.setSnapshotSheet(snapshotSheet);

        // 1.2) Pedir datas de início e fim (formato dd/MM/yyyy)
        Date startDate = promptDate("Enter START date (dd/MM/yyyy)", MasterData.DATE_PATTERN_DISPLAY, MasterData.START_DATE);
        if (startDate == null) {
            System.err.println("Start date not provided or invalid. Exiting.");
            return;
        }

        Date endDate = promptDate("Enter END date (dd/MM/yyyy)", MasterData.DATE_PATTERN_DISPLAY, MasterData.END_DATE);
        if (endDate == null) {
            System.err.println("End date not provided or invalid. Exiting.");
            return;
        }

        // Validar intervalo (start <= end)
        if (startDate.after(endDate)) {
            JOptionPane.showMessageDialog(null,
                    "Start date must be before or equal to End date.",
                    "Invalid Date Range",
                    JOptionPane.ERROR_MESSAGE);
            System.err.println("Invalid date range. Exiting.");
            return;
        }

        // Guardar no MasterData
        MasterData.setDateRange(startDate, endDate);

        // 2) Determinar pasta de destino
        Path devExcelDir = Paths.get("src", "main", "excelFiles"); // existe no projeto
        Path workingDir = Paths.get("").toAbsolutePath();
        Path excelDestDir = Files.isDirectory(devExcelDir) ? devExcelDir : workingDir.resolve("excelFiles");

        try {
            Files.createDirectories(excelDestDir);
        } catch (IOException e) {
            System.err.println("Failed to create destination folder: " + excelDestDir);
            e.printStackTrace();
            return;
        }

        // 3) Nomes de destino (mantém os nomes que já usavas)
        Path statusDestPath = excelDestDir.resolve("VIM-Incidents-Status - Copy.xlsx");
        Path snapshotsDestPath = excelDestDir.resolve("VIM-Incidents-Snapshots - Copy.xlsx");

        // 4) Copiar ficheiros
        try {
            Files.copy(statusSourcePath, statusDestPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(snapshotsSourcePath, snapshotsDestPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Files copied to: " + excelDestDir.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to copy files. Check paths and permissions.");
            e.printStackTrace();
            return;
        }

        // 5) Caminhos para processamento
        String statusFile = statusDestPath.toString();
        String snapshotsFile = snapshotsDestPath.toString();
        String outputFile = "Migrated_Snapshot.xlsx"; // podes mudar para excelFiles se preferires

        String statusSheet = MasterData.STATUS_SHEET;
        // snapshotSheet foi pedido ao utilizador e guardado no MasterData
        snapshotSheet = MasterData.getSnapshotSheet();

        SimpleDateFormat display = new SimpleDateFormat(MasterData.DATE_PATTERN_DISPLAY);
        System.out.println("Starting process...");
        System.out.println("Date range: " + display.format(MasterData.START_DATE) + " -> " + display.format(MasterData.END_DATE));
        System.out.println("Snapshot sheet: " + snapshotSheet);

        try {
            ExcelReader reader = new ExcelReader(statusFile, snapshotsFile, statusSheet, snapshotSheet);
            List<Map<String, Object>> statusData = reader.readStatusFile();
            List<String> snapshotTickets = reader.readSnapshotTickets();

            DataUpdater updater = new DataUpdater();
            List<Map<String, Object>> migratedData = updater.processUpdates(statusData, snapshotTickets);

            NewEntriesProcessor newEntriesProcessor = new NewEntriesProcessor();
            List<Map<String, Object>> newEntries = newEntriesProcessor.processNewEntries(statusData, snapshotTickets);

            ExcelWriter writer = new ExcelWriter();
            writer.writeToExcel(outputFile, migratedData, newEntries);

            System.out.println("Process finished successfully!");
            Path outputPath = Paths.get(outputFile).toAbsolutePath();
            System.out.println("Output file: " + outputPath);

            // 6) (Opcional) Abrir a pasta onde os ficheiros estão
            try {
                java.awt.Desktop.getDesktop().open(excelDestDir.toFile());
            } catch (Exception ignore) {}
        } catch (Exception e) {
            System.err.println("Processing failed:");
            e.printStackTrace();
        }
    }

    /**
     * Abre um diálogo nativo para escolher ficheiros Excel (.xlsx/.xls).
     * Retorna o Path escolhido ou null se o utilizador cancelar.
     */
    private static Path pickExcelFileNative(String title) {
        Frame frame = new Frame();
        try {
            FileDialog dialog = new FileDialog(frame, title, FileDialog.LOAD);
            dialog.setFile("*.xlsx;*.xls"); // filtro simples (dependente do OS)
            dialog.setVisible(true);

            String dir = dialog.getDirectory();
            String file = dialog.getFile();
            if (dir == null || file == null) {
                return null; // cancelado
            }
            Path chosen = Paths.get(dir, file);
            // validação simples
            String name = chosen.getFileName().toString().toLowerCase();
            if (!(name.endsWith(".xlsx") || name.endsWith(".xls"))) {
                System.err.println("Selected file is not an Excel file: " + chosen);
                return null;
            }
            if (!Files.isReadable(chosen)) {
                System.err.println("Selected file is not readable: " + chosen);
                return null;
            }
            return chosen;
        } finally {
            frame.dispose();
        }
    }

    /**
     * Pede ao utilizador o nome da sheet do Snapshot via JOptionPane.
     * Se o utilizador cancelar, retorna null.
     */
    private static String promptSnapshotSheetName(String defaultValue) {
        String input = (String) JOptionPane.showInputDialog(
                null,
                "Snapshot sheet name:",
                "Snapshot Sheet",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                defaultValue
        );
        return input;
    }

    /**
     * Pede uma data ao utilizador com validação e formato dd/MM/yyyy.
     * Se cancelar, retorna null.
     */
    private static Date promptDate(String message, String pattern, Date defaultValue) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false); // validação rigorosa
        String defaultStr = defaultValue != null ? sdf.format(defaultValue) : "";
        while (true) {
            String input = (String) JOptionPane.showInputDialog(
                    null,
                    message,
                    "Date Input",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    defaultStr
            );
            if (input == null) {
                // Cancelado
                return null;
            }
            input = input.trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Please enter a date in format " + pattern,
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                continue;
            }
            try {
                return sdf.parse(input);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(null,
                        "Invalid date. Please use format " + pattern + " (e.g., 15/12/2025)",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

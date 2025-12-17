
import java.util.List;
import java.util.Map;

public class MainApp {
    public static void main(String[] args) {

        String statusFile = "src/main/ExcelFiles/VIM-Incidents-Status - Copy.xlsx";
        String snapshotsFile = "src/main/ExcelFiles/VIM-Incidents-Snapshots - Copy.xlsx";
        String outputFile = "Migrated_Snapshot.xlsx";

        String statusSheet = MasterData.STATUS_SHEET;
        String snapshotSheet = MasterData.SNAPSHOT_SHEET;

        System.out.println("Starting process...");
        System.out.println("Date range: " + MasterData.START_DATE + " -> " + MasterData.END_DATE);

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
    }
}

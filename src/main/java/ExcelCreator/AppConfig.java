package ExcelCreator;

import java.util.Date;

public class AppConfig {
    public final Date startDate;
    public final Date endDate;
    public final String snapshotSheetName;

    public AppConfig(Date startDate, Date endDate, String snapshotSheetName) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.snapshotSheetName = snapshotSheetName;
    }
}
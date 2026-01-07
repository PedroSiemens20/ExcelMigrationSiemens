package ExcelCreator;

import java.util.Date;
import java.util.Map;

public class IncidentMapper {
    private final AppConfig config;

    public IncidentMapper(AppConfig config) {
        this.config = config;
    }

    public Incident mapFromStatusRow(Map<String, Object> row) {
        Incident incident = new Incident();

        incident.id = ExcelUtils.canonical(row.get(MasterData.COL_ID)); // O INC está aqui
        incident.reportedBy = ExcelUtils.canonical(row.get(MasterData.COL_REPORTED_BY));
        incident.priority = ExcelUtils.canonical(row.get(MasterData.COL_PRIORITY));
        incident.description = ExcelUtils.canonical(row.get(MasterData.COL_DESCRIPTION));

        incident.createdOn = row.get(MasterData.COL_CREATED);
        incident.lastChangedOn = row.get(MasterData.COL_CONTROL_DATE);

        // Lógica de Datas e Status
        Date dateCreated = ExcelUtils.extractDate(incident.createdOn);
        Date dateLastChanged = ExcelUtils.extractDate(incident.lastChangedOn);
        String rawStatus = ExcelUtils.canonical(row.get(MasterData.COL_STATUS));
        String rawAre = ExcelUtils.canonical(row.get(MasterData.COL_ARE));
        incident.are = rawAre;
        // Regra ARE
        /*
        if (rawAre != null && (rawAre.equalsIgnoreCase(MasterData.ARE_ORA) || rawAre.equalsIgnoreCase(MasterData.ARE_SIB))) {
            incident.are = MasterData.ARE_BUZ;
        } else {
incident.are = rawAre;
        }

         */
        // Regra Status
        String finalStatus = rawStatus;
        if (MasterData.STATUS_CLOSED.equalsIgnoreCase(rawStatus)) {
            finalStatus = MasterData.STATUS_CONFIRM_CLOSED;
        }
        incident.status = finalStatus;

        return incident;
    }
}
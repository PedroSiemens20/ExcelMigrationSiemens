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

        // 1. Guardar os valores brutos para garantir que não se perde informação
        incident.createdOn = row.get(MasterData.COL_CREATED);
        incident.lastChangedOn = row.get(MasterData.COL_CONTROL_DATE);

        // 2. Tentar extrair datas reais apenas para a lógica de negócio (flags)
        Date dateCreated = ExcelUtils.extractDate(incident.createdOn);
        Date dateLastChanged = ExcelUtils.extractDate(incident.lastChangedOn);

        String rawStatus = ExcelUtils.canonical(row.get(MasterData.COL_STATUS));
        String rawAre = ExcelUtils.canonical(row.get(MasterData.COL_ARE));

        // Regra ARE
        if (rawAre != null && (rawAre.equalsIgnoreCase(MasterData.ARE_ORA) || rawAre.equalsIgnoreCase(MasterData.ARE_SIB))) {
            incident.are = MasterData.ARE_BUZ;
        } else {
            incident.are = rawAre;
        }

        // Regra Status e Flags (Usa as datas extraídas se existirem)
        String finalStatus = rawStatus;
        if (dateCreated != null && dateLastChanged != null &&
                !dateCreated.before(config.startDate) && !dateCreated.after(config.endDate) &&
                dateLastChanged.after(config.endDate)) {
            finalStatus = MasterData.STATUS_IN_PROCESS;
            incident.lastChangedOn = incident.createdOn;

        } else if (MasterData.STATUS_CLOSED.equalsIgnoreCase(rawStatus)) {
            finalStatus = MasterData.STATUS_CONFIRM_CLOSED;
        }
        incident.status = finalStatus;

        incident.id = ExcelUtils.canonical(row.get(MasterData.COL_TICKET_NR));
        incident.futureNowTicket = ExcelUtils.canonical(row.get(MasterData.COL_INCIDENT));
        incident.reportedBy = ExcelUtils.canonical(row.get(MasterData.COL_REPORTED_BY));
        incident.priority = ExcelUtils.canonical(row.get(MasterData.COL_PRIORITY));
        incident.description = ExcelUtils.canonical(row.get(MasterData.COL_DESCRIPTION));

        return incident;
    }
}
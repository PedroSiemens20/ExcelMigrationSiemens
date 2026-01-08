package ExcelCreator;
import java.util.Map;

public class IncidentMapper {
    private final AppConfig config;
    public IncidentMapper(AppConfig config) { this.config = config; }

    public Incident mapFromStatusRow(Map<String, Object> row) {
        Incident incident = new Incident();

        incident.id = ExcelUtils.canonical(row.get(MasterData.COL_ID));
        incident.ticketNr = ExcelUtils.canonical(row.get(MasterData.COL_TICKET_NR));
        incident.category = ExcelUtils.canonical(row.get(MasterData.COL_CATEGORY));
        incident.recurrentTech = ExcelUtils.canonical(row.get(MasterData.COL_REC_TECH));
        incident.recurrentBusiness = ExcelUtils.canonical(row.get(MasterData.COL_REC_BUSINESS));

        incident.reportedBy = ExcelUtils.canonical(row.get(MasterData.COL_REPORTED_BY));
        incident.priority = ExcelUtils.canonical(row.get(MasterData.COL_PRIORITY));
        incident.description = ExcelUtils.canonical(row.get(MasterData.COL_DESCRIPTION));
        incident.are = ExcelUtils.canonical(row.get(MasterData.COL_ARE));

        incident.createdOn = row.get(MasterData.COL_CREATED);
        incident.lastChangedOn = row.get(MasterData.COL_CONTROL_DATE);

        String rawStatus = ExcelUtils.canonical(row.get(MasterData.COL_STATUS));
        //Status Rules confirm to confirm closed
        if (MasterData.STATUS_CLOSED.equalsIgnoreCase(rawStatus)) {
            incident.status = MasterData.STATUS_CONFIRM_CLOSED;
        } else {
            incident.status = rawStatus;
        }

        //Regra: Se a coluna Incident estiver vazia vamos usar a Coluna Ticket Nr, se amabs vazias manda um aviso para a consola
        if (incident.id == null || incident.id.trim().isEmpty()) {
            if (incident.ticketNr != null && !incident.ticketNr.trim().isEmpty()) {
                incident.id = incident.ticketNr;
            } else {
                System.out.println("WARNING: Row with no Incident ID and no Ticket Nr. found.");
            }
        }





        return incident;
    }
}
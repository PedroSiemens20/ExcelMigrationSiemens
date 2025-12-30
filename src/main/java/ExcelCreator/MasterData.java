package ExcelCreator;

public class MasterData {
    public static final String STATUS_SHEET = "VIM";
    public static final String DEFAULT_SNAPSHOT_SHEET = "VIM-Incidents_20251221";
    public static final String DATE_PATTERN_DISPLAY = "dd/MM/yyyy";

    // DATAS PADRÃO (Sugestão para o utilizador)
    public static final String DEFAULT_START_DATE = "15/12/2025";
    public static final String DEFAULT_END_DATE = "21/12/2025";

    // Colunas Input (Ficheiro Status)
    public static final String COL_INCIDENT = "Incident";
    public static final String COL_REPORTED_BY = "Reported By";
    public static final String COL_ARE = "ARE";
    public static final String COL_CREATED = "Created";
    public static final String COL_CONTROL_DATE = "Control Date";
    public static final String COL_PRIORITY = "Priority";
    public static final String COL_DESCRIPTION = "Description";
    public static final String COL_STATUS = "Status";
    public static final String COL_TICKET_NR = "Ticket Nr.";

    // Colunas Input (Snapshot) e Output
    public static final String COL_FUTURENOW_TICKET = "FutureNow Ticket";
    public static final String HEADER_FUTURENOW_TICKET = "FutureNow Ticket";
    public static final String HEADER_ID = "ID";
    public static final String HEADER_EXTERNAL_ID = "External ID";
    public static final String HEADER_ARE = "ARE";
    public static final String HEADER_CREATED_ON = "Created On";
    public static final String HEADER_DUE_BY = "Due by";
    public static final String HEADER_REPORTED_BY = "Reported By";
    public static final String HEADER_MESSAGE_PROCESSOR = "Message Processor";
    public static final String HEADER_LAST_CHANGED_ON = "Last Changed on";
    public static final String HEADER_LAST_CHANGED_BY = "Last Changed By";
    public static final String HEADER_PRIORITY = "Priority";
    public static final String HEADER_STATUS = "Status";
    public static final String HEADER_DESCRIPTION = "Description";

    public static final String[] OUTPUT_HEADERS = {
            HEADER_ID, HEADER_FUTURENOW_TICKET, HEADER_EXTERNAL_ID, HEADER_ARE,
            HEADER_CREATED_ON, HEADER_DUE_BY, HEADER_REPORTED_BY, HEADER_MESSAGE_PROCESSOR,
            HEADER_LAST_CHANGED_ON, HEADER_LAST_CHANGED_BY, HEADER_PRIORITY, HEADER_STATUS, HEADER_DESCRIPTION
    };

    public static final String STATUS_CLOSED = "Closed";
    public static final String STATUS_CONFIRM_CLOSED = "Confirm_Closed";
    public static final String STATUS_IN_PROCESS = "In Process";
    public static final String ARE_ORA = "ORA";
    public static final String ARE_SIB = "SIB";
    public static final String ARE_BUZ = "BUZ";
}
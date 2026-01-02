package ExcelCreator;

public class MasterData {
    public static final String STATUS_SHEET = "VIM";
    public static final String DEFAULT_SNAPSHOT_SHEET = "VIM-Incidents_20251221";
    public static final String DATE_PATTERN_DISPLAY = "dd/MM/yyyy";
    public static final String DEFAULT_START_DATE = "15/12/2025";
    public static final String DEFAULT_END_DATE = "21/12/2025";

    // Colunas
    public static final String COL_INCIDENT = "Incident";
    public static final String COL_REPORTED_BY = "Reported By";
    public static final String COL_ARE = "ARE";
    public static final String COL_CREATED = "Created";
    public static final String COL_CONTROL_DATE = "Control Date";
    public static final String COL_PRIORITY = "Priority";
    public static final String COL_DESCRIPTION = "Description";
    public static final String COL_STATUS = "Status";
    public static final String COL_TICKET_NR = "Ticket Nr.";

    public static final String COL_FUTURENOW_TICKET = "FutureNow Ticket";
    public static final String HEADER_FUTURENOW_TICKET = "FutureNow Ticket";
    public static final String HEADER_ID = "ID";
    public static final String HEADER_ARE = "ARE";
    public static final String HEADER_CREATED_ON = "Created On";
    public static final String HEADER_REPORTED_BY = "Reported By";
    public static final String HEADER_LAST_CHANGED_ON = "Last Changed on";
    public static final String HEADER_PRIORITY = "Priority";
    public static final String HEADER_STATUS = "Status";
    public static final String HEADER_DESCRIPTION = "Description";

    public static final String[] OUTPUT_HEADERS = {
            HEADER_ID, HEADER_FUTURENOW_TICKET, "External ID", HEADER_ARE,
            HEADER_CREATED_ON, "Due by", HEADER_REPORTED_BY, "Message Processor",
            HEADER_LAST_CHANGED_ON, "Last Changed By", HEADER_PRIORITY, HEADER_STATUS, HEADER_DESCRIPTION
    };

    public static final String STATUS_CLOSED = "Closed";
    public static final String STATUS_CONFIRM_CLOSED = "Confirm_Closed";
    public static final String STATUS_IN_PROCESS = "In Process";
    public static final String ARE_ORA = "ORA";
    public static final String ARE_SIB = "SIB";
    public static final String ARE_BUZ = "BUZ";
}
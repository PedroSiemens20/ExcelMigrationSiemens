package ExcelCreator;

public class MasterData {
    public static final String STATUS_SHEET = "VIM";
    public static final String DEFAULT_SNAPSHOT_SHEET = "VIM-Incidents";
    public static final String DATE_PATTERN_DISPLAY = "dd/MM/yyyy";
    public static final String DEFAULT_START_DATE = "01/12/2025";
    public static final String DEFAULT_END_DATE = "04/01/2026";

    // --- COLUNAS DO STATUS (Leitura - Amarelo) ---
    public static final String COL_ID = "Incident";
    public static final String COL_TICKET_NR = "Ticket Nr.";
    public static final String COL_ARE = "ARE";
    public static final String COL_CREATED = "Created";
    public static final String COL_CONTROL_DATE = "Control Date";
    public static final String COL_REPORTED_BY = "Reported By";
    public static final String COL_PRIORITY = "Priority";
    public static final String COL_STATUS = "Status";
    public static final String COL_DESCRIPTION = "Description";
    public static final String COL_CATEGORY = "Category";
    public static final String COL_REC_TECH = "Recurrent Issue\n(tech)";
    public static final String COL_REC_BUSINESS = "Recurrent Issue\n(Business)";

    // --- CABEÇALHOS DO SNAPSHOT (Escrita - Verde) ---
    public static final String HEADER_ID = "ID";
    public static final String HEADER_ARE = "ARE";
    public static final String HEADER_CREATED_ON = "Created On";
    public static final String HEADER_REPORTED_BY = "Reported By";
    public static final String HEADER_LAST_CHANGED_ON = "Last Changed on";
    public static final String HEADER_PRIORITY = "Priority";
    public static final String HEADER_STATUS = "Status";
    public static final String HEADER_DESCRIPTION = "Description";

    public static final String[] OUTPUT_HEADERS = {
            HEADER_ID, HEADER_ARE, HEADER_CREATED_ON, HEADER_REPORTED_BY,
            HEADER_LAST_CHANGED_ON, HEADER_PRIORITY, HEADER_STATUS, HEADER_DESCRIPTION
    };

    // --- CONFIGURAÇÃO BUGS ---
    public static final String SHEET_BUGS = "Bugs";
    public static final String[] BUG_HEADERS = {
            "FutureNow Ticket", "ID", "Recurrent Issue (tech)", "Recurrent Issue (Business)"
    };
    public static final String[] BUG_CATEGORIES = {"product bug", "project bug", "bug/cr"};

    public static final String STATUS_CLOSED = "Closed";
    public static final String STATUS_CONFIRM_CLOSED = "Confirm_Closed";
}
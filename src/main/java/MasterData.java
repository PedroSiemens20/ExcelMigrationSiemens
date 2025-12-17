import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum MasterData {
    INSTANCE; // Singleton, mas aqui só para manter padrão enum

    // Nomes de Sheets

    public static final String STATUS_SHEET = "VIM";
    public static final String SNAPSHOT_SHEET = "VIM-Incidents_20251130";

    // Intervalo de datas (definido por constantes String e convertido para Date)

    private static final String START_DATE_STR = "15/12/2025";
    private static final String END_DATE_STR = "21/12/2025";


    public static Date START_DATE;
    public static Date END_DATE;

    public static final String DATE_PATTERN_DISPLAY = "dd/MM/yyyy";

    static {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_DISPLAY);
        try {
            START_DATE = sdf.parse(START_DATE_STR);
            END_DATE = sdf.parse(END_DATE_STR);
        } catch (ParseException e) {
            // Se der erro, evita NPE e mantém valores nulos
            START_DATE = null;
            END_DATE = null;
        }
    }


    // Formatos de data




    // Nomes de colunas (Status)
    public static final String COL_INCIDENT = "Incident";
    public static final String COL_REPORTED_BY = "Reported By";
    public static final String COL_ARE = "ARE";
    public static final String COL_CREATED = "Created";
    public static final String COL_CONTROL_DATE = "Control Date";
    public static final String COL_PRIORITY = "Priority";
    public static final String COL_DESCRIPTION = "Description";
    public static final String COL_STATUS = "Status";
    public static final String COL_FUTURENOW_TICKET = "FutureNow Ticket";
    public static final String COL_TICKET_NR = "Ticket Nr.";

    // Nomes de colunas (Snapshot)
    public static final String HEADER_ID = "ID";
    public static final String HEADER_FUTURENOW_TICKET = "FutureNow Ticket";
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

    // Regras de Status

    public static final String STATUS_CLOSED = "Closed";
    public static final String STATUS_CONFIRMED_CLOSED = "Confirmed_Closed";
    public static final String STATUS_IN_PROCESS = "In Process";


}


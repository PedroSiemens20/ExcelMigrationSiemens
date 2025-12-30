
import java.text.SimpleDateFormat;
import java.util.*;

public class NewEntriesProcessor {

    public List<Map<String, Object>> processNewEntries(
            List<Map<String, Object>> statusData,
            List<String> snapshotTickets) {

        List<Map<String, Object>> newEntries = new ArrayList<>();
        System.out.println("Starting NewEntriesProcessor logic...");

        for (Map<String, Object> row : statusData) {
            String incidentId = canonical(row.get(MasterData.COL_INCIDENT));
            if (incidentId != null && !snapshotTickets.contains(incidentId)) {
                Map<String, Object> mappedRow = mapRow(row);
                newEntries.add(mappedRow);
            }
        }

        System.out.println("NewEntriesProcessor finished. New entries count: " + newEntries.size());
        return newEntries;
    }

    private Map<String, Object> mapRow(Map<String, Object> row) {
        Map<String, Object> mapped = new LinkedHashMap<>();

        String status = canonical(row.get(MasterData.COL_STATUS));
        Object createdOn = row.get(MasterData.COL_CREATED);
        Date createdDate = extractDate(createdOn);

        // Regra: se Created dentro do intervalo → "In Process"
       // if (createdDate != null
         //       && MasterData.START_DATE != null
           //     && MasterData.END_DATE != null
             //   && !createdDate.before(MasterData.START_DATE)
               // && !createdDate.after(MasterData.END_DATE)) {
          //  status = MasterData.STATUS_IN_PROCESS;
        //}

        // Normalização Closed → Confirm_Closed
        if (status != null && status.equalsIgnoreCase(MasterData.STATUS_CLOSED)) {
            status = MasterData.STATUS_CONFIRM_CLOSED;
        }

        // Preserva tipos

        mapped.put(MasterData.HEADER_ID, row.get(MasterData.COL_TICKET_NR));

        mapped.put(MasterData.HEADER_FUTURENOW_TICKET, row.get(MasterData.COL_INCIDENT));
        mapped.put(MasterData.HEADER_REPORTED_BY, row.get(MasterData.COL_REPORTED_BY));
        mapped.put(MasterData.HEADER_ARE, row.get(MasterData.COL_ARE));
        mapped.put(MasterData.HEADER_CREATED_ON, createdOn);
        mapped.put(MasterData.HEADER_LAST_CHANGED_ON, row.get(MasterData.COL_CONTROL_DATE));
        mapped.put(MasterData.HEADER_PRIORITY, row.get(MasterData.COL_PRIORITY));
        mapped.put(MasterData.HEADER_DESCRIPTION, row.get(MasterData.COL_DESCRIPTION));
        mapped.put(MasterData.HEADER_STATUS, status);

        return mapped;
    }

    private Date extractDate(Object value) {
        if (value == null) return null;
        if (value instanceof Date) return (Date) value;
        if (value instanceof String) {
            try {
                return new SimpleDateFormat(MasterData.DATE_PATTERN_DISPLAY).parse(((String) value).trim());
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String canonical(Object v) {
        if (v == null) return null;
        if (v instanceof String) return ((String) v).trim();
        if (v instanceof Number) {
            double d = ((Number) v).doubleValue();
            if (Math.rint(d) == d) {
                // Inteiro: sem ".0"
                return String.format(Locale.ROOT, "%.0f", d);
            } else {
                // Decimal: sem notação científica/zeros supérfluos
                return java.math.BigDecimal.valueOf(d).stripTrailingZeros().toPlainString();
            }
        }
        if (v instanceof Date) {
            return new SimpleDateFormat(MasterData.DATE_PATTERN_DISPLAY).format((Date) v);
        }
        if (v instanceof Boolean) return Boolean.toString((Boolean) v);
        return v.toString();
    }
}
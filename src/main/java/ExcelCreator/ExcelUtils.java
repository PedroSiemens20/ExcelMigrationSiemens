package ExcelCreator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExcelUtils {
    private static final SimpleDateFormat SDF = new SimpleDateFormat(MasterData.DATE_PATTERN_DISPLAY);

    public static String canonical(Object v) {
        if (v == null) return null;
        if (v instanceof String) return ((String) v).trim();
        if (v instanceof Number) {
            double d = ((Number) v).doubleValue();
            if (Math.rint(d) == d) return String.format(Locale.ROOT, "%.0f", d);
            return java.math.BigDecimal.valueOf(d).stripTrailingZeros().toPlainString();
        }
        if (v instanceof Date) return SDF.format((Date) v);
        return v.toString();
    }

    public static Date extractDate(Object value) {
        if (value == null) return null;
        if (value instanceof Date) return (Date) value;
        try {
            return SDF.parse(value.toString().trim());
        } catch (Exception e) {
            return null;
        }
    }
}
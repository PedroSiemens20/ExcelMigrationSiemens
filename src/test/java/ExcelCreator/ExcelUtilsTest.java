package ExcelCreator;

import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.Calendar;
import static org.junit.jupiter.api.Assertions.*;

class ExcelUtilsTest {

    @Test
    void testCanonicalDoubleToIntegerString() {
        // Testa se 123.0 vira "123" e se decimais são mantidos corretamente
        assertEquals("123", ExcelUtils.canonical(123.0));
        assertEquals("123.45", ExcelUtils.canonical(123.45));
    }

    @Test
    void testCanonicalNull() {
        assertNull(ExcelUtils.canonical(null));
    }

    @Test
    void testCanonicalDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.DECEMBER, 25);
        Date date = cal.getTime();
        // O formato deve seguir o DATE_PATTERN_DISPLAY (dd/MM/yyyy)
        assertEquals("25/12/2025", ExcelUtils.canonical(date));
    }

    @Test
    void testExtractDate() {
        // Testa extração de um objeto Date real
        Date dateObj = new Date();
        Date result = ExcelUtils.extractDate(dateObj);
        assertNotNull(result);
        assertEquals(dateObj, result);
    }
}
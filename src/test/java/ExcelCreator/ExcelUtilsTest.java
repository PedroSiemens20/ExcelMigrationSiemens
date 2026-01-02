package ExcelCreator;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class ExcelUtilsTest {

    @Test
    void testCanonicalDoubleToIntegerString() {
        assertEquals("123", ExcelUtils.canonical(123.0));
        assertEquals("123.45", ExcelUtils.canonical(123.45));
    }

    @Test
    void testCanonicalNull() {
        assertNull(ExcelUtils.canonical(null));
    }

    @Test
    void testExtractDate() {
        Object dateObj = new Date(1734220800000L);
        Date result = ExcelUtils.extractDate(dateObj);
        assertNotNull(result);
    }
}
package ExcelCreator;

import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.Calendar;
import static org.junit.jupiter.api.Assertions.*;

class ExcelUtilsTest {

    @Test
    void testCanonicalFormatting() {
        // Testa se remove o .0 de números inteiros vindos do Excel
        assertEquals("44338463", ExcelUtils.canonical(44338463.0));
        // Testa se mantém decimais se existirem
        assertEquals("123.45", ExcelUtils.canonical(123.45));
        // Testa se limpa espaços
        assertEquals("INC123", ExcelUtils.canonical("  INC123  "));
    }

    @Test
    void testExtractDate() {
        // Testa se consegue converter uma data real
        Date now = new Date();
        assertEquals(now, ExcelUtils.extractDate(now));

        // Testa se falha graciosamente com formatos errados (deve retornar null)
        assertNull(ExcelUtils.extractDate("data-errada"));
    }
}
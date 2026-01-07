package ExcelCreator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BugProcessorTest {

    @Test
    void testIsBugFilter() {
        // Testamos variações de escrita para garantir que o filtro é robusto
        assertTrue(BugProcessor.isBug("Project Bug"));
        assertTrue(BugProcessor.isBug("product bug"));
        assertTrue(BugProcessor.isBug("BUG/CR"));

        // Testamos coisas que NÃO são bugs
        assertFalse(BugProcessor.isBug("Q&A"));
        assertFalse(BugProcessor.isBug("CR"));
        assertFalse(BugProcessor.isBug(null));
    }
}
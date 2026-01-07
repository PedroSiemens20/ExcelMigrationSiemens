package ExcelCreator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class IncidentIntegrationTest {

    private IncidentMapper mapper;
    private AppConfig config;

    @BeforeEach
    void setUp() throws Exception {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        Date start = sdf.parse("29/12/2025");
        Date end = sdf.parse("04/01/2026");
        config = new AppConfig(start, end, "VIM-Incidents");
        mapper = new IncidentMapper(config);
    }

    @Test
    void testBulkConversionFromStatusRows() {
        // 1. Criamos uma "massa de dados" que simula várias linhas lidas do Excel de Status
        List<Map<String, Object>> mockExcelRows = new ArrayList<>();

        // Linha 1: Um ticket ORA (deve virar BUZ) e Status Closed (deve virar Confirm_Closed)
        Map<String, Object> row1 = new HashMap<>();
        row1.put(MasterData.COL_ID, "INC44000111");
        row1.put(MasterData.COL_ARE, "ORA");
        row1.put(MasterData.COL_STATUS, "Closed");
        row1.put(MasterData.COL_CREATED, "30/12/2025");
        row1.put(MasterData.COL_CONTROL_DATE, "30/12/2025");
        mockExcelRows.add(row1);

        // Linha 2: Um ticket BUZ normal e In Process
        Map<String, Object> row2 = new HashMap<>();
        row2.put(MasterData.COL_ID, "INC44000222");
        row2.put(MasterData.COL_ARE, "BUZ");
        row2.put(MasterData.COL_STATUS, "In Process");
        row2.put(MasterData.COL_CREATED, "31/12/2025");
        row2.put(MasterData.COL_CONTROL_DATE, "02/01/2026");
        mockExcelRows.add(row2);

        // Linha 3: Um ticket SIB (deve virar BUZ)
        Map<String, Object> row3 = new HashMap<>();
        row3.put(MasterData.COL_ID, "INC44000333");
        row3.put(MasterData.COL_ARE, "SIB");
        row3.put(MasterData.COL_STATUS, "In Process");
        row3.put(MasterData.COL_CREATED, "01/01/2026");
        row3.put(MasterData.COL_CONTROL_DATE, "01/01/2026");
        mockExcelRows.add(row3);

        // 2. Executamos a conversão de todas as linhas (como o MainApp faz)
        List<Incident> processedIncidents = new ArrayList<>();
        for (Map<String, Object> row : mockExcelRows) {
            processedIncidents.add(mapper.mapFromStatusRow(row));
        }

        // 3. Verificações (Asserções)
        assertEquals(3, processedIncidents.size(), "Devem ter sido processadas 3 linhas");

        // Validar Linha 1 (ORA -> BUZ, Closed -> Confirm_Closed)
        Incident i1 = processedIncidents.get(0);
        assertEquals("INC44000111", i1.id);
        assertEquals(MasterData.ARE_BUZ, i1.are);
        assertEquals(MasterData.STATUS_CONFIRM_CLOSED, i1.status);

        // Validar Linha 2 (Manter BUZ e In Process)
        Incident i2 = processedIncidents.get(1);
        assertEquals("INC44000222", i2.id);
        assertEquals("BUZ", i2.are);
        assertEquals("In Process", i2.status);

        // Validar Linha 3 (SIB -> BUZ)
        Incident i3 = processedIncidents.get(2);
        assertEquals(MasterData.ARE_BUZ, i3.are);
    }
}
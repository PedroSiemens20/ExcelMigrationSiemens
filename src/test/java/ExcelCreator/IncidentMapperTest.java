package ExcelCreator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class IncidentMapperTest {

    private IncidentMapper mapper;
    private AppConfig config;

    @BeforeEach
    void setUp() throws Exception {
        // Configurar intervalo de datas padrão para os testes
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        Date start = sdf.parse("29/12/2025");
        Date end = sdf.parse("04/01/2026");
        config = new AppConfig(start, end, "VIM-Incidents");
        mapper = new IncidentMapper(config);
    }

    @Test
    void testFullMappingAndIdExtraction() {
        Map<String, Object> row = new HashMap<>();
        // Colunas conforme a nova estrutura (Amarela)
        row.put(MasterData.COL_ID, "INC44222821");
        row.put(MasterData.COL_TICKET_NR, "6000123456");
        row.put(MasterData.COL_ARE, "ORA"); // Agora deve manter ORA
        row.put(MasterData.COL_STATUS, "In Process");
        row.put(MasterData.COL_REPORTED_BY, "Test User");
        row.put(MasterData.COL_PRIORITY, "2 - High");
        row.put(MasterData.COL_DESCRIPTION, "Test description");

        Incident result = mapper.mapFromStatusRow(row);

        assertEquals("INC44222821", result.id, "O ID deve ser o INC");
        assertEquals("6000123456", result.ticketNr, "O Ticket Nr deve ser o 6000...");
        assertEquals("ORA", result.are, "O ARE deve ser mantido como no original");
        assertEquals("In Process", result.status);
        assertEquals("Test User", result.reportedBy);
    }

    @Test
    void testBugColumnsMapping() {
        Map<String, Object> row = new HashMap<>();
        row.put(MasterData.COL_REC_TECH, "x");
        row.put(MasterData.COL_REC_BUSINESS, "");

        Incident result = mapper.mapFromStatusRow(row);

        // Verifica se as novas colunas de recorrência para os bugs estão a ser lidas
        assertEquals("x", result.recurrentTech);
        assertEquals("", result.recurrentBusiness);
    }

    @Test
    void testStatusClosedToConfirmClosed() {
        Map<String, Object> row = new HashMap<>();
        row.put(MasterData.COL_STATUS, "Closed");

        Incident result = mapper.mapFromStatusRow(row);

        // Verifica se a regra de negócio do Status (IF/ELSE) está a funcionar
        assertEquals(MasterData.STATUS_CONFIRM_CLOSED, result.status, "Closed deve virar Confirm_Closed");
    }

    @Test
    void testOtherStatusRemainsUnchanged() {
        Map<String, Object> row = new HashMap<>();
        row.put(MasterData.COL_STATUS, "In Process");

        Incident result = mapper.mapFromStatusRow(row);

        // Garante que o código não mexe no que não deve (como o In Process)
        assertEquals("In Process", result.status, "Outros status não devem ser alterados");
    }
}
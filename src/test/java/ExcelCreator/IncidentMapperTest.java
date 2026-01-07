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
        row.put(MasterData.COL_ID, "INC44222821"); // Coluna "Incident"
        row.put(MasterData.COL_ARE, "BUZ");
        row.put(MasterData.COL_STATUS, "In Process");
        row.put(MasterData.COL_REPORTED_BY, "Test User");
        row.put(MasterData.COL_PRIORITY, "2 - High");
        row.put(MasterData.COL_DESCRIPTION, "Test description");

        Incident result = mapper.mapFromStatusRow(row);

        assertEquals("INC44222821", result.id, "O ID deve ser extraído da coluna Incident");
        assertEquals("BUZ", result.are);
        assertEquals("In Process", result.status);
        assertEquals("Test User", result.reportedBy);
    }

    @Test
    void testAreNormalization() {
        // Simular linha com ARE = ORA ou SIB para validar conversão para BUZ
        Map<String, Object> rowORA = new HashMap<>();
        rowORA.put(MasterData.COL_ARE, "ORA");

        Map<String, Object> rowSIB = new HashMap<>();
        rowSIB.put(MasterData.COL_ARE, "SIB");

        Incident resORA = mapper.mapFromStatusRow(rowORA);
        Incident resSIB = mapper.mapFromStatusRow(rowSIB);

        assertEquals(MasterData.ARE_BUZ, resORA.are, "ORA deve ser convertido para BUZ");
        assertEquals(MasterData.ARE_BUZ, resSIB.are, "SIB deve ser convertido para BUZ");
    }

    @Test
    void testStatusClosedToConfirmClosed() {
        Map<String, Object> row = new HashMap<>();
        row.put(MasterData.COL_STATUS, "Closed");

        Incident result = mapper.mapFromStatusRow(row);

        // Verifica se a regra de negócio de fecho foi aplicada
        assertEquals(MasterData.STATUS_CONFIRM_CLOSED, result.status, "Closed deve virar Confirm_Closed");
    }

    @Test
    void testStatusStaysInProcess() {
        // Como removeste a lógica complexa de datas para o In Process,
        // este teste garante que o status "In Process" do Excel original é mantido.
        Map<String, Object> row = new HashMap<>();
        row.put(MasterData.COL_STATUS, "In Process");

        Incident result = mapper.mapFromStatusRow(row);

        assertEquals("In Process", result.status, "Status In Process deve ser mantido como está");
    }
}
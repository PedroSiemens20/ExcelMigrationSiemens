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
        // Configurar um intervalo de datas para o teste
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        Date start = sdf.parse("15/12/2025");
        Date end = sdf.parse("21/12/2025");
        config = new AppConfig(start, end, "Sheet1");
        mapper = new IncidentMapper(config);
    }

    @Test
    void testAreNormalization() {
        // Simular linha com ARE = ORA
        Map<String, Object> row = new HashMap<>();
        row.put(MasterData.COL_ARE, "ORA");

        Incident result = mapper.mapFromStatusRow(row);

        // Verificar se virou BUZ
        assertEquals(MasterData.ARE_BUZ, result.are, "ORA deve ser convertido para BUZ");
    }

    @Test
    void testStatusClosedToConfirmClosed() {
        Map<String, Object> row = new HashMap<>();
        row.put(MasterData.COL_STATUS, "Closed");

        Incident result = mapper.mapFromStatusRow(row);

        assertEquals(MasterData.STATUS_CONFIRM_CLOSED, result.status, "Closed deve virar Confirm_Closed");
    }

    @Test
    void testInProcessFlagLogic() throws Exception {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

        Map<String, Object> row = new HashMap<>();
        row.put(MasterData.COL_CREATED, sdf.parse("16/12/2025")); // Dentro do intervalo
        row.put(MasterData.COL_CONTROL_DATE, sdf.parse("25/12/2025")); // Depois do fim (End Date é 21/12)
        row.put(MasterData.COL_STATUS, "Open");

        Incident result = mapper.mapFromStatusRow(row);

        assertEquals(MasterData.STATUS_IN_PROCESS, result.status, "Deve aplicar flag In Process se control date for futuro");
        assertEquals(result.createdOn, result.lastChangedOn, "LastChanged deve igualar CreatedOn na regra do In Process");
    }
}
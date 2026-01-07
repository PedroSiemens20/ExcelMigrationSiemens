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
        // Definimos a "Semana de Trabalho" para o teste
        Date start = sdf.parse("29/12/2025");
        Date end = sdf.parse("04/01/2026");
        config = new AppConfig(start, end, "VIM-Incidents");
        mapper = new IncidentMapper(config);
    }

    @Test
    void testCompleteWorkflow() throws Exception {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        List<Map<String, Object>> mockRows = new ArrayList<>();

        // 1. LINHA BUG: Categoria Project Bug, Data antiga (Novembro)
        Map<String, Object> rowBug = new HashMap<>();
        rowBug.put(MasterData.COL_ID, "INC44000001");
        rowBug.put(MasterData.COL_CATEGORY, "Project Bug");
        rowBug.put(MasterData.COL_CONTROL_DATE, sdf.parse("15/11/2025"));
        rowBug.put(MasterData.COL_REC_BUSINESS, "x");
        mockRows.add(rowBug);

        // 2. LINHA INCIDENTE NOVO: Data na semana, Status Closed
        Map<String, Object> rowNew = new HashMap<>();
        rowNew.put(MasterData.COL_ID, "INC44000002");
        rowNew.put(MasterData.COL_CATEGORY, "Q&A");
        rowNew.put(MasterData.COL_STATUS, "Closed");
        rowNew.put(MasterData.COL_CONTROL_DATE, sdf.parse("30/12/2025")); // Na semana
        mockRows.add(rowNew);

        // 3. LINHA FORA DA DATA: Incidente normal mas de Novembro
        Map<String, Object> rowOld = new HashMap<>();
        rowOld.put(MasterData.COL_ID, "INC44000003");
        rowOld.put(MasterData.COL_CONTROL_DATE, sdf.parse("01/11/2025"));
        mockRows.add(rowOld);

        // --- EXECUÇÃO DA LÓGICA (Simulando o MainApp) ---
        List<Incident> bugsFound = new ArrayList<>();
        List<Incident> inScope = new ArrayList<>();

        for (Map<String, Object> row : mockRows) {
            Incident inc = mapper.mapFromStatusRow(row);

            if (BugProcessor.isBug(inc.category)) {
                bugsFound.add(inc);
            }

            Date d = ExcelUtils.extractDate(inc.lastChangedOn);
            if (d != null && !d.before(config.startDate) && !d.after(config.endDate)) {
                inScope.add(inc);
            }
        }

        // --- VERIFICAÇÕES ---

        // O Bug deve ser encontrado mesmo com data de Novembro
        assertEquals(1, bugsFound.size(), "O Bug deve ser capturado independentemente da data");
        assertEquals("x", bugsFound.get(0).recurrentBusiness);

        // Apenas 1 incidente deve estar "na semana" (o INC44...002)
        assertEquals(1, inScope.size(), "Apenas incidentes na semana devem ser processados para migração");
        assertEquals(MasterData.STATUS_CONFIRM_CLOSED, inScope.get(0).status, "Status Closed deve ser convertido");
    }
}
package com.gwj.service;
 
import static org.junit.jupiter.api.Assertions.*;
 
import com.gwj.model.domain.entities.Agendamento;
import com.gwj.model.domain.entities.Profissional;
import com.gwj.model.domain.entities.Servico;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
 
public class AgendamentoServiceTest {
 
    @Test
    public void testAgendamentoProperties() {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(55L);
        agendamento.setClienteNome("Marcos Silva");
        agendamento.setClienteTelefone("11999991111");
        
        Profissional p = new Profissional();
        p.setId(2L);
        p.setNome("Carlos");
        agendamento.setProfissional(p);
 
        Servico s = new Servico();
        s.setId(1L);
        s.setNome("Corte");
        s.setPreco(new java.math.BigDecimal("45.00"));
        s.setDuracao(30);
        agendamento.setServico(s);
 
        agendamento.setDataAgendamento(LocalDate.of(2026, 6, 15));
        agendamento.setHoraInicio(LocalTime.of(9, 0));
        agendamento.setHoraFim(LocalTime.of(9, 30));
        agendamento.setStatus("Confirmado");
 
        assertEquals(55L, agendamento.getId());
        assertEquals("Marcos Silva", agendamento.getClienteNome());
        assertEquals("11999991111", agendamento.getClienteTelefone());
        assertEquals(p, agendamento.getProfissional());
        assertEquals(s, agendamento.getServico());
        assertEquals(LocalDate.of(2026, 6, 15), agendamento.getDataAgendamento());
        assertEquals(LocalTime.of(9, 0), agendamento.getHoraInicio());
        assertEquals(LocalTime.of(9, 30), agendamento.getHoraFim());
        assertEquals("Confirmado", agendamento.getStatus());
    }

    @Test
    public void testSchemaValidation() {
        // Garante que o validador de schema aceita a nova classe Agendamento sem falhar
        com.gwj.model.domain.factory.SchemaValidator.validateAllEntities();
    }

    @Test
    public void testHorariosDisponiveis() {
        AgendamentoService service = new AgendamentoService();
        // Terça-feira (2026-06-16) - Barbearia aberta
        List<java.util.Map<String, Object>> slotsAbertos = service.getHorariosDisponiveis(1L, 2L, "2026-06-16");
        assertNotNull(slotsAbertos);
        assertFalse(slotsAbertos.isEmpty());

        // Segunda-feira (2026-06-15) - Barbearia fechada
        List<java.util.Map<String, Object>> slotsFechados = service.getHorariosDisponiveis(1L, 2L, "2026-06-15");
        assertNotNull(slotsFechados);
        assertTrue(slotsFechados.isEmpty());
    }

    @Test
    public void testHorariosNoPassadoComoIndisponiveis() {
        AgendamentoService service = new AgendamentoService();
        // Terça-feira no passado (2026-06-09)
        List<java.util.Map<String, Object>> slots = service.getHorariosDisponiveis(1L, 2L, "2026-06-09");
        assertNotNull(slots);
        assertFalse(slots.isEmpty());
        for (java.util.Map<String, Object> slot : slots) {
            assertFalse((Boolean) slot.get("disponivel"));
        }
    }

    @Test
    public void testConsultarServicoPorId() {
        IService<Servico> servicoService = ServiceRegistry.getService("Servico");
        
        // Consultar Serviço 1
        Servico f1 = new Servico();
        f1.setId(1L);
        List<Servico> r1 = servicoService.read(f1);
        if (!r1.isEmpty()) {
            assertEquals(1L, r1.get(0).getId());
        }

        // Consultar Serviço 2 se existir
        Servico f2 = new Servico();
        f2.setId(2L);
        List<Servico> r2 = servicoService.read(f2);
        if (!r2.isEmpty()) {
            assertEquals(2L, r2.get(0).getId());
            assertNotEquals("Corte de Cabelo Masculino", r2.get(0).getNome());
        }
    }
}

package com.gwj.service;

import static org.junit.jupiter.api.Assertions.*;

import com.gwj.model.domain.entities.Agenda;
import com.gwj.model.domain.entities.Cliente;
import com.gwj.model.domain.entities.Profissional;
import com.gwj.model.domain.entities.Servico;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendaServiceTest {

    @Test
    public void testAgendaProperties() {
        Agenda agenda = new Agenda();
        agenda.setId(10L);
        agenda.setNome("Corte");
        LocalDateTime agora = LocalDateTime.now();
        agenda.setData(agora);

        Cliente c = new Cliente();
        c.setNome("Carlos");
        agenda.setCliente(c);

        Profissional p = new Profissional();
        p.setNome("Marcos");
        agenda.setProfissional(p);

        Servico s = new Servico();
        s.setNome("Cabelo");
        List<Servico> lista = new ArrayList<>();
        lista.add(s);
        agenda.setListaServico(lista);

        assertEquals(10L, agenda.getId());
        assertEquals("Corte", agenda.getNome());
        assertEquals(agora, agenda.getData());
        assertEquals(c, agenda.getCliente());
        assertEquals(p, agenda.getProfissional());
        assertEquals(1, agenda.getListaServico().size());
        assertEquals(s, agenda.getListaServico().get(0));
    }
}

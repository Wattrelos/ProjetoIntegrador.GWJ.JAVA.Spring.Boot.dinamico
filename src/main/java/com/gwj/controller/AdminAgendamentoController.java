package com.gwj.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gwj.model.domain.entities.Agendamento;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

@Controller
@RequestMapping("/MRYnZpAsC9sp/agendamentos")
public class AdminAgendamentoController {

    @GetMapping({"", "/"})
    public String listar(Model model) {
        try {
            IService<Agendamento> service = ServiceRegistry.getService("Agendamento");
            List<Agendamento> agendamentos = service.read(new Agendamento());
            
            // Ordenar do mais recente para o mais antigo
            agendamentos.sort((a1, a2) -> {
                if (a1.getDataAgendamento() == null || a2.getDataAgendamento() == null)
                    return 0;
                int compData = a2.getDataAgendamento().compareTo(a1.getDataAgendamento());
                if (compData != 0)
                    return compData;
                if (a1.getHoraInicio() == null || a2.getHoraInicio() == null)
                    return 0;
                return a2.getHoraInicio().compareTo(a1.getHoraInicio());
            });

            model.addAttribute("agendamentos", agendamentos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/order/booking/listar";
    }

    @GetMapping("/cancelar/{id}")
    public String cancelar(@PathVariable("id") Long id) {
        try {
            IService<Agendamento> service = ServiceRegistry.getService("Agendamento");
            Agendamento filtro = new Agendamento();
            filtro.setId(id);
            List<Agendamento> resultados = service.read(filtro);
            if (!resultados.isEmpty()) {
                Agendamento ag = resultados.get(0);
                ag.setStatus("CANCELADO");
                service.update(ag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/MRYnZpAsC9sp/agendamentos";
    }
}

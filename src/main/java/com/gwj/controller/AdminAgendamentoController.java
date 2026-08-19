package com.gwj.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gwj.model.domain.entities.Agendamento;
import com.gwj.model.domain.entities.Profissional;
import com.gwj.model.domain.entities.Servico;
import com.gwj.model.domain.entities.GradeHorarios;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

@Controller
@RequestMapping("/MRYnZpAsC9sp/agendamentos")
public class AdminAgendamentoController {

    private void populateDropdowns(Model model) {
        try {
            IService<Profissional> profService = ServiceRegistry.getService("Profissional");
            Profissional pFiltro = new Profissional();
            pFiltro.setStatus(true);
            model.addAttribute("profissionais", profService.read(pFiltro));

            IService<Servico> servicoService = ServiceRegistry.getService("Servico");
            model.addAttribute("servicos", servicoService.read(new Servico()));

            IService<GradeHorarios> ghService = ServiceRegistry.getService("GradeHorarios");
            model.addAttribute("gradesHorarios", ghService.read(new GradeHorarios()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    @GetMapping("/novo")
    public String novo(Model model) {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("Confirmado");
        model.addAttribute("agendamento", agendamento);
        populateDropdowns(model);
        return "admin/order/booking/create";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        try {
            IService<Agendamento> service = ServiceRegistry.getService("Agendamento");
            Agendamento filtro = new Agendamento();
            filtro.setId(id);
            List<Agendamento> resultados = service.read(filtro);
            if (!resultados.isEmpty()) {
                model.addAttribute("agendamento", resultados.get(0));
                populateDropdowns(model);
                return "admin/order/booking/edit";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/MRYnZpAsC9sp/agendamentos";
    }

    @PostMapping("/salvar")
    public String salvar(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("clienteNome") String clienteNome,
            @RequestParam(value = "clienteTelefone", required = false) String clienteTelefone,
            @RequestParam("dataAgendamento") String dataAgendamentoStr,
            @RequestParam("horaInicio") String horaInicioStr,
            @RequestParam(value = "horaFim", required = false) String horaFimStr,
            @RequestParam("servicoId") Long servicoId,
            @RequestParam("profissionalId") Long profissionalId,
            @RequestParam(value = "gradeHorariosId", required = false) Long gradeHorariosId,
            @RequestParam(value = "status", defaultValue = "Confirmado") String status) {
        try {
            IService<Agendamento> service = ServiceRegistry.getService("Agendamento");

            Agendamento ag = new Agendamento();
            if (id != null && id > 0) {
                ag.setId(id);
            }
            ag.setClienteNome(clienteNome);
            ag.setClienteTelefone(clienteTelefone);
            ag.setStatus(status);

            if (dataAgendamentoStr != null && !dataAgendamentoStr.isBlank()) {
                ag.setDataAgendamento(java.time.LocalDate.parse(dataAgendamentoStr));
            }
            if (horaInicioStr != null && !horaInicioStr.isBlank()) {
                ag.setHoraInicio(java.time.LocalTime.parse(horaInicioStr));
            }
            if (horaFimStr != null && !horaFimStr.isBlank()) {
                ag.setHoraFim(java.time.LocalTime.parse(horaFimStr));
            }

            if (servicoId != null && servicoId > 0) {
                Servico serv = new Servico();
                serv.setId(servicoId);
                ag.setServico(serv);
                // Se horaFim não foi informada, calcula automaticamente com a duracao do servico
                if (ag.getHoraFim() == null && ag.getHoraInicio() != null) {
                    try {
                        IService<Servico> servService = ServiceRegistry.getService("Servico");
                        List<Servico> servList = servService.read(serv);
                        if (!servList.isEmpty()) {
                            ag.setHoraFim(ag.getHoraInicio().plusMinutes(servList.get(0).getDuracao()));
                        }
                    } catch (Exception ignored) {}
                }
            }

            if (profissionalId != null && profissionalId > 0) {
                Profissional prof = new Profissional();
                prof.setId(profissionalId);
                ag.setProfissional(prof);
            }

            if (gradeHorariosId != null && gradeHorariosId > 0) {
                GradeHorarios gh = new GradeHorarios();
                gh.setId(gradeHorariosId);
                ag.setGradeHorarios(gh);
            }

            if (ag.getId() == null || ag.getId() <= 0) {
                service.create(ag);
            } else {
                service.update(ag);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/MRYnZpAsC9sp/agendamentos";
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

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id) {
        try {
            IService<Agendamento> service = ServiceRegistry.getService("Agendamento");
            Agendamento filtro = new Agendamento();
            filtro.setId(id);
            service.delete(filtro);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/MRYnZpAsC9sp/agendamentos";
    }
}

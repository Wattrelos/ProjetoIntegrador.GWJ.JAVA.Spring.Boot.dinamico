package com.gwj.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.gwj.model.domain.entities.Servico;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

@Controller
@RequestMapping("/MRYnZpAsC9sp/servicos")
public class AdminServicoController {

    @GetMapping({"", "/"})
    public String listar(Model model) {
        IService<Servico> service = ServiceRegistry.getService("Servico");
        List<Servico> servicos = service.read(new Servico());
        model.addAttribute("servicos", servicos);
        return "admin/catalog/service/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Servico servico = new Servico();
        servico.setAtivo(true);
        servico.setDuracao(30);
        model.addAttribute("servico", servico);
        return "admin/catalog/service/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        IService<Servico> service = ServiceRegistry.getService("Servico");
        Servico filtro = new Servico();
        filtro.setId(id);
        List<Servico> resultados = service.read(filtro);
        if (!resultados.isEmpty()) {
            model.addAttribute("servico", resultados.get(0));
            return "admin/catalog/service/form";
        }
        return "redirect:/MRYnZpAsC9sp/servicos";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Servico servico) {
        IService<Servico> service = ServiceRegistry.getService("Servico");
        if (servico.getId() == null || servico.getId() == 0) {
            service.create(servico);
        } else {
            service.update(servico);
        }
        return "redirect:/MRYnZpAsC9sp/servicos";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id) {
        IService<Servico> service = ServiceRegistry.getService("Servico");
        Servico filtro = new Servico();
        filtro.setId(id);
        service.delete(filtro);
        return "redirect:/MRYnZpAsC9sp/servicos";
    }
}

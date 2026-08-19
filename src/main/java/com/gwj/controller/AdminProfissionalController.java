package com.gwj.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.gwj.model.domain.entities.Profissional;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

@Controller
@RequestMapping("/MRYnZpAsC9sp/profissionais")
public class AdminProfissionalController {

    @GetMapping({"", "/"})
    public String listar(Model model) {
        IService<Profissional> service = ServiceRegistry.getService("Profissional");
        List<Profissional> profissionais = service.read(new Profissional());
        model.addAttribute("profissionais", profissionais);
        return "admin/staff/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Profissional prof = new Profissional();
        prof.setStatus(true);
        model.addAttribute("profissional", prof);
        return "admin/staff/create";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        IService<Profissional> service = ServiceRegistry.getService("Profissional");
        Profissional filtro = new Profissional();
        filtro.setId(id);
        List<Profissional> resultados = service.read(filtro);
        if (!resultados.isEmpty()) {
            model.addAttribute("profissional", resultados.get(0));
            return "admin/staff/edit";
        }
        return "redirect:/MRYnZpAsC9sp/profissionais";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Profissional prof) {
        IService<Profissional> service = ServiceRegistry.getService("Profissional");
        if (prof.getId() == null || prof.getId() == 0) {
            service.create(prof);
        } else {
            service.update(prof);
        }
        return "redirect:/MRYnZpAsC9sp/profissionais";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id) {
        IService<Profissional> service = ServiceRegistry.getService("Profissional");
        Profissional filtro = new Profissional();
        filtro.setId(id);
        service.delete(filtro);
        return "redirect:/MRYnZpAsC9sp/profissionais";
    }
}

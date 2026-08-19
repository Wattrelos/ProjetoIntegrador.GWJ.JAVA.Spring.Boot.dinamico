package com.gwj.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.gwj.model.domain.entities.Cliente;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

@Controller
@RequestMapping("/MRYnZpAsC9sp/clientes")
public class AdminClienteController {

    @GetMapping({"", "/"})
    public String listar(Model model) {
        IService<Cliente> service = ServiceRegistry.getService("Cliente");
        List<Cliente> clientes = service.read(new Cliente());
        model.addAttribute("clientes", clientes);
        return "admin/customer/customer/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Cliente cliente = new Cliente();
        model.addAttribute("cliente", cliente);
        return "admin/customer/customer/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        IService<Cliente> service = ServiceRegistry.getService("Cliente");
        Cliente filtro = new Cliente();
        filtro.setId(id);
        List<Cliente> resultados = service.read(filtro);
        if (!resultados.isEmpty()) {
            model.addAttribute("cliente", resultados.get(0));
            return "admin/customer/customer/form";
        }
        return "redirect:/MRYnZpAsC9sp/clientes";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Cliente cliente) {
        IService<Cliente> service = ServiceRegistry.getService("Cliente");
        if (cliente.getId() == null || cliente.getId() == 0) {
            cliente.setNomeUsuario(cliente.getEmail());
            service.create(cliente);
        } else {
            service.update(cliente);
        }
        return "redirect:/MRYnZpAsC9sp/clientes";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id) {
        IService<Cliente> service = ServiceRegistry.getService("Cliente");
        Cliente filtro = new Cliente();
        filtro.setId(id);
        service.delete(filtro);
        return "redirect:/MRYnZpAsC9sp/clientes";
    }
}

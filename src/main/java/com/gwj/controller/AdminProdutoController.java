package com.gwj.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.gwj.model.domain.entities.Produto;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

@Controller
@RequestMapping("/MRYnZpAsC9sp/produtos")
public class AdminProdutoController {

    @GetMapping({"", "/"})
    public String listar(Model model) {
        IService<Produto> service = ServiceRegistry.getService("Produto");
        List<Produto> produtos = service.read(new Produto());
        model.addAttribute("produtos", produtos);
        return "admin/catalog/product/listar";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Produto produto = new Produto();
        model.addAttribute("produto", produto);
        return "admin/catalog/product/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model) {
        IService<Produto> service = ServiceRegistry.getService("Produto");
        Produto filtro = new Produto();
        filtro.setId(id);
        List<Produto> resultados = service.read(filtro);
        if (!resultados.isEmpty()) {
            model.addAttribute("produto", resultados.get(0));
            return "admin/catalog/product/form";
        }
        return "redirect:/MRYnZpAsC9sp/produtos";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Produto produto) {
        IService<Produto> service = ServiceRegistry.getService("Produto");
        if (produto.getId() == null || produto.getId() == 0) {
            service.create(produto);
        } else {
            service.update(produto);
        }
        return "redirect:/MRYnZpAsC9sp/produtos";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id) {
        IService<Produto> service = ServiceRegistry.getService("Produto");
        Produto filtro = new Produto();
        filtro.setId(id);
        service.delete(filtro);
        return "redirect:/MRYnZpAsC9sp/produtos";
    }
}

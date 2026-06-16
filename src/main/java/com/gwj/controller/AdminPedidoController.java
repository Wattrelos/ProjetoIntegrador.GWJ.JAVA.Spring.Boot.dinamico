package com.gwj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.gwj.model.domain.entities.Pedido;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;
import java.util.List;

@Controller
public class AdminPedidoController {

    @GetMapping("/MRYnZpAsC9sp/pedidos")
    public String listarPedidos(Model model) {
        try {
            IService<Pedido> service = ServiceRegistry.getService("Pedido");
            List<Pedido> resultados = service.read(new Pedido());
            
            // Ordenar por ID decrescente para mostrar os pedidos mais recentes no topo
            resultados.sort((p1, p2) -> p2.getId().compareTo(p1.getId()));
            
            model.addAttribute("pedidos", resultados);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/pedidos/listar";
    }

    @GetMapping("/MRYnZpAsC9sp/pedidos/detalhe")
    public String detalhePedido(@RequestParam("id") Long id, Model model) {
        try {
            IService<Pedido> service = ServiceRegistry.getService("Pedido");
            Pedido filtro = new Pedido();
            filtro.setId(id);
            List<Pedido> resultados = service.read(filtro);
            if (!resultados.isEmpty()) {
                model.addAttribute("pedido", resultados.get(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "admin/pedidos/detalhe";
    }

    @PostMapping("/MRYnZpAsC9sp/pedidos/atualizar-status")
    public String atualizarStatus(
            @RequestParam("id") Long id,
            @RequestParam("status") String status) {
        try {
            IService<Pedido> service = ServiceRegistry.getService("Pedido");
            Pedido filtro = new Pedido();
            filtro.setId(id);
            List<Pedido> resultados = service.read(filtro);
            if (!resultados.isEmpty()) {
                Pedido pedido = resultados.get(0);
                pedido.setStatus(status);
                service.update(pedido);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/MRYnZpAsC9sp/pedidos/detalhe?id=" + id;
    }
}

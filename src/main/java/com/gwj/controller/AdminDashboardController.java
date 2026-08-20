package com.gwj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gwj.model.domain.entities.Agendamento;
import com.gwj.model.domain.entities.Cliente;
import com.gwj.model.domain.entities.Pedido;
import com.gwj.model.domain.entities.Produto;
import com.gwj.model.domain.entities.Profissional;
import com.gwj.model.domain.entities.Servico;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

@Controller
public class AdminDashboardController {

    @GetMapping({"/MRYnZpAsC9sp", "/MRYnZpAsC9sp/"})
    public String dashboard(Model model) {
        try {
            IService<Agendamento> agService = ServiceRegistry.getService("Agendamento");
            model.addAttribute("totalAgendamentos", agService.read(new Agendamento()).size());
        } catch (Exception e) {
            model.addAttribute("totalAgendamentos", 0);
        }

        try {
            IService<Pedido> pedidoService = ServiceRegistry.getService("Pedido");
            model.addAttribute("totalPedidos", pedidoService.read(new Pedido()).size());
        } catch (Exception e) {
            model.addAttribute("totalPedidos", 0);
        }

        try {
            IService<Cliente> clienteService = ServiceRegistry.getService("Cliente");
            model.addAttribute("totalClientes", clienteService.read(new Cliente()).size());
        } catch (Exception e) {
            model.addAttribute("totalClientes", 0);
        }

        try {
            IService<Produto> prodService = ServiceRegistry.getService("Produto");
            model.addAttribute("totalProdutos", prodService.read(new Produto()).size());
        } catch (Exception e) {
            model.addAttribute("totalProdutos", 0);
        }

        try {
            IService<Servico> servService = ServiceRegistry.getService("Servico");
            model.addAttribute("totalServicos", servService.read(new Servico()).size());
        } catch (Exception e) {
            model.addAttribute("totalServicos", 0);
        }

        try {
            IService<Profissional> profService = ServiceRegistry.getService("Profissional");
            model.addAttribute("totalProfissionais", profService.read(new Profissional()).size());
        } catch (Exception e) {
            model.addAttribute("totalProfissionais", 0);
        }

        return "admin/dashboard/index";
    }
}


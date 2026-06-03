package com.gwj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller

public class Router {
    @GetMapping("/servicos")
    public String servico() {
        return "servicos";
    }

    @GetMapping("/sobre")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/contato")
    public String contato() {
        return "contato";
    }
    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }
    @GetMapping({"/home","/"})
    public String home() {
        return "home";
    }
    @GetMapping("order-confirmation")
    public String orderConfirmation() {
        return "order-confirmation";

    }
    

    @GetMapping("/login")
    public String login() {
        return "login";
    
    }
    
}

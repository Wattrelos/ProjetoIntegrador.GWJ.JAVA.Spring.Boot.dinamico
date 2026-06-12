package com.gwj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.gwj.model.dataTransferObject.EntityMapper;
import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.factory.SimpleObjectFactory;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class Router {

    @GetMapping({"/home","/"})
    public String home(Model model) {
        IEntity servicoBase = SimpleObjectFactory.create("Servico");
        IService<IEntity> service = ServiceRegistry.getService("Servico");
        List<IEntity> servicos = service.read(servicoBase);
        model.addAttribute("servicos", servicos);
        return "home";
    }

    @GetMapping("/servicos")
    public String servico(Model model) {
        IEntity servicoBase = SimpleObjectFactory.create("Servico");
        IService<IEntity> servicoService = ServiceRegistry.getService("Servico");
        List<IEntity> servicos = servicoService.read(servicoBase);
        model.addAttribute("servicos", servicos);

        IEntity profissionalBase = SimpleObjectFactory.create("Profissional");
        IService<IEntity> profService = ServiceRegistry.getService("Profissional");
        List<IEntity> profissionais = profService.read(profissionalBase);
        model.addAttribute("profissionais", profissionais);

        return "servicos";
    }

    @GetMapping("/sobre-nos")
    public String sobre() {
        return "sobre";
    }

    @GetMapping("/contato")
    public String contato() {
        return "contato";
    }

    @GetMapping({"/cart","/carrinho"})
    public String carrinho(){
        return "carrinho";
    }    

    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    @GetMapping("order-confirmation")
    public String orderConfirmation() {
        return "order-confirmation";
    }
    
    @GetMapping("loja")
    public String shop(Model model) {
        IEntity produtoBase = SimpleObjectFactory.create("Produto");
        IService<IEntity> service = ServiceRegistry.getService("Produto");
        List<IEntity> produtos = service.read(produtoBase);
        model.addAttribute("produtos", produtos);
        return "loja";
    }

    @GetMapping("/single-product")
    public String singleProduct(HttpServletRequest request, Model model) {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            IEntity produtoBase = SimpleObjectFactory.create("Produto");
            IEntity filtro = EntityMapper.fillEntity(produtoBase, request);
            IService<IEntity> service = ServiceRegistry.getService("Produto");
            List<IEntity> resultados = service.read(filtro);
            if (!resultados.isEmpty()) {
                model.addAttribute("produto", resultados.get(0));
            }
        }
        return "single-product";
    }   
}

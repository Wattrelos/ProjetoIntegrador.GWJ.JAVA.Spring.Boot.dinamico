package com.gwj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.gwj.model.dataAccessObject.DataAccessObject;
import com.gwj.model.dataTransferObject.EntityMapper;
import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.factory.SimpleObjectFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;


@Controller
public class Router {

    private final DataAccessObject dao = new DataAccessObject();

    @GetMapping({"/home","/"})
    public String home() {
        return "home";
    }

    @GetMapping("/servicos")
    public String servico() {
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
        List<IEntity> produtos = dao.read(produtoBase);
        model.addAttribute("produtos", produtos);
        return "loja";
    }


    @GetMapping("/single-product")
    public String singleProduct(HttpServletRequest request, Model model) {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isBlank()) {
            IEntity produtoBase = SimpleObjectFactory.create("Produto");
            IEntity filtro = EntityMapper.fillEntity(produtoBase, request);
            List<IEntity> resultados = dao.read(filtro);
            if (!resultados.isEmpty()) {
                model.addAttribute("produto", resultados.get(0));
            }
        }
        return "single-product";
    }   

    
}

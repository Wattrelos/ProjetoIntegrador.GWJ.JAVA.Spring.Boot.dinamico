package com.gwj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import com.gwj.model.domain.Carrinho;
import com.gwj.model.domain.entities.Produto;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gwj.model.domain.entities.Pedido;
import com.gwj.model.domain.entities.ItemPedido;
import com.gwj.model.domain.entities.Cliente;
import com.gwj.model.domain.entities.Usuario;

@Controller
public class CarrinhoController {

    @Autowired
    private HttpSession session;

    @GetMapping("/carrinho/mini-cart-fragment")
    public String obterMiniCartFragment() {
        return "parts/header :: #miniCartItems";
    }

    private Carrinho getCarrinhoFromSession() {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

    @PostMapping("/carrinho/adicionar")
    @ResponseBody
    public Map<String, Object> adicionar(
            @RequestParam("produtoId") Long produtoId,
            @RequestParam(value = "quantidade", defaultValue = "1") int quantidade) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            IService<Produto> service = ServiceRegistry.getService("Produto");
            Produto filtro = new Produto();
            filtro.setId(produtoId);
            List<Produto> resultados = service.read(filtro);
            
            if (!resultados.isEmpty()) {
                Produto produto = resultados.get(0);
                Carrinho carrinho = getCarrinhoFromSession();
                carrinho.adicionarItem(produto, quantidade);
                
                response.put("sucesso", true);
                response.put("mensagem", "Produto adicionado ao carrinho!");
                response.put("quantidadeTotal", carrinho.getQuantidadeTotal());
                response.put("valorTotal", carrinho.getValorTotal());
            } else {
                response.put("sucesso", false);
                response.put("mensagem", "Produto não encontrado.");
            }
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", "Erro ao adicionar produto: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/carrinho/atualizar")
    @ResponseBody
    public Map<String, Object> atualizar(
            @RequestParam("produtoId") Long produtoId,
            @RequestParam("quantidade") int quantidade) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            Carrinho carrinho = getCarrinhoFromSession();
            carrinho.atualizarQuantidade(produtoId, quantidade);
            
            response.put("sucesso", true);
            response.put("quantidadeTotal", carrinho.getQuantidadeTotal());
            response.put("valorTotal", carrinho.getValorTotal());
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
        }
        return response;
    }

    @PostMapping("/carrinho/remover")
    @ResponseBody
    public Map<String, Object> remover(@RequestParam("produtoId") Long produtoId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Carrinho carrinho = getCarrinhoFromSession();
            carrinho.removerItem(produtoId);
            
            response.put("sucesso", true);
            response.put("quantidadeTotal", carrinho.getQuantidadeTotal());
            response.put("valorTotal", carrinho.getValorTotal());
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
        }
        return response;
    }

    @PostMapping("/carrinho/limpar")
    @ResponseBody
    public Map<String, Object> limpar() {
        Map<String, Object> response = new HashMap<>();
        try {
            Carrinho carrinho = getCarrinhoFromSession();
            carrinho.limpar();
            response.put("sucesso", true);
            response.put("quantidadeTotal", 0);
            response.put("valorTotal", 0);
        } catch (Exception e) {
            response.put("sucesso", false);
            response.put("mensagem", e.getMessage());
        }
        return response;
    }

    @GetMapping("/carrinho/checkout")
    public String checkout(org.springframework.ui.Model model) {
        Carrinho carrinho = getCarrinhoFromSession();
        if (carrinho.getQuantidadeTotal() == 0) {
            return "redirect:/loja";
        }
        
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado != null) {
            try {
                IService<Cliente> clienteService = ServiceRegistry.getService("Cliente");
                Cliente cFiltro = new Cliente();
                cFiltro.setId(usuarioLogado.getId());
                List<Cliente> clientes = clienteService.read(cFiltro);
                if (!clientes.isEmpty()) {
                    model.addAttribute("clienteLogado", clientes.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "carrinho-checkout";
    }

    @PostMapping("/carrinho/checkout/confirmar")
    public String confirmar(
            @RequestParam(value = "nomeVisitante", required = false) String nomeVisitante,
            @RequestParam(value = "telefoneVisitante", required = false) String telefoneVisitante,
            @RequestParam("formaPagamento") String formaPagamento) {
        
        Carrinho carrinho = getCarrinhoFromSession();
        if (carrinho.getQuantidadeTotal() == 0) {
            return "redirect:/loja";
        }

        try {
            Pedido pedido = new Pedido();
            pedido.setFormaPagamento(formaPagamento);
            pedido.setStatus("Aguardando Retirada");
            pedido.setDataPedido(java.time.LocalDateTime.now());

            Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
            if (usuarioLogado != null) {
                IService<Cliente> clienteService = ServiceRegistry.getService("Cliente");
                Cliente cFiltro = new Cliente();
                cFiltro.setId(usuarioLogado.getId());
                List<Cliente> clientes = clienteService.read(cFiltro);
                if (!clientes.isEmpty()) {
                    pedido.setCliente(clientes.get(0));
                }
            } else {
                pedido.setNomeVisitante(nomeVisitante);
                pedido.setTelefoneVisitante(telefoneVisitante);
            }

            for (com.gwj.model.domain.CarrinhoItem itemCart : carrinho.getListaItens()) {
                ItemPedido itemPedido = new ItemPedido(itemCart.getProduto(), itemCart.getQuantidade());
                pedido.adicionarItem(itemPedido);
            }

            pedido.calcularValorTotal();

            IService<Pedido> pedidoService = ServiceRegistry.getService("Pedido");
            Pedido pedidoSalvo = pedidoService.create(pedido);

            carrinho.limpar();

            return "redirect:/compra-confirmada?id=" + pedidoSalvo.getId();
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/carrinho/checkout?erro=" + e.getMessage();
        }
    }
}

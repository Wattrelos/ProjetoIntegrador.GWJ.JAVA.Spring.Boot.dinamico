package com.gwj.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.gwj.service.ServiceRegistry;
import com.gwj.service.IService;
import com.gwj.model.domain.entities.Setting;
import com.gwj.model.domain.Carrinho;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalAttributesAdvice {

    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;

    @ModelAttribute("requestURI")
    public String getRequestURI() {
        return request.getRequestURI();
    }

    @ModelAttribute("carrinho")
    public Carrinho getCarrinho() {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

    @ModelAttribute("empresa")
    public Map<String, String> getEmpresaConfig() {
        Map<String, String> configMap = new HashMap<>();
        
        // Valores padrão fallback caso o banco esteja vazio ou falhe
        configMap.put("nome", "Tgo's Barbearia");
        configMap.put("descricao", "Tradição e estilo para o homem moderno. Cuidamos do seu visual com excelência, usando as melhores técnicas e produtos para você se sentir incrível.");
        configMap.put("endereco_curto", "Rua Principal, 123 - Centro");
        configMap.put("endereco_completo", "Rua Principal, 123, Centro\nCidade, Estado - CEP 12345-678");
        configMap.put("telefone", "(11) 99999-9999");
        configMap.put("email", "contato@tgosbarbearia.com");
        configMap.put("horarios", "Segunda a Sábado\nDas 09:00 às 20:00");
        configMap.put("sobre_titulo", "Tradição e Estilo");
        configMap.put("sobre_texto1", "Fundada com o propósito de resgatar a clássica experiência de ir ao barbeiro, nossa Barbearia une o ambiente nostálgico com as mais modernas técnicas de visagismo masculino.");
        configMap.put("sobre_texto2", "Acreditamos que um bom corte de cabelo e uma barba bem feita são fundamentais para a autoestima do homem contemporâneo. Aqui, cada cliente é tratado como um amigo. Sente-se, tome um café ou uma cerveja gelada e deixe o visual por nossa conta.");
        configMap.put("sobre_imagem", "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=800&q=80");
        configMap.put("logo_alt", "Fatec-FV");
        configMap.put("logo_url", "/img/logo.png");

        try {
            IService<Setting> service = ServiceRegistry.getService("Setting");
            List<Setting> settings = service.read(new Setting());
            for (Setting s : settings) {
                if (s.getChave() != null && s.getValor() != null) {
                    configMap.put(s.getChave(), s.getValor());
                }
            }
        } catch (Exception e) {
            // Ignora e usa os valores padrão
            System.err.println("Aviso: Falha ao carregar configurações da empresa do banco de dados. Usando fallbacks. " + e.getMessage());
        }

        return configMap;
    }
}

package com.gwj.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.gwj.model.domain.entities.Usuario;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        // Se a sessão não existir ou não tiver o usuário logado, barra o acesso
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false; // Interrompe o fluxo (não chega no Controller)
        }
        
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // O Administrador (Perfil 1) sempre tem acesso total a tudo
        if (usuarioLogado.getPerfil() != null && usuarioLogado.getPerfil().getId() == 1L) {
            return true; 
        }

        // O Spring Boot salva as variáveis de rota (como o {entity}) neste atributo especial
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // Verifica se a URL acessada exigia uma entidade específica (como nas rotas /listar, /create, /editar)
        if (pathVariables != null && pathVariables.containsKey("entity")) {
            String entidade = pathVariables.get("entity");
            
            // Mapeia qual permissão é necessária para a entidade acessada
            String permissaoNecessaria = "ADMIN_ONLY"; // Bloqueio padrão para classes não mapeadas
            if ("Cliente".equalsIgnoreCase(entidade)) permissaoNecessaria = "GERENCIAR_CLIENTES";
            else if ("Servico".equalsIgnoreCase(entidade)) permissaoNecessaria = "GERENCIAR_SERVICOS";
            else if ("Agenda".equalsIgnoreCase(entidade)) permissaoNecessaria = "AGENDAR_HORARIO";

            // Se o usuário não possuir a permissão estipulada, barra e redireciona ao painel principal
            if (!usuarioLogado.hasPermissao(permissaoNecessaria)) {
                
                // Cria uma mensagem Flash (disponível apenas na próxima requisição e some ao atualizar a página)
                FlashMap flashMap = new FlashMap();
                flashMap.put("mensagemErro", "Acesso Negado: Você não possui permissão para gerenciar " + entidade + ".");
                FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
                if (flashMapManager != null) {
                    flashMapManager.saveOutputFlashMap(flashMap, request, response);
                }

                response.sendRedirect(request.getContextPath() + "/MRYnZpAsC9sp");
                return false; // Interrompe a requisição (O Controller nem chega a ser acionado)
            }
        }

        return true; // Usuário logado, permite continuar o acesso
    }
}

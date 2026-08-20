package com.gwj.controller;

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
            response.sendRedirect(request.getContextPath() + "/MRYnZpAsC9sp/login");
            return false; // Interrompe o fluxo (não chega no Controller)
        }
        
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // Bloqueia clientes (Perfil 4) de acessarem qualquer rota do painel administrativo
        if (usuarioLogado.getPerfil() != null && usuarioLogado.getPerfil().getId() == 4L) {
            response.sendRedirect(request.getContextPath() + "/");
            return false;
        }

        // O Administrador (Perfil 1) sempre tem acesso total a tudo
        if (usuarioLogado.getPerfil() != null && usuarioLogado.getPerfil().getId() == 1L) {
            return true; 
        }

        // Identifica a permissão necessária com base na URL do módulo acessado
        String uri = request.getRequestURI();
        String modulo = null;
        String permissaoNecessaria = null;

        if (uri.contains("/MRYnZpAsC9sp/clientes")) {
            modulo = "Clientes";
            permissaoNecessaria = "GERENCIAR_CLIENTES";
        } else if (uri.contains("/MRYnZpAsC9sp/servicos")) {
            modulo = "Serviços";
            permissaoNecessaria = "GERENCIAR_SERVICOS";
        } else if (uri.contains("/MRYnZpAsC9sp/produtos")) {
            modulo = "Produtos / Estoque";
            permissaoNecessaria = "GERENCIAR_ESTOQUE";
        } else if (uri.contains("/MRYnZpAsC9sp/agendamentos")) {
            modulo = "Agendamentos";
            // Permite quem tem permissão de gerenciar agendas ou agendar horário
            if (!usuarioLogado.hasPermissao("GERENCIAR_TODAS_AGENDAS") 
                    && !usuarioLogado.hasPermissao("AGENDAR_HORARIO")
                    && !usuarioLogado.hasPermissao("VISUALIZAR_PROPRIA_AGENDA")) {
                permissaoNecessaria = "GERENCIAR_TODAS_AGENDAS";
            }
        } else if (uri.contains("/MRYnZpAsC9sp/profissionais") || uri.contains("/MRYnZpAsC9sp/configuracoes")) {
            modulo = "Administração / Configurações";
            permissaoNecessaria = "ADMIN_ONLY";
        }

        // Se uma permissão específica for requerida e o usuário não a possuir, bloqueia o acesso
        if (permissaoNecessaria != null && !usuarioLogado.hasPermissao(permissaoNecessaria)) {
            FlashMap flashMap = new FlashMap();
            flashMap.put("mensagemErro", "Acesso Negado: Você não possui permissão para gerenciar " + (modulo != null ? modulo : "este módulo") + ".");
            FlashMapManager flashMapManager = RequestContextUtils.getFlashMapManager(request);
            if (flashMapManager != null) {
                flashMapManager.saveOutputFlashMap(flashMap, request, response);
            }

            response.sendRedirect(request.getContextPath() + "/MRYnZpAsC9sp");
            return false;
        }

        return true; // Usuário logado e autorizado
    }
}

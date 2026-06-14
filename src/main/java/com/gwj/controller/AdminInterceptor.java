package com.gwj.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.gwj.model.domain.entities.Usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        // Verifica se a sessão é nula ou se não existe o atributo 'usuarioLogado' nela
        if (session == null || session.getAttribute("usuarioLogado") == null) {
            // Se for uma requisição para a API (JSON), retorna erro 401 (Não Autorizado) em vez de redirecionar
            if (request.getRequestURI().contains("-json")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acesso negado. Por favor, faça login.");
            } else {
                // Se for navegação normal no painel /MRYnZpAsC9sp, redireciona o usuário para a página de login
                response.sendRedirect("/login");
            }
            return false; // Interrompe a requisição e não deixa chegar no Controller
        }
        
        // Verifica se o usuário logado possui perfil de Cliente (ID 4)
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario.getPerfil() != null && usuario.getPerfil().getId() == 4L) {
            if (request.getRequestURI().contains("-json")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado. Permissão insuficiente.");
            } else {
                // Clientes são redirecionados para a Home pública
                response.sendRedirect("/");
            }
            return false; // Bloqueia o acesso
        }
        
        return true; // Sessão válida e perfil autorizado, permite que a requisição continue normalmente
    }
}

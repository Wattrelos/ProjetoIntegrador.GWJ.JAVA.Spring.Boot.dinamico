package com.gwj.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.entities.Usuario;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;
import com.gwj.controller.PasswordUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        // Se a sessão existir e houver um usuário logado nela, redireciona imediatamente
        if (session != null && session.getAttribute("usuarioLogado") != null) {
            return "redirect:/MRYnZpAsC9sp/listar/Cliente";
        }
        return "login"; // Deve existir um arquivo login.html na pasta templates
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email, 
                               @RequestParam("senha") String senha, 
                               HttpServletRequest request, 
                               Model model) {
        
        Usuario filtro = new Usuario();
        filtro.setEmail(email);
        
        IService<Usuario> service = ServiceRegistry.getService("Usuario");
        // Retorna a lista de usuários baseada no email (o Serviço carrega perfil e permissões automaticamente)
        List<Usuario> resultados = service.read(filtro);
        
        // Aplica o hash na senha fornecida pelo formulário
        String senhaCriptografada = "{sha256}" + PasswordUtil.hash(senha);

        for (Usuario usuarioBanco : resultados) {
            boolean senhaValida = false;
            
            // Verifica se a senha no banco já está no novo formato criptografado
            if (usuarioBanco.getSenha() != null && usuarioBanco.getSenha().startsWith("{sha256}")) {
                senhaValida = usuarioBanco.getSenha().equals(senhaCriptografada);
            } else if (usuarioBanco.getSenha() != null) {
                // Retrocompatibilidade: Permite logar com senhas antigas que ainda estão em texto puro
                senhaValida = usuarioBanco.getSenha().equals(senha);
            }

            // Validação exata no Java para contornar o "LIKE" do DAO/Repository
            if (usuarioBanco.getEmail().equalsIgnoreCase(email) && senhaValida) {
                
                // Login bem-sucedido! Guardamos o objeto completo na sessão
                HttpSession session = request.getSession();
                session.setAttribute("usuarioLogado", usuarioBanco);
                
                return "redirect:/MRYnZpAsC9sp/listar/Cliente"; // Redireciona para o painel principal
            }
        }
        
        // Se o loop terminar sem sucesso, a senha ou e-mail estão incorretos
        model.addAttribute("erro", "E-mail ou senha inválidos.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Invalida a sessão HTTP, removendo o usuarioLogado
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}

package com.gwj.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura o erro 404 (Página não encontrada)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFoundError(NoHandlerFoundException ex, HttpServletRequest request, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Página Não Encontrada");
        model.addAttribute("message", "Desculpe, a página que você está procurando não existe ou foi movida.");
        model.addAttribute("path", request.getRequestURI());
        
        return "error"; // Renderiza templates/error.html
    }

    // Captura o erro 404 específico para arquivos/recursos estáticos não encontrados (Spring 3.2+)
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundError(NoResourceFoundException ex, HttpServletRequest request, Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Recurso Não Encontrado");
        model.addAttribute("message", "O recurso (CSS, JS, Imagem, etc.) que o navegador tentou carregar não existe.");
        model.addAttribute("path", request.getRequestURI());
        
        return "error";
    }

    // Captura qualquer outra exceção genérica (Erro 500)
    @ExceptionHandler(Exception.class)
    public String handleGlobalError(Exception ex, HttpServletRequest request, Model model) {
        // Aqui você poderia adicionar um logger, ex: logger.error("Erro capturado: ", ex);
        System.err.println("Erro global capturado em " + request.getRequestURI() + ": " + ex.getMessage());
        ex.printStackTrace(); // Imprime o stacktrace no console do servidor para debug
        
        model.addAttribute("status", 500);
        model.addAttribute("error", "Erro Interno do Servidor");
        model.addAttribute("message", "Ocorreu um erro inesperado em nossos servidores. Nossa equipe foi notificada.");
        model.addAttribute("path", request.getRequestURI());
        
        return "error"; // Renderiza templates/error.html
    }
}
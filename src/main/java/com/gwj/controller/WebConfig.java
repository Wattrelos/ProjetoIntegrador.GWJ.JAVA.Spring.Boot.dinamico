package com.gwj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gwj.controller.AdminInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Registra o AdminInterceptor
        registry.addInterceptor(adminInterceptor)
                // Aplica a proteção em todas as rotas que começam com /MRYnZpAsC9sp/ (o ** significa "qualquer sub-caminho")
                .addPathPatterns("/MRYnZpAsC9sp/**")
                .addPathPatterns("/MRYnZpAsC9sp") // Protege a raiz do admin também
                .addPathPatterns("/create-json") // Protege a API de Criação
                .addPathPatterns("/update-json") // Protege a API de Edição
                .addPathPatterns("/delete-json"); // Protege a API de Exclusão
    }
}

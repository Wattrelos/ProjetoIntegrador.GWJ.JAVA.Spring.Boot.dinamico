package com.gwj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.gwj")
public class Application {

    public static void main(String[] args) {
        // Este comando inicia o servidor Tomcat embutido e sobe sua API
        SpringApplication.run(Application.class, args);
        System.out.println("=== Servidor Spring Boot iniciado com sucesso! ===");
    }
}

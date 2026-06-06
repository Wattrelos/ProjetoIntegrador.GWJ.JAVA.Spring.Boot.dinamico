package com.gwj.controller;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.gwj.model.dataAccessObject.DataAccessObject;
import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.entities.Perfil;
import com.gwj.model.domain.entities.Usuario;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private final DataAccessObject dao = new DataAccessObject();

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o usuário já existe para não criar duplicatas a cada reinicialização
        Usuario filtro = new Usuario();
        filtro.setEmail("gabriel@admin.com"); 

        List<IEntity> usuarios = dao.read(filtro);

        if (usuarios.isEmpty()) {
            Usuario admin = new Usuario();
            admin.setNomeUsuario("Gabriel");
            admin.setEmail("gabriel@admin.com");
            admin.setSenha("KxE7ql=iAx"); // Sua senha solicitada
            
            // Utilizamos o seu próprio método para criptografar a senha antes de salvar!
            admin.criptografarSenha(); 
            admin.setStatus(true);
            
            // Relaciona com o Perfil 1 (Geralmente reservado para Administradores no banco)
            Perfil perfilAdmin = new Perfil();
            perfilAdmin.setId(1L); 
            admin.setPerfil(perfilAdmin);

            dao.create(admin);
            
            System.out.println("========================================================");
            System.out.println("👑 Superuser criado com sucesso! Use gabriel@admin.com para logar.");
            System.out.println("========================================================");
        }
    }
}
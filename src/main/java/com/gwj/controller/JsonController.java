package com.gwj.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping; // Para o @PutMapping
import org.springframework.web.bind.annotation.DeleteMapping; // Para o @DeleteMapping
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gwj.model.dataAccessObject.DataAccessObject;
import com.gwj.model.dataTransferObject.EntityMapper;
import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.factory.SimpleObjectFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List; // Para o List

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class JsonController {
    // Instancia uma única vez para este controller para economizar recursos
    private final DataAccessObject dao = new DataAccessObject();

    @PostMapping("/create-json") // Operações de escrita devem ser POST
    public ResponseEntity<?> create(@RequestParam(value = "entity", required = false) String entityName, HttpServletRequest request) {

        if (entityName != null && !entityName.isBlank()) {
            System.out.println("console.log('" + entityName + "')");
            IEntity entidade = SimpleObjectFactory.create(entityName);

            try {
                // Agora o DAO retorna a entidade populada ou lança erro
                IEntity entidadeSalva = dao.create(EntityMapper.fillEntity(entidade, request));
                
                // Retorna a entidade completa (ID incluso) como JSON
                return ResponseEntity.ok(entidadeSalva);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Erro ao salvar: " + e.getMessage());
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("<h1>Nenhuma entidade especificada!</h1>");
        }
    }

    @GetMapping("/read-json")
    public ResponseEntity<?> read(@RequestParam(value = "entity", required = false) String entityName, HttpServletRequest request) {
        
        if (entityName != null && !entityName.isBlank()) {
            // Lógica de negócio mantida
            IEntity entidade = SimpleObjectFactory.create(entityName);
            List<IEntity> listaEntity = dao.read(EntityMapper.fillEntity(entidade, request));

            // O Spring converte a lista automaticamente para JSON usando o Jackson interno
            return ResponseEntity.ok(listaEntity);
        } else {
            // Retorna HTML ou uma mensagem de erro apropriada
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("<h1>Nenhum valor recebido!</h1>");
        }
    }

    @PostMapping("/update-json") // Alterado para POST para compatibilidade total com formulários
    public ResponseEntity<?> update(HttpServletRequest request) {

        String idParam = request.getParameter("id"); // O EntityMapper.fillEntity também usará isso
        String entityName = request.getParameter("entity"); // Corrigido para 'entity'
        // 2. Tenta converter com segurança
        Long entityId = null;
        try {
            if (idParam != null && !idParam.isBlank()) {
                entityId = Long.valueOf(idParam.trim());
            }
        } catch (NumberFormatException e) {
            // ID inválido (ex: "abc")
        }
        if (entityName != null && !entityName.isBlank() && entityId != null) {
            IEntity entidade = SimpleObjectFactory.create(entityName);
            
            // O EntityMapper continua funcionando com o HttpServletRequest que o Spring injeta
            Long primaryKey = dao.update(EntityMapper.fillEntity(entidade, request));

            System.out.println("Update: Retornou de dataAccessObject.update"); // Mensagem de debug para o console
            System.out.println("Update: Long = " + primaryKey); // Mensagem de debug para o console

            return ResponseEntity.ok(primaryKey);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("<h1>Erro ao tentar atualizar! id = " + idParam+ " entityName = " + entityName + "</h1>");
        }
    }
    
    @DeleteMapping("/delete-json") // Padrão REST para exclusões
    public ResponseEntity<?> delete(@RequestParam(value = "entity", required = false) String entityName, HttpServletRequest request) {

        if (entityName != null && !entityName.isBlank()) {
            IEntity entidade = SimpleObjectFactory.create(entityName);
            
            // O EntityMapper extrai os IDs ou dados necessários do request
            Long primaryKey = dao.delete(EntityMapper.fillEntity(entidade, request));

            System.out.println("Delete: Retornou de dataAccessObject.delete");
            System.out.println("Delete: Chave primária Long = " + primaryKey + " da entidade excluída.");

            // Retornamos a PK da entidade deletada
            return ResponseEntity.ok(primaryKey);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("<h1>Nenhuma chave primária recebida!</h1>");
        }
    }
    
}

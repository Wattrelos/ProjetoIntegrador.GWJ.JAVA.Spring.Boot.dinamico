package com.gwj.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gwj.model.dataTransferObject.EntityMapper;
import com.gwj.model.dataTransferObject.dto.DtoMapper;
import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.factory.SimpleObjectFactory;
import com.gwj.service.IService;
import com.gwj.service.ServiceRegistry;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class JsonController {

    private IEntity fillAndMap(String entityName, HttpServletRequest request) {
        IEntity target = SimpleObjectFactory.create(entityName);
        if ("Usuario".equalsIgnoreCase(entityName)) {
            com.gwj.model.dataTransferObject.dto.UsuarioDTO dto = new com.gwj.model.dataTransferObject.dto.UsuarioDTO();
            EntityMapper.fillEntity(dto, request);
            return DtoMapper.toEntity(dto, target);
        } else if ("Cliente".equalsIgnoreCase(entityName)) {
            com.gwj.model.dataTransferObject.dto.ClienteDTO dto = new com.gwj.model.dataTransferObject.dto.ClienteDTO();
            EntityMapper.fillEntity(dto, request);
            return DtoMapper.toEntity(dto, target);
        }
        return EntityMapper.fillEntity(target, request);
    }

    private Object toResponseBody(Object obj) {
        if (obj instanceof IEntity entity) {
            return DtoMapper.toDto(entity);
        } else if (obj instanceof List<?> list) {
            return list.stream()
                       .map(item -> item instanceof IEntity ? DtoMapper.toDto((IEntity) item) : item)
                       .toList();
        }
        return obj;
    }

    @PostMapping("/create-json")
    public ResponseEntity<?> create(@RequestParam(value = "entity", required = false) String entityName, HttpServletRequest request) {
        if (entityName != null && !entityName.isBlank()) {
            System.out.println("console.log('" + entityName + "')");
            try {
                IEntity entidadePreenchida = fillAndMap(entityName, request);
                IService<IEntity> service = ServiceRegistry.getService(entityName);
                IEntity entidadeSalva = service.create(entidadePreenchida);
                
                return ResponseEntity.ok(toResponseBody(entidadeSalva));
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
            try {
                IEntity filtro = fillAndMap(entityName, request);
                IService<IEntity> service = ServiceRegistry.getService(entityName);
                List<IEntity> listaEntity = service.read(filtro);

                return ResponseEntity.ok(toResponseBody(listaEntity));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Erro ao buscar: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("<h1>Nenhum valor recebido!</h1>");
        }
    }

    @PostMapping("/update-json")
    public ResponseEntity<?> update(HttpServletRequest request) {
        String idParam = request.getParameter("id");
        String entityName = request.getParameter("entity");
        
        Long entityId = null;
        try {
            if (idParam != null && !idParam.isBlank()) {
                entityId = Long.valueOf(idParam.trim());
            }
        } catch (NumberFormatException e) {
            // ID inválido
        }
        
        if (entityName != null && !entityName.isBlank() && entityId != null) {
            try {
                IEntity entidadePreenchida = fillAndMap(entityName, request);
                IService<IEntity> service = ServiceRegistry.getService(entityName);
                Long primaryKey = service.update(entidadePreenchida);

                System.out.println("Update: Retornou de service.update");
                System.out.println("Update: Long = " + primaryKey);

                return ResponseEntity.ok(primaryKey);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Erro ao atualizar: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("<h1>Erro ao tentar atualizar! id = " + idParam+ " entityName = " + entityName + "</h1>");
        }
    }
    
    @DeleteMapping("/delete-json")
    public ResponseEntity<?> delete(@RequestParam(value = "entity", required = false) String entityName, HttpServletRequest request) {
        if (entityName != null && !entityName.isBlank()) {
            try {
                IEntity filtro = fillAndMap(entityName, request);
                IService<IEntity> service = ServiceRegistry.getService(entityName);
                Long primaryKey = service.delete(filtro);

                System.out.println("Delete: Retornou de service.delete");
                System.out.println("Delete: Chave primária Long = " + primaryKey + " da entidade excluída.");

                return ResponseEntity.ok(primaryKey);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Erro ao deletar: " + e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("<h1>Nenhuma chave primária recebida!</h1>");
        }
    }
}

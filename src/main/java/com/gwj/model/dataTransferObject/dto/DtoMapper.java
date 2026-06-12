package com.gwj.model.dataTransferObject.dto;

import com.gwj.model.domain.IEntity;
import com.gwj.model.domain.entities.Usuario;
import com.gwj.model.domain.entities.Cliente;
import com.gwj.model.domain.entities.Perfil;

public class DtoMapper {

    public static Object toDto(IEntity entity) {
        if (entity instanceof Usuario u) {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setId(u.getId());
            dto.setNomeUsuario(u.getNomeUsuario());
            dto.setEmail(u.getEmail());
            dto.setStatus(u.getStatus());
            if (u.getPerfil() != null) {
                dto.setPerfilId(u.getPerfil().getId());
            }
            return dto;
        } else if (entity instanceof Cliente c) {
            ClienteDTO dto = new ClienteDTO();
            dto.setId(c.getId());
            dto.setNome(c.getNome());
            dto.setSobrenome(c.getSobrenome());
            dto.setTelefone(c.getTelefone());
            dto.setCpf(c.getCpf());
            dto.setObservacao(c.getObservacao());
            return dto;
        }
        return entity; // Fallback se não possuir DTO especializado
    }

    public static IEntity toEntity(Object dto, IEntity targetEntity) {
        if (dto instanceof UsuarioDTO d && targetEntity instanceof Usuario u) {
            u.setId(d.getId());
            u.setNomeUsuario(d.getNomeUsuario());
            u.setEmail(d.getEmail());
            u.setSenha(d.getSenha());
            u.setStatus(d.getStatus());
            if (d.getPerfilId() != null) {
                Perfil p = new Perfil();
                p.setId(d.getPerfilId());
                u.setPerfil(p);
            }
            return u;
        } else if (dto instanceof ClienteDTO d && targetEntity instanceof Cliente c) {
            c.setId(d.getId());
            c.setNome(d.getNome());
            c.setSobrenome(d.getSobrenome());
            c.setTelefone(d.getTelefone());
            c.setCpf(d.getCpf());
            c.setObservacao(d.getObservacao());
            return c;
        }
        return targetEntity;
    }
}

package com.gwj.model.dataTransferObject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class UsuarioDTO {
    private Long id;
    private Long perfilId;
    private String nomeUsuario;
    private String email;
    
    @JsonProperty(access = Access.WRITE_ONLY)
    private String senha;
    private Boolean status;

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public Long getPerfilId() { 
        return perfilId; 
    }
    
    public void setPerfilId(Long perfilId) { 
        this.perfilId = perfilId; 
    }
    
    public String getNomeUsuario() { 
        return nomeUsuario; 
    }
    
    public void setNomeUsuario(String nomeUsuario) { 
        this.nomeUsuario = nomeUsuario; 
    }
    
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    public String getSenha() { 
        return senha; 
    }
    
    public void setSenha(String senha) { 
        this.senha = senha; 
    }
    
    public Boolean getStatus() { 
        return status; 
    }
    
    public void setStatus(Boolean status) { 
        this.status = status; 
    }
}

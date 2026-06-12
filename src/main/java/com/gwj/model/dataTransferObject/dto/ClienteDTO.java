package com.gwj.model.dataTransferObject.dto;

public class ClienteDTO {
    private Long id;
    private String nome;
    private String sobrenome;
    private String telefone;
    private String cpf;
    private String observacao;

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getNome() { 
        return nome; 
    }
    
    public void setNome(String nome) { 
        this.nome = nome; 
    }
    
    public String getSobrenome() { 
        return sobrenome; 
    }
    
    public void setSobrenome(String sobrenome) { 
        this.sobrenome = sobrenome; 
    }
    
    public String getTelefone() { 
        return telefone; 
    }
    
    public void setTelefone(String telefone) { 
        this.telefone = telefone; 
    }
    
    public String getCpf() { 
        return cpf; 
    }
    
    public void setCpf(String cpf) { 
        this.cpf = cpf; 
    }
    
    public String getObservacao() { 
        return observacao; 
    }
    
    public void setObservacao(String observacao) { 
        this.observacao = observacao; 
    }
}

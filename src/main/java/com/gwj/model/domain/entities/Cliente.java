package com.gwj.model.domain.entities;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;

@Entity
public class Cliente extends Usuario { // A classe Cliente é uma especialização de Usuario, herdando atributos e métodos
	
	// Atributos primitivos:
	protected String nome;
	protected String sobrenome;
	protected String telefone;
	protected String cpf;
	protected String observacao;

	@ManyToMany
    @JoinTable(
        name = "tab_cliente_endereco",
        joinColumns = @JoinColumn(name = "cliente_id"),
        inverseJoinColumns = @JoinColumn(name = "endereco_id")
    )
	private List<Endereco> listaEndereco = new ArrayList<>(); // Declaração da lista de Endereço. Note que inicializei a lista com new ArrayList<>() para evitar que o método tente remover algo de uma lista nula.

	public Cliente() {
		super();
		// Atribui o perfil padrão de Cliente (ID 4, conforme banco de dados)
		Perfil perfilPadrao = new Perfil();
		perfilPadrao.setId(4L);
		this.setPerfil(perfilPadrao);
	}

	// Métodos:

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

	public List<Endereco> getListaEndereco() {
		return listaEndereco;
	}

	public void setListaEndereco(List<Endereco> listaEndereco) {
		this.listaEndereco = listaEndereco;
	}
	public void addEndereco(Endereco endereco) {
		this.listaEndereco.add(endereco);
	}
}
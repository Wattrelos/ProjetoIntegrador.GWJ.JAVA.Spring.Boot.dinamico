package com.gwj.model.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import com.gwj.model.domain.IEntity;

@Entity
@Table(name = "tab_agendamento")
public class Agendamento implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = 0L;

    @Column(name = "cliente_nome", nullable = false, length = 100)
    private String clienteNome;

    @Column(name = "cliente_telefone", length = 20)
    private String clienteTelefone;

    @Column(name = "data_agendamento", nullable = false)
    private LocalDate dataAgendamento; // Mapeia o DATE do MySQL

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio; // Mapeia o TIME do MySQL

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim; // Mapeia o TIME do MySQL

    @Column(length = 20)
    private String status = "Confirmado";

    @ManyToOne // Indica que este agendamento "pertence" a um barbeiro
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    @ManyToOne // Indica que este agendamento "pertence" a um servico
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    // Construtor Padrão (Obrigatório para o Hibernate/Reflexão)
    public Agendamento() {
    }

    // Construtor Auxiliar para novos agendamentos
    public Agendamento(String clienteNome, String clienteTelefone,
            LocalDate dataAgendamento, LocalTime horaInicio, LocalTime horaFim) {
        this.clienteNome = clienteNome;
        this.clienteTelefone = clienteTelefone;
        this.dataAgendamento = dataAgendamento;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.status = "Confirmado";
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public void setClienteNome(String clienteNome) {
        this.clienteNome = clienteNome;
    }

    public String getClienteTelefone() {
        return clienteTelefone;
    }

    public void setClienteTelefone(String clienteTelefone) {
        this.clienteTelefone = clienteTelefone;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }
}

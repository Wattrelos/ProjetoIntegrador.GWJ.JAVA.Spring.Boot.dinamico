package com.gwj.model.domain.entities;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.List;

import com.gwj.model.domain.IEntity;

@Entity
@Table(name = "tab_dias_funcionamento")
public class DiasFuncionamento implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dia_semana", nullable = false)
    private Integer diaSemana; // 1 = Segunda, 2 = Terça...

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(nullable = false)
    private Boolean aberto = true;

    @Column(name = "horario_inicio")
    private LocalTime horarioInicio;

    @Column(name = "horario_fim")
    private LocalTime horarioFim;

    // Relacionamento de 1 dia para muitos horários na grade
    @OneToMany(mappedBy = "diaFuncionamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GradeHorarios> gradeHorarios;

    // Construtores
    public DiasFuncionamento() {
    }

    public DiasFuncionamento(Integer diaSemana, String nome, Boolean aberto, LocalTime horarioInicio,
            LocalTime horarioFim) {
        this.diaSemana = diaSemana;
        this.nome = nome;
        this.aberto = aberto;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAberto() {
        return aberto;
    }

    public void setAberto(Boolean aberto) {
        this.aberto = aberto;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }

    public List<GradeHorarios> getGradeHorarios() {
        return gradeHorarios;
    }

    public void setGradeHorarios(List<GradeHorarios> gradeHorarios) {
        this.gradeHorarios = gradeHorarios;
    }

    @Override
    public String toString() {
        return nome;
    }
}

package com.gwj.model.domain.entities;

import jakarta.persistence.*;
import java.time.LocalTime;
import com.gwj.model.domain.IEntity;

@Entity
@Table(name = "tab_grade_horarios")
public class GradeHorarios implements IEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitos horários pertencem a um único dia de funcionamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dia_funcionamento_id", nullable = false)
    private DiasFuncionamento diaFuncionamento;

    @Column(name = "horario_inicio", nullable = false)
    private LocalTime horarioInicio;

    @Column(name = "horario_fim", nullable = false)
    private LocalTime horarioFim;

    // Construtores
    public GradeHorarios() {
    }

    public GradeHorarios(DiasFuncionamento diaFuncionamento, LocalTime horarioInicio, LocalTime horarioFim) {
        this.diaFuncionamento = diaFuncionamento;
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

    public DiasFuncionamento getDiaFuncionamento() {
        return diaFuncionamento;
    }

    public void setDiaFuncionamento(DiasFuncionamento diaFuncionamento) {
        this.diaFuncionamento = diaFuncionamento;
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

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return diaFuncionamento.getNome() + " - " + horarioInicio + " - " + horarioFim;
    }
}

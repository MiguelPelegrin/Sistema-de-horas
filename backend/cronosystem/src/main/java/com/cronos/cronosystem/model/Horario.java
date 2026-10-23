package com.cronos.cronosystem.model;

import com.cronos.cronosystem.model.enums.DiaSemana;
import com.cronos.cronosystem.model.enums.Turmasep;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;


@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "horario")
public class Horario {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private DiaSemana diaSemana;

    @PositiveOrZero
    private Integer tempo_aula;

    @NotNull
    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @NotNull
    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    @Column(name = "turmasep")
    private Turmasep turmasep;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_escola")
    private Escola escola;
}

package com.cronos.cronosystem.model;

import com.cronos.cronosystem.model.enums.DiaSemana;
import com.cronos.cronosystem.model.enums.Turmasep;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "horario")
public class Horario {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana")
    private DiaSemana diaSemana;

    @PositiveOrZero
    private Integer tempo_aula;

    @Enumerated(EnumType.STRING)
    @Column(name = "turmasep")
    private Turmasep turmasep;
}

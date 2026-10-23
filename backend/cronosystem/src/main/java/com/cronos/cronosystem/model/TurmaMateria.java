package com.cronos.cronosystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "turma_materia")
public class TurmaMateria {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_turma")
    private Turma turma;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_materia")
    private Materia materia;

    @NotNull
    @Positive
    @Max(40)
    @Column(name = "qtd_aulas")
    private Integer qtdAulas;
}

package com.cronos.cronosystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "divisao")
public class Divisao {
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
    @JoinColumn(name = "id_materia_a")
    private Materia materiaA;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_materia_b")
    private Materia materiaB;
}

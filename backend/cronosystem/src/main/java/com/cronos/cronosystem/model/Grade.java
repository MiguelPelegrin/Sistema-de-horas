package com.cronos.cronosystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "grade")
public class Grade {
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
    @JoinColumn(name = "id_materia_prof")
    private MateriaProf materiaProf;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_horario")
    private Horario horario;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_escola")
    private Escola escola;
}

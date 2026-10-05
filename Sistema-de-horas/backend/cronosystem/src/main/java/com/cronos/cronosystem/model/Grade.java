package com.cronos.cronosystem.model;

import jakarta.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "id_turma")
    private Turma turma;

    @ManyToOne
    @JoinColumn(name = "id_materia_prof")
    private MateriaProf materiaProf;

    @ManyToOne
    @JoinColumn(name = "id_horario")
    private Horario horario;
}

package com.cronos.cronosystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "materia_prof")
public class MateriaProf {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_prof")
    private Prof prof;

    @ManyToOne
    @JoinColumn(name = "id_materia")
    private Materia materia;
}

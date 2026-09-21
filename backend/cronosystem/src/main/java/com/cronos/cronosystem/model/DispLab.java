package com.cronos.cronosystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "disp_lab")
public class DispLab {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String horario;

    private String dia_semana;

    @ManyToOne
    @JoinColumn(name = "id_materia")
    private Materia materia;
}

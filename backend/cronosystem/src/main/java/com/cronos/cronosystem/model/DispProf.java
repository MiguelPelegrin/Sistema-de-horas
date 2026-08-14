package com.cronos.cronosystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "disp_prof")
public class DispProf {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name= 'id_prof')
    private Prof prof;

    @ManyToOne
    @JoinColumn(name = 'id_horario')
    private Horario horario;
}

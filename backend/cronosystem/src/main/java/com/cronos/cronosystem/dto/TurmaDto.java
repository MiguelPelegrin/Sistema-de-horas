package com.cronos.cronosystem.model;

import com.cronos.cronosystem.model.enums.Turno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "turma")
public class Turma {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "turno")
    private Turno turno;
}

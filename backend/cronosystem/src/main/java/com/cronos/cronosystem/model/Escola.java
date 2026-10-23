package com.cronos.cronosystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "escola")
public class Escola {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 225)
    private String nome;

    @Size(max = 255)
    private String coordenacao;

    @Size(max = 100)
    private String semestre;

    @Size(max = 50)
    private String turno;

    @Size(max = 100)
    @Column(name = "dias_letivos")
    private String diasLetivos;
}

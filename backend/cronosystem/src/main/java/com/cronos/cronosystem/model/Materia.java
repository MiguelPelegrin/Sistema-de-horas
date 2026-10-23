package com.cronos.cronosystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "materia")
public class Materia {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String nome;

    @Size(max = 20)
    private String cor;

    @Size(max = 100)
    private String laboratorio;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_escola")
    private Escola escola;
}

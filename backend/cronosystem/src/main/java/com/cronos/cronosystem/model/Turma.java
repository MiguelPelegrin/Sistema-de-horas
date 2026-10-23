package com.cronos.cronosystem.model;

import com.cronos.cronosystem.model.enums.Turno;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(max = 255)
    private String nome;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "turno")
    private Turno turno;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_escola")
    private Escola escola;
}

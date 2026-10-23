package com.cronos.cronosystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "prof")
public class Prof {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String nome;

    @PositiveOrZero
    @Max(65)
    @Column(name = "carga_horaria_maxima")
    private Integer chm; // Carga Horaria Maxima

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_escola")
    private Escola escola;
}

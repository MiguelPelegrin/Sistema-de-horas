package com.cronos.cronosystem.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class ProfDto {

    private Long id;

    private String nome;

    private String chm; // Carga Horaria Maxima
}

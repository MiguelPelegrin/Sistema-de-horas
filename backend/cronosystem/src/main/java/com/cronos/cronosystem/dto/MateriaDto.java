package com.cronos.cronosystem.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@AllArgsConstructor
public class MateriaDto {

    private Long id;

    private String nome;
}

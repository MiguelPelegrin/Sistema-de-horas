package com.cronos.cronosystem.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
public class MateriaDto {

    private Long id;

    private String nome;
}

package com.cronos.cronosystem.dto;

import com.cronos.cronosystem.model.enums.Turno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public class TurmaDto {

    private Long id;

    private String nome;

    private String turno;
}

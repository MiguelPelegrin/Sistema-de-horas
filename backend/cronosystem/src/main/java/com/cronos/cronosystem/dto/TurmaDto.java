package com.cronos.cronosystem.dto;

import com.cronos.cronosystem.model.enums.Turno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class TurmaDto {

    private Long id;

    private String nome;

    private String turno;
}

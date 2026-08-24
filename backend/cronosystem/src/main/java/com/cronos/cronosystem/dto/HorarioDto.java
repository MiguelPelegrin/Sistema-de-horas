package com.cronos.cronosystem.dto;

import com.cronos.cronosystem.model.enums.DiaSemana;
import com.cronos.cronosystem.model.enums.Turmasep;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
public class HorarioDto {

    private Long id;

    private String diaSemana;

    private String tempo_aula;

    private String turmasep;
}

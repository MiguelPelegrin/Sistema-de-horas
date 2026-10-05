package com.cronos.cronosystem.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public class GradeDto {

    private Long id;

    private String turma;

    private String materiaProf;

    private String horario;
}

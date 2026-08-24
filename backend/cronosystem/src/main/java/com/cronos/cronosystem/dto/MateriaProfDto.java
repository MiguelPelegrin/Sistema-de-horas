package com.cronos.cronosystem.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class MateriaProfDto {

    private Long id;

    private String prof;

    private String materia;
}

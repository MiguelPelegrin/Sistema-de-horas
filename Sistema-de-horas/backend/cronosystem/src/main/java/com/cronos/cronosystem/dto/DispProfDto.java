package com.cronos.cronosystem.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public class DispProfDto {

    private Long id;

    private String prof;

    private String horario;

}

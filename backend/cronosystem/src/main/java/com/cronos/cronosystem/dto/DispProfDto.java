package com.cronos.cronosystem.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class DispProfDto {

    private Long id;

    private String prof;

    private String horario;

}

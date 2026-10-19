package com.cronos.cronosystem.dto;

import lombok.Data;

@Data
public class DisponibilidadeProfDto {
    private String dia;
    private String horaInicio;
    private String horaFim;
    private Integer tempoAula;
}

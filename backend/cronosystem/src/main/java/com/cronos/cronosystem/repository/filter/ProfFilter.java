package com.cronos.cronosystem.repository.filter;

import lombok.Data;

@Data
public class ProfFilter {

    private Long id;

    private String nome;

    private String chm; // Carga Horaria Maxima
}

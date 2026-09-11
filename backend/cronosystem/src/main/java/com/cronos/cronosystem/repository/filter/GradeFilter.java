package com.cronos.cronosystem.repository.filter;

import lombok.Data;

@Data
public class GradeFilter {

    private Long id;

    private String turma;

    private String materiaProf;

    private String horario;
}

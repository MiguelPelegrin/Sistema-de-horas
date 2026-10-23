package com.cronos.cronosystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDto {

    private Long id;

    private String nome;

    private String email;
}

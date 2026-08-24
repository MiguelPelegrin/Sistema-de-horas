package com.cronos.cronosystem.dto;

import lombok.Data;

@Data
public class UsuarioFilter {

    private Long id;

    private String nome;

    private String email;

    private String senha;
}

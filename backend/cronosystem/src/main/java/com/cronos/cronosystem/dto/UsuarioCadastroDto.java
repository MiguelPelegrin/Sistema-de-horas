package com.cronos.cronosystem.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioCadastroDto (
        @NotBlank String nome,

        @NotBlank String email,

        @NotBlank String senha

){
}

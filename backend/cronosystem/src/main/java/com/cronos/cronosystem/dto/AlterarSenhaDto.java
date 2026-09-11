package com.cronos.cronosystem.dto;

import jakarta.validation.constraints.NotBlank;

public record AlterarSenhaDto(

        @NotBlank String senhaAtual,

        @NotBlank String novaSenha
) {
}

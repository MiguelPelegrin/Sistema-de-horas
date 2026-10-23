package com.cronos.cronosystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaDto(

        @NotBlank String senhaAtual,

        @NotBlank @Size(min = 6) String novaSenha
) {
}

package com.cronos.cronosystem.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(

        @NotBlank String email,

        @NotBlank String senha
) {
}

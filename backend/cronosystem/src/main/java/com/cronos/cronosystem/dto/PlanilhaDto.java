package com.cronos.cronosystem.dto;

import java.time.LocalDateTime;

public record PlanilhaDto(Long id, String nomeArquivo, Boolean completa, Integer aulasPendentes,
                          Boolean solucaoSugerida, LocalDateTime dataGeracao) {
}

package com.cronos.cronosystem.dto;

public record LoginResponse(String token, Long id, String nome, String email) {
}

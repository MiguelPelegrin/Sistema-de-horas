package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.dto.CadastroRequest;
import com.cronos.cronosystem.dto.LoginRequest;
import com.cronos.cronosystem.dto.LoginResponse;
import com.cronos.cronosystem.dto.UsuarioResponse;
import com.cronos.cronosystem.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }
}

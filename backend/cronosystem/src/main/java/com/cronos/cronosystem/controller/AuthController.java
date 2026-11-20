package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.dto.LoginDto;
import com.cronos.cronosystem.dto.TokenDto;
import com.cronos.cronosystem.model.Usuario;
import com.cronos.cronosystem.service.TentativaLoginService;
import com.cronos.cronosystem.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TentativaLoginService tentativaLoginService;

    @PostMapping("/login")
    public TokenDto login(@RequestBody @Valid LoginDto dados, HttpServletRequest request) {
        String chave = tentativaLoginService.chave(dados.email(), request.getRemoteAddr());
        tentativaLoginService.validarBloqueio(chave);

        var credenciais = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        Authentication autenticacao;

        try {
            autenticacao = authenticationManager.authenticate(credenciais);
        } catch (AuthenticationException erro) {
            tentativaLoginService.registrarErro(chave);
            throw erro;
        }

        tentativaLoginService.limpar(chave);
        Usuario usuario = (Usuario) autenticacao.getPrincipal();

        String escola = usuario.getEscola() == null ? null : usuario.getEscola().getNome();

        return new TokenDto(tokenService.gerarToken(usuario), usuario.getNome(), usuario.getAdmin(), escola);
    }
}

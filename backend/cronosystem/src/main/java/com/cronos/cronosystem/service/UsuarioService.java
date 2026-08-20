package com.cronos.cronosystem.service;

import com.cronos.cronosystem.dto.CadastroRequest;
import com.cronos.cronosystem.dto.LoginRequest;
import com.cronos.cronosystem.dto.LoginResponse;
import com.cronos.cronosystem.dto.UsuarioResponse;
import com.cronos.cronosystem.exception.CredenciaisInvalidasException;
import com.cronos.cronosystem.exception.EmailJaCadastradoException;
import com.cronos.cronosystem.model.Usuario;
import com.cronos.cronosystem.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));

        return UsuarioResponse.fromEntity(usuarioRepository.save(usuario));
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        String token = jwtService.gerarToken(usuario);
        return new LoginResponse(token, usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}

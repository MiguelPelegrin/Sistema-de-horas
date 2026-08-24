package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.dto.AlterarSenhaDto;
import com.cronos.cronosystem.dto.UsuarioCadastroDto;
import com.cronos.cronosystem.dto.UsuarioDto;
import com.cronos.cronosystem.dto.UsuarioPublicoDto;
import com.cronos.cronosystem.model.Usuario;
import com.cronos.cronosystem.repository.UsuarioRepository;
import com.cronos.cronosystem.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> listar(){return repository.findAll();}
    @GetMapping("/pornome")
    public Page<UsuarioDto> listarPorNome(UsuarioFilter userFilter, Pageable pageable){
        return repository.filtrar(userFilter, pageable);
    }

    @GetMapping("/me")
    public Usuario buscarLogado(@AuthenticationPrincipal Usuario usuarioLogado){
        return usuarioLogado;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> buscar(@PathVariable Long userId, @AuthenticationPrincipal Usuario usuarioLogado){
        Usuario usuario = service.buscaroufalhar(userId);

        boolean ehDono = usuarioLogado != null && userId.equals(usuarioLogado.getId());
        boolean ehAdmin = usuarioLogado != null && Boolean.TRUE.equals(usuarioLogado.getAdmin());

        if (ehDono || ehAdmin) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.ok(new UsuarioPublicoDto(usuario.getId(), usuario.getNome()));
    }

    @PostMapping
    public Usuario adicionar(@RequestBody @Valid UsuarioCadastroDto dados) { return service.cadastrar(dados); }

    @DeleteMapping("/{userId}")
    public void remover(@PathVariable Long userId, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.validarDono(userId, usuarioLogado);
        service.excluir(userId);
    }

    @PutMapping("/{userId}")
    public Usuario alterar(@PathVariable Long userId, @RequestBody Usuario user, @AuthenticationPrincipal Usuario usuarioLogado){
        service.validarDono(userId, usuarioLogado);

        Usuario userAtual = service.buscaroufalhar(userId);
        BeanUtils.copyProperties(user, userAtual, "id", "senha", "plano", "admin");
        return service.salvar(userAtual);
    }

    @PutMapping("/{userId}/senha")
    public Usuario alterarSenha(@PathVariable Long userId, @RequestBody @Valid AlterarSenhaDto dados, @AuthenticationPrincipal Usuario usuarioLogado){
        service.validarDono(userId, usuarioLogado);
        return service.alterarSenha(userId, dados);
    }
}

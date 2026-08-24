package com.cronos.cronosystem.controller;

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
    public Page<UserDto> listarPorNome(UserFilter userFilter, Pageable pageable){
        return repository.filtrar(userFilter, pageable);
    }

    @GetMapping("/me")
    public User buscarLogado(@AuthenticationPrincipal User usuarioLogado){
        return usuarioLogado;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> buscar(@PathVariable Long userId, @AuthenticationPrincipal User usuarioLogado){
        User usuario = service.buscaroufalhar(userId);

        boolean ehDono = usuarioLogado != null && userId.equals(usuarioLogado.getId());
        boolean ehAdmin = usuarioLogado != null && Boolean.TRUE.equals(usuarioLogado.getAdmin());

        if (ehDono || ehAdmin) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.ok(new UserPublicoDto(usuario.getId(), usuario.getUser(), usuario.getDescr()));
    }

    @PostMapping
    public User adicionar(@RequestBody @Valid UserCadastroDto dados) { return service.cadastrar(dados); }

    @DeleteMapping("/{userId}")
    public void remover(@PathVariable Long userId, @AuthenticationPrincipal User usuarioLogado){
        service.validarDono(userId, usuarioLogado);
        service.excluir(userId);
    }

    @PutMapping("/{userId}")
    public User alterar(@PathVariable Long userId, @RequestBody User user, @AuthenticationPrincipal User usuarioLogado){
        service.validarDono(userId, usuarioLogado);

        User userAtual = service.buscaroufalhar(userId);
        BeanUtils.copyProperties(user, userAtual, "id", "senha", "plano", "admin");
        return service.salvar(userAtual);
    }

    @PutMapping("/{userId}/senha")
    public User alterarSenha(@PathVariable Long userId, @RequestBody @Valid AlterarSenhaDto dados, @AuthenticationPrincipal User usuarioLogado){
        service.validarDono(userId, usuarioLogado);
        return service.alterarSenha(userId, dados);
    }

}

package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.dto.AlterarSenhaDto;
import com.cronos.cronosystem.dto.UsuarioCadastroDto;
import com.cronos.cronosystem.dto.UsuarioDto;
import com.cronos.cronosystem.dto.UsuarioPublicoDto;
import com.cronos.cronosystem.repository.filter.UsuarioFilter;
import com.cronos.cronosystem.model.Usuario;
import com.cronos.cronosystem.repository.UsuarioRepository;
import com.cronos.cronosystem.service.UsuarioService;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @GetMapping("/pornome")
    public Page<UsuarioDto> pesquisar(UsuarioFilter filter, Pageable pageable) {
        return repository.filtrar(filter, pageable);
    }

    @GetMapping("/me")
    public Usuario buscarLogado(@AuthenticationPrincipal Usuario usuarioLogado) {
        return usuarioLogado;
    }

    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Usuario add(@RequestBody UsuarioCadastroDto dados) {
        return service.cadastrar(dados);
    }

    @SneakyThrows
    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id, @AuthenticationPrincipal Usuario usuarioLogado) {
        service.validarDono(id, usuarioLogado);
        service.excluir(id);
    }

    @SneakyThrows
    @PutMapping("/{id}")
    public Usuario atualizar(@PathVariable Long id, @RequestBody Usuario model,
                             @AuthenticationPrincipal Usuario usuarioLogado) {
        service.validarDono(id, usuarioLogado);
        Usuario modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id", "senha", "plano", "admin");
        return service.salvar(modelAtual);
    }

    @SneakyThrows
    @PutMapping("/{id}/senha")
    public Usuario alterarSenha(@PathVariable Long id, @RequestBody AlterarSenhaDto dados,
                                @AuthenticationPrincipal Usuario usuarioLogado) {
        service.validarDono(id, usuarioLogado);
        return service.alterarSenha(id, dados);
    }
}

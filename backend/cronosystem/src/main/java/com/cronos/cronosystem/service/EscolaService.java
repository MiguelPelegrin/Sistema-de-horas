package com.cronos.cronosystem.service;

import com.cronos.cronosystem.dto.EscolaCadastroDto;
import com.cronos.cronosystem.dto.EscolaResumoDto;
import com.cronos.cronosystem.exception.RegraNegocioException;
import com.cronos.cronosystem.model.Escola;
import com.cronos.cronosystem.model.Usuario;
import com.cronos.cronosystem.repository.EscolaRepository;
import com.cronos.cronosystem.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EscolaService {

    @Autowired
    private EscolaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario usuarioLogado() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null || !(autenticacao.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Usuário não autenticado.");
        }

        return usuario;
    }

    public Escola escolaLogada() {
        Escola escola = usuarioLogado().getEscola();

        if (escola == null) {
            throw new AccessDeniedException("Este usuário não está vinculado a nenhuma escola.");
        }

        return escola;
    }

    public Long escolaLogadaId() {
        return escolaLogada().getId();
    }

    public Escola buscar() {
        return repository.findById(escolaLogadaId()).orElseThrow();
    }

    private static final List<String> DIAS_LETIVOS = List.of("Segunda", "Terça", "Quarta", "Quinta", "Sexta");

    private void validarDiasLetivos(String diasLetivos) {
        if (diasLetivos == null || diasLetivos.isBlank()) {
            throw new RegraNegocioException("Selecione pelo menos um dia letivo.");
        }

        for (String dia : diasLetivos.split(",")) {
            if (!DIAS_LETIVOS.contains(dia)) {
                throw new RegraNegocioException("Dia letivo inválido: " + dia);
            }
        }
    }

    @Transactional
    public Escola atualizar(Escola model) {
        validarDiasLetivos(model.getDiasLetivos());
        Escola escola = buscar();
        escola.setNome(model.getNome());
        escola.setCoordenacao(model.getCoordenacao());
        escola.setSemestre(model.getSemestre());
        escola.setTurno(model.getTurno());
        escola.setDiasLetivos(model.getDiasLetivos());
        return repository.save(escola);
    }

    @Transactional
    public EscolaResumoDto cadastrar(EscolaCadastroDto dados) {
        if (usuarioRepository.existsByEmail(dados.email())) {
            throw new DataIntegrityViolationException("Já existe um usuário com esse e-mail.");
        }

        Escola escola = new Escola();
        escola.setNome(dados.nomeEscola());
        escola.setCoordenacao(dados.coordenacao());
        escola.setDiasLetivos("Segunda,Terça,Quarta,Quinta,Sexta");
        escola = repository.save(escola);

        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        usuario.setAdmin(false);
        usuario.setEscola(escola);
        usuarioRepository.save(usuario);

        return new EscolaResumoDto(escola.getId(), escola.getNome(), escola.getCoordenacao(), usuario.getNome(), usuario.getEmail());
    }

    public List<EscolaResumoDto> listar() {
        return usuarioRepository.findByEscolaIsNotNullOrderByEscola_Nome().stream()
                .map(usuario -> new EscolaResumoDto(
                        usuario.getEscola().getId(),
                        usuario.getEscola().getNome(),
                        usuario.getEscola().getCoordenacao(),
                        usuario.getNome(),
                        usuario.getEmail()))
                .toList();
    }
}

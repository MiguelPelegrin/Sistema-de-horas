package com.cronos.cronosystem.service;

import com.cronos.cronosystem.dto.AlterarSenhaDto;
import com.cronos.cronosystem.dto.UsuarioCadastroDto;
import com.cronos.cronosystem.model.Usuario;
import com.cronos.cronosystem.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario cadastrar(UsuarioCadastroDto dados){
        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        usuario.setAdmin(false);
        return repository.save(usuario);
    }

    @Transactional
    public Usuario salvar(Usuario usuario){return repository.save(usuario);}

    @Transactional
    public Usuario alterarSenha(Long userId, AlterarSenhaDto dados){
        Usuario usuario = buscaroufalhar(userId);

        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.getSenha())) {
            throw new BadCredentialsException("Senha atual incorreta.");
        }

        usuario.setSenha(passwordEncoder.encode(dados.novaSenha()));
        return repository.save(usuario);
    }

    public Usuario buscaroufalhar(Long userId){
        return repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("usuario não encontrado com esse ID."));
    }

    @Transactional
    public void excluir(Long userId){ repository.deleteById(userId);}

    public void validarDono(Long userId, Usuario usuarioLogado) throws AccessDeniedException { // Se erro tirar throws AccessDeniedException
        boolean ehDono = userId.equals(usuarioLogado.getId());
        boolean ehAdmin = Boolean.TRUE.equals(usuarioLogado.getAdmin());

        if (!ehDono && !ehAdmin) {
            throw new AccessDeniedException("Você não tem permissão para alterar este usuário");
        }
    }
}

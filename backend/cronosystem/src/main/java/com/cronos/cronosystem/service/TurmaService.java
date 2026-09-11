package com.cronos.cronosystem.service;

import com.cronos.cronosystem.dto.AlterarSenhaDto;
import com.cronos.cronosystem.model.Turma;
import com.cronos.cronosystem.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository repository;

    public Turma salvar(Turma x) {
        return repository.save(x);
    }

    public Turma buscaroufalhar(Long yId){
        return repository.findById(yId)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){ repository.deleteById(yId);}
}

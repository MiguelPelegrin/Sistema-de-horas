package com.cronos.cronosystem.service;


import com.cronos.cronosystem.model.Horario;
import com.cronos.cronosystem.repository.HorarioRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class HorarioService {
    @Autowired
    private HorarioRepository repository;

    public Horario salvar(Horario x) {
        return repository.save(x);
    }

    public Horario buscaroufalhar(Long yId) {
        return repository.findById(yId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com esse ID."));
    }

    @Transactional
    public void excluir(Long yId) {
        repository.deleteById(yId);
    }
}

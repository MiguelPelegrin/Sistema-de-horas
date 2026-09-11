package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.Prof;
import com.cronos.cronosystem.repository.ProfRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfService {
    @Autowired
    private ProfRepository repository;

    public Prof salvar(Prof x) {
        return repository.save(x);
    }

    public Prof buscaroufalhar(Long yId){
        return repository.findById(yId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){ repository.deleteById(yId);}
}

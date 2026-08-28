package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.repository.DispProfRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DispProfService {
    @Autowired
    private DispProfRepository repository;

    public DispProf salvar(DispProf x) {
        return repository.save(x);
    }

    public DispProf buscaroufalhar(Long yId){
        return repository.findById(yId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){ repository.deleteById(yId);}
}

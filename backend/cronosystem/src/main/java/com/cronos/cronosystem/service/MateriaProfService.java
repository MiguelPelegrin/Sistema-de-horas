package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.MateriaProf;
import com.cronos.cronosystem.repository.MateriaProfRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MateriaProfService {
    @Autowired
    private MateriaProfRepository repository;

    public MateriaProf salvar(MateriaProf x) {
        return repository.save(x);
    }

    public MateriaProf buscaroufalhar(Long yId){
        return repository.findById(yId)
                .orElseThrow(() -> new EntityNotFoundException("Matéria não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){ repository.deleteById(yId);}
}

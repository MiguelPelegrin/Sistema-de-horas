package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.Materia;
import com.cronos.cronosystem.repository.MateriaRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository repository;

    public Materia salvar(Materia x) {
        return repository.save(x);
    }

    public Materia buscaroufalhar(Long yId){
        return repository.findById(yId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){ repository.deleteById(yId);}
}

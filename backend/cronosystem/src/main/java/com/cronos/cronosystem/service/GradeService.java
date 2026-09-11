package com.cronos.cronosystem.service;


import com.cronos.cronosystem.model.Grade;
import com.cronos.cronosystem.repository.GradeRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GradeService {
    @Autowired
    private GradeRepository repository;

    public Grade salvar(Grade x) {
        return repository.save(x);
    }

    public Grade buscaroufalhar(Long yId){
        return repository.findById(yId)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){ repository.deleteById(yId);}
}

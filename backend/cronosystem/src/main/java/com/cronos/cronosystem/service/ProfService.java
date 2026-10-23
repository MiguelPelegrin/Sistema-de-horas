package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.Prof;
import com.cronos.cronosystem.repository.DispProfRepository;
import com.cronos.cronosystem.repository.GradeRepository;
import com.cronos.cronosystem.repository.MateriaProfRepository;
import com.cronos.cronosystem.repository.ProfRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfService {
    @Autowired
    private ProfRepository repository;

    @Autowired
    private DispProfRepository dispProfRepository;

    @Autowired
    private MateriaProfRepository materiaProfRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EscolaService escolaService;

    public List<Prof> listar() {
        return repository.findByEscola_IdOrderByNome(escolaService.escolaLogadaId());
    }

    public Prof salvar(Prof x) {
        x.setEscola(escolaService.escolaLogada());
        return repository.save(x);
    }

    public Prof buscaroufalhar(Long yId){
        return repository.findByIdAndEscola_Id(yId, escolaService.escolaLogadaId())
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){
        Prof prof = buscaroufalhar(yId);
        gradeRepository.deleteByMateriaProf_Prof_Id(prof.getId());
        materiaProfRepository.deleteByProf_Id(prof.getId());
        dispProfRepository.deleteByProf_Id(prof.getId());
        repository.delete(prof);
    }
}

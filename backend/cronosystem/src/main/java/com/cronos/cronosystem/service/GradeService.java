package com.cronos.cronosystem.service;


import com.cronos.cronosystem.model.Grade;
import com.cronos.cronosystem.repository.GradeRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GradeService {
    @Autowired
    private GradeRepository repository;

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private MateriaProfService materiaProfService;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private EscolaService escolaService;

    public List<Grade> listar() {
        return repository.findByEscola_Id(escolaService.escolaLogadaId());
    }

    public Grade salvar(Grade x) {
        x.setTurma(turmaService.buscaroufalhar(x.getTurma().getId()));
        x.setMateriaProf(materiaProfService.buscaroufalhar(x.getMateriaProf().getId()));
        x.setHorario(horarioService.buscaroufalhar(x.getHorario().getId()));
        x.setEscola(escolaService.escolaLogada());
        return repository.save(x);
    }

    public Grade buscaroufalhar(Long yId){
        return repository.findByIdAndEscola_Id(yId, escolaService.escolaLogadaId())
                .orElseThrow(() -> new EntityNotFoundException("Aula da grade não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){ repository.delete(buscaroufalhar(yId));}
}

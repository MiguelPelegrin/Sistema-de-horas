package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.Turma;
import com.cronos.cronosystem.repository.DivisaoRepository;
import com.cronos.cronosystem.repository.GradeRepository;
import com.cronos.cronosystem.repository.TurmaMateriaRepository;
import com.cronos.cronosystem.repository.TurmaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository repository;

    @Autowired
    private TurmaMateriaRepository turmaMateriaRepository;

    @Autowired
    private DivisaoRepository divisaoRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EscolaService escolaService;

    public List<Turma> listar() {
        return repository.findByEscola_IdOrderByNome(escolaService.escolaLogadaId());
    }

    public Turma salvar(Turma x) {
        x.setEscola(escolaService.escolaLogada());
        return repository.save(x);
    }

    public Turma buscaroufalhar(Long yId){
        return repository.findByIdAndEscola_Id(yId, escolaService.escolaLogadaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){
        Turma turma = buscaroufalhar(yId);
        gradeRepository.deleteByTurma_Id(turma.getId());
        turmaMateriaRepository.deleteByTurma_Id(turma.getId());
        divisaoRepository.deleteByTurma_Id(turma.getId());
        repository.delete(turma);
    }
}

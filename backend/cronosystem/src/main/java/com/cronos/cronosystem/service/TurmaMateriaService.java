package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.TurmaMateria;
import com.cronos.cronosystem.repository.TurmaMateriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TurmaMateriaService {

    @Autowired
    private TurmaMateriaRepository repository;

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private EscolaService escolaService;

    public List<TurmaMateria> listar() {
        return repository.findByTurma_Escola_Id(escolaService.escolaLogadaId());
    }

    public List<TurmaMateria> listarPorTurma(Long turmaId) {
        return repository.findByTurma_Id(turmaService.buscaroufalhar(turmaId).getId());
    }

    public TurmaMateria salvar(TurmaMateria x) {
        x.setTurma(turmaService.buscaroufalhar(x.getTurma().getId()));
        x.setMateria(materiaService.buscaroufalhar(x.getMateria().getId()));

        repository.findByTurma_IdAndMateria_Id(x.getTurma().getId(), x.getMateria().getId())
                .filter(existente -> !existente.getId().equals(x.getId()))
                .ifPresent(existente -> {
                    throw new DataIntegrityViolationException("Essa disciplina já foi adicionada à turma.");
                });

        return repository.save(x);
    }

    public TurmaMateria buscaroufalhar(Long yId) {
        return repository.findByIdAndTurma_Escola_Id(yId, escolaService.escolaLogadaId())
                .orElseThrow(() -> new EntityNotFoundException("Disciplina da turma não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId) {
        repository.delete(buscaroufalhar(yId));
    }
}

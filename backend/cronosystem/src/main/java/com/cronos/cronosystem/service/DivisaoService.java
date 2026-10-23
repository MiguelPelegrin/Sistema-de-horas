package com.cronos.cronosystem.service;

import com.cronos.cronosystem.exception.RegraNegocioException;
import com.cronos.cronosystem.model.Divisao;
import com.cronos.cronosystem.repository.DivisaoRepository;
import com.cronos.cronosystem.repository.TurmaMateriaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DivisaoService {

    @Autowired
    private DivisaoRepository repository;

    @Autowired
    private TurmaMateriaRepository turmaMateriaRepository;

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private EscolaService escolaService;

    public List<Divisao> listar() {
        return repository.findByTurma_Escola_Id(escolaService.escolaLogadaId());
    }

    public List<Divisao> listarPorTurma(Long turmaId) {
        return repository.findByTurma_Id(turmaService.buscaroufalhar(turmaId).getId());
    }

    public Divisao salvar(Divisao x) {
        x.setTurma(turmaService.buscaroufalhar(x.getTurma().getId()));
        x.setMateriaA(materiaService.buscaroufalhar(x.getMateriaA().getId()));
        x.setMateriaB(materiaService.buscaroufalhar(x.getMateriaB().getId()));

        if (x.getMateriaA().getId().equals(x.getMateriaB().getId())) {
            throw new RegraNegocioException("A divisão precisa de duas disciplinas diferentes.");
        }

        Long turmaId = x.getTurma().getId();

        if (turmaMateriaRepository.findByTurma_IdAndMateria_Id(turmaId, x.getMateriaA().getId()).isEmpty()
                || turmaMateriaRepository.findByTurma_IdAndMateria_Id(turmaId, x.getMateriaB().getId()).isEmpty()) {
            throw new RegraNegocioException("As duas disciplinas da divisão precisam estar cadastradas na turma.");
        }

        return repository.save(x);
    }

    public Divisao buscaroufalhar(Long yId) {
        return repository.findByIdAndTurma_Escola_Id(yId, escolaService.escolaLogadaId())
                .orElseThrow(() -> new EntityNotFoundException("Divisão não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId) {
        repository.delete(buscaroufalhar(yId));
    }
}

package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.Materia;
import com.cronos.cronosystem.repository.DivisaoRepository;
import com.cronos.cronosystem.repository.GradeRepository;
import com.cronos.cronosystem.repository.MateriaProfRepository;
import com.cronos.cronosystem.repository.MateriaRepository;
import com.cronos.cronosystem.repository.TurmaMateriaRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MateriaService {

    @Autowired
    private MateriaRepository repository;

    @Autowired
    private MateriaProfRepository materiaProfRepository;

    @Autowired
    private TurmaMateriaRepository turmaMateriaRepository;

    @Autowired
    private DivisaoRepository divisaoRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EscolaService escolaService;

    public List<Materia> listar() {
        return repository.findByEscola_IdOrderByNome(escolaService.escolaLogadaId());
    }

    public Materia salvar(Materia x) {
        Long escolaId = escolaService.escolaLogadaId();

        boolean nomeRepetido = x.getId() == null
                ? repository.existsByNomeIgnoreCaseAndEscola_Id(x.getNome(), escolaId)
                : repository.existsByNomeIgnoreCaseAndEscola_IdAndIdNot(x.getNome(), escolaId, x.getId());

        if (nomeRepetido) {
            throw new DataIntegrityViolationException("Já existe uma disciplina com esse nome.");
        }

        x.setEscola(escolaService.escolaLogada());
        return repository.save(x);
    }

    public Materia buscaroufalhar(Long yId){
        return repository.findByIdAndEscola_Id(yId, escolaService.escolaLogadaId())
                .orElseThrow(() -> new EntityNotFoundException("Matéria não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){
        Materia materia = buscaroufalhar(yId);
        gradeRepository.deleteByMateriaProf_Materia_Id(materia.getId());
        materiaProfRepository.deleteByMateria_Id(materia.getId());
        turmaMateriaRepository.deleteByMateria_Id(materia.getId());
        divisaoRepository.deleteByMateriaA_IdOrMateriaB_Id(materia.getId(), materia.getId());
        repository.delete(materia);
    }
}

package com.cronos.cronosystem.service;

import com.cronos.cronosystem.model.MateriaProf;
import com.cronos.cronosystem.repository.GradeRepository;
import com.cronos.cronosystem.repository.MateriaProfRepository;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MateriaProfService {
    @Autowired
    private MateriaProfRepository repository;

    @Autowired
    private ProfService profService;

    @Autowired
    private MateriaService materiaService;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private EscolaService escolaService;

    public List<MateriaProf> listar() {
        return repository.findByProf_Escola_Id(escolaService.escolaLogadaId());
    }

    public List<MateriaProf> listarPorProfessor(Long profId) {
        return repository.findByProf_Id(profService.buscaroufalhar(profId).getId());
    }

    public MateriaProf salvar(MateriaProf x) {
        x.setProf(profService.buscaroufalhar(x.getProf().getId()));
        x.setMateria(materiaService.buscaroufalhar(x.getMateria().getId()));

        if (x.getId() == null && repository.existsByProf_IdAndMateria_Id(x.getProf().getId(), x.getMateria().getId())) {
            throw new DataIntegrityViolationException("Essa disciplina já está vinculada ao professor.");
        }

        return repository.save(x);
    }

    public MateriaProf buscaroufalhar(Long yId){
        return repository.findByIdAndProf_Escola_Id(yId, escolaService.escolaLogadaId())
                .orElseThrow(() -> new EntityNotFoundException("Matéria do professor não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId){
        MateriaProf materiaProf = buscaroufalhar(yId);
        gradeRepository.deleteByMateriaProf_Id(materiaProf.getId());
        repository.delete(materiaProf);
    }
}

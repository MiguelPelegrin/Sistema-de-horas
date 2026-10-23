package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.Grade;
import com.cronos.cronosystem.repository.Grade.GradeRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long>, GradeRepositoryQuery {
    List<Grade> findByEscola_Id(Long escolaId);

    Optional<Grade> findByIdAndEscola_Id(Long id, Long escolaId);

    long countByEscola_Id(Long escolaId);

    void deleteByTurma_Id(Long turmaId);

    void deleteByMateriaProf_Prof_Id(Long profId);

    void deleteByMateriaProf_Materia_Id(Long materiaId);

    void deleteByMateriaProf_Id(Long materiaProfId);

    void deleteByHorario_Id(Long horarioId);
}

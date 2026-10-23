package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.TurmaMateria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TurmaMateriaRepository extends JpaRepository<TurmaMateria, Long> {
    List<TurmaMateria> findByTurma_Escola_Id(Long escolaId);

    List<TurmaMateria> findByTurma_Id(Long turmaId);

    Optional<TurmaMateria> findByIdAndTurma_Escola_Id(Long id, Long escolaId);

    Optional<TurmaMateria> findByTurma_IdAndMateria_Id(Long turmaId, Long materiaId);

    void deleteByTurma_Id(Long turmaId);

    void deleteByMateria_Id(Long materiaId);
}

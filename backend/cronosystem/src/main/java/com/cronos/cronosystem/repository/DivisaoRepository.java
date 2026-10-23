package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.Divisao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DivisaoRepository extends JpaRepository<Divisao, Long> {
    List<Divisao> findByTurma_Escola_Id(Long escolaId);

    List<Divisao> findByTurma_Id(Long turmaId);

    Optional<Divisao> findByIdAndTurma_Escola_Id(Long id, Long escolaId);

    void deleteByTurma_Id(Long turmaId);

    void deleteByMateriaA_IdOrMateriaB_Id(Long materiaAId, Long materiaBId);
}

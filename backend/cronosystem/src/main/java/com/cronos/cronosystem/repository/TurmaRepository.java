package com.cronos.cronosystem.repository;


import com.cronos.cronosystem.model.Turma;
import com.cronos.cronosystem.repository.Turma.TurmaRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TurmaRepository extends JpaRepository<Turma, Long>, TurmaRepositoryQuery {
    List<Turma> findByEscola_IdOrderByNome(Long escolaId);

    Optional<Turma> findByIdAndEscola_Id(Long id, Long escolaId);

    long countByEscola_Id(Long escolaId);
}

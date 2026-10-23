package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.Materia;
import com.cronos.cronosystem.repository.Materia.MateriaRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia, Long>, MateriaRepositoryQuery {
    List<Materia> findByEscola_IdOrderByNome(Long escolaId);

    Optional<Materia> findByIdAndEscola_Id(Long id, Long escolaId);

    boolean existsByNomeIgnoreCaseAndEscola_Id(String nome, Long escolaId);

    boolean existsByNomeIgnoreCaseAndEscola_IdAndIdNot(String nome, Long escolaId, Long id);

    long countByEscola_Id(Long escolaId);
}

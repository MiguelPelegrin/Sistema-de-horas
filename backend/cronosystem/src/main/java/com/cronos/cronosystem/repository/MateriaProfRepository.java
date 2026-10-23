package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.MateriaProf;
import com.cronos.cronosystem.repository.MateriaProf.MateriaProfRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MateriaProfRepository extends JpaRepository<MateriaProf, Long>, MateriaProfRepositoryQuery {
    List<MateriaProf> findByProf_Escola_Id(Long escolaId);

    List<MateriaProf> findByProf_Id(Long profId);

    Optional<MateriaProf> findByIdAndProf_Escola_Id(Long id, Long escolaId);

    boolean existsByProf_IdAndMateria_Id(Long profId, Long materiaId);

    void deleteByProf_Id(Long profId);

    void deleteByMateria_Id(Long materiaId);
}

package com.cronos.cronosystem.repository;


import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.repository.DispProf.DispProfRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispProfRepository extends JpaRepository<DispProf, Long>, DispProfRepositoryQuery {
    boolean existsByProf_IdAndHorario_Id(Long profId, Long horarioId);

    List<DispProf> findByProf_Escola_Id(Long escolaId);

    List<DispProf> findByProf_Id(Long profId);

    Optional<DispProf> findByIdAndProf_Escola_Id(Long id, Long escolaId);

    void deleteByProf_Id(Long profId);

    void deleteByHorario_Id(Long horarioId);
}

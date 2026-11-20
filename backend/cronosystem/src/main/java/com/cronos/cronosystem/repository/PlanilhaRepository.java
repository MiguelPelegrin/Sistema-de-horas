package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.dto.PlanilhaDto;
import com.cronos.cronosystem.model.Planilha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanilhaRepository extends JpaRepository<Planilha, Long> {

    @Query("select new com.cronos.cronosystem.dto.PlanilhaDto(p.id, p.nomeArquivo, p.completa, p.aulasPendentes, "
            + "p.solucaoSugerida, p.dataGeracao) from Planilha p where p.escola.id = :escolaId "
            + "order by p.dataGeracao desc, p.id desc")
    List<PlanilhaDto> listarResumo(@Param("escolaId") Long escolaId);

    @Query("select p.id from Planilha p where p.escola.id = :escolaId order by p.dataGeracao desc, p.id desc")
    List<Long> listarIds(@Param("escolaId") Long escolaId);

    Optional<Planilha> findByIdAndEscola_Id(Long id, Long escolaId);
}

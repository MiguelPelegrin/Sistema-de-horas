package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.Horario;
import com.cronos.cronosystem.repository.Horario.HorarioRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cronos.cronosystem.model.enums.DiaSemana;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface HorarioRepository extends JpaRepository<Horario, Long>, HorarioRepositoryQuery {
    Optional<Horario> findFirstByDiaSemanaAndHoraInicioAndHoraFimAndEscola_Id(DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFim, Long escolaId);

    List<Horario> findByEscola_Id(Long escolaId);

    Optional<Horario> findByIdAndEscola_Id(Long id, Long escolaId);
}

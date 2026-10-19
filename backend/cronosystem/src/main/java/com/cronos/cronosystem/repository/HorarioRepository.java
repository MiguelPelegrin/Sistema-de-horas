package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.Horario;
import com.cronos.cronosystem.repository.Horario.HorarioRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cronos.cronosystem.model.enums.DiaSemana;

import java.time.LocalTime;

public interface HorarioRepository extends JpaRepository<Horario, Long>, HorarioRepositoryQuery {
    java.util.Optional<Horario> findFirstByDiaSemanaAndHoraInicioAndHoraFim(DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFim);
}

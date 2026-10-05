package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.Horario;
import com.cronos.cronosystem.repository.Horario.HorarioRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorarioRepository extends JpaRepository<Horario, Long>, HorarioRepositoryQuery {

}

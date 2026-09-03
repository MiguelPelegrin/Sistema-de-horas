package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.Materia;
import com.cronos.cronosystem.repository.MateriaProf.MateriaProfRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Long>, MateriaProfRepositoryQuery {

}

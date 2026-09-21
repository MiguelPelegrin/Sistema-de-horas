package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.MateriaProf;
import com.cronos.cronosystem.repository.Materia.MateriaRepositoryQuery;
import com.cronos.cronosystem.repository.MateriaProf.MateriaProfRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaProfRepository extends JpaRepository<MateriaProf, Long>, MateriaProfRepositoryQuery {
}

package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.Grade;
import com.cronos.cronosystem.repository.Grade.GradeRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long>, GradeRepositoryQuery {

}
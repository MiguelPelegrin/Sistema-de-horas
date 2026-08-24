package com.cronos.cronosystem.repository;

import com.cronos.cronosystem.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeDto extends JpaRepository<Grade, Long> {

}
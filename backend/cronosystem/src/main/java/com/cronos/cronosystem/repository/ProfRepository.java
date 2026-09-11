package com.cronos.cronosystem.repository;


import com.cronos.cronosystem.model.Prof;
import com.cronos.cronosystem.repository.Prof.ProfRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfRepository extends JpaRepository<Prof, Long>, ProfRepositoryQuery {
}

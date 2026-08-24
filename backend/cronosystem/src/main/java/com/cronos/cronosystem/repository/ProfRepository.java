package com.cronos.cronosystem.repository;


import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.model.Prof;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfDto extends JpaRepository<Prof, Long> {
}

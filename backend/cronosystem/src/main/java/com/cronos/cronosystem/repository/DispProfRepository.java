package com.cronos.cronosystem.repository;


import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.repository.DispProf.DispProfRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DispProfRepository extends JpaRepository<DispProf, Long>, DispProfRepositoryQuery {

}

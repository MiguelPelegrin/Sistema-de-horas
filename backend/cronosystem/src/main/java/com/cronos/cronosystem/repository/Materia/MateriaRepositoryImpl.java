package com.cronos.cronosystem.repository.Materia;

import com.cronos.cronosystem.dto.MateriaDto;
import com.cronos.cronosystem.repository.filter.MateriaFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class MateriaRepositoryImpl implements MateriaRepositoryQuery{

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<MateriaDto> filtrar(MateriaFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();




        return null;
    }
}

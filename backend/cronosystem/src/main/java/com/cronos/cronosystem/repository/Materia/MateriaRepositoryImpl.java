package com.cronos.cronosystem.repository.Materia;

import com.cronos.cronosystem.dto.MateriaDto;
import com.cronos.cronosystem.model.Materia;
import com.cronos.cronosystem.repository.filter.MateriaFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class MateriaRepositoryImpl implements MateriaRepositoryQuery{

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<MateriaDto> filtrar(MateriaFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<MateriaDto> criteria = builder.createQuery(MateriaDto.class);
        Root<Materia> root = criteria.from(Materia.class);

        criteria.select((builder.construct(MateriaDto.class,
                root.get("id"),
                root.get("nome")
        )));

        Predicate[] predicates = criarRestricoes(filter,builder,root);

        criteria.where(predicates);
        criteria.orderBy(builder.asc(root.get("nome")));





        return null;
    }
}

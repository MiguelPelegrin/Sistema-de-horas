package com.cronos.cronosystem.repository.Impl;

import com.cronos.cronosystem.dto.MateriaProfDto;
import com.cronos.cronosystem.filter.MateriaProfFilter;
import com.cronos.cronosystem.model.MateriaProf;
import com.cronos.cronosystem.repository.query.MateriaProfRepositoryQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class MateriaProfRepositoryImpl implements MateriaProfRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<MateriaProfDto> filtrar(MateriaProfFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<MateriaProfDto> criteria = builder.createQuery(MateriaProfDto.class);
        Root<MateriaProf> root = criteria.from(MateriaProf.class);

        criteria.select(builder.construct(MateriaProfDto.class,
                root.get("id"),
                root.get("nome"),
                root.get("chm")
        ));

        Predicate[] predicates = criarRestricoes(filter, builder, root);

        criteria.where(predicates);
        criteria.orderBy(builder.asc(root.get("nome")));

        TypedQuery<MateriaProfDto> query = manager.createQuery(criteria);
        addRestPag(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private Long total(MateriaProfFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<MateriaProf> root = criteria.from(MateriaProf.class);

        Predicate[] predicates = criarRestricoes(filter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }

    private void addRestPag(TypedQuery<MateriaProfDto> query, Pageable pageable) {
        int pagAtual = pageable.getPageNumber();
        int totalRegPorPag = pageable.getPageSize();
        int primRegPag = pagAtual * totalRegPorPag;

        query.setFirstResult(primRegPag);
        query.setMaxResults(totalRegPorPag);

    }

    private Predicate[] criarRestricoes(MateriaProfFilter filter, CriteriaBuilder builder, Root<MateriaProf> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(!StringUtils.isEmpty(filter.getNome())) {
            predicates.add(builder.like(builder.lower(root.get("nome")), "%" + filter.getNome().toLowerCase()));
        }

        return predicates.toArray(new Predicate[predicates.size()]);

    }
}


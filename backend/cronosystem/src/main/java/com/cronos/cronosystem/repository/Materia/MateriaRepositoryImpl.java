package com.cronos.cronosystem.repository.Materia;

import com.cronos.cronosystem.dto.MateriaDto;
import com.cronos.cronosystem.model.Materia;
import com.cronos.cronosystem.repository.filter.MateriaFilter;
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

        TypedQuery<Materia> query = manager.createQuery(criteria);
        addRestPag(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private Object total(MateriaFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Materia> root = criteria.from(Materia.class);

        Predicate[] predicates = criarRestricoes(filter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }

    private void addRestPag(TypedQuery<Materia> query, Pageable pageable) {
        int pagAtual = pageable.getPageNumber();
        int totalRegPorPag = pageable.getPageSize();
        int primRegPag = pagAtual * totalRegPorPag;

        query.setFirstResult(primRegPag);
        query.setMaxResults(totalRegPorPag);
    }

    private Predicate[] criarRestricoes(MateriaFilter filter, CriteriaBuilder builder, Root<Materia> root) {
        List<Predicate>predicates = new ArrayList<>();

        if(!StringUtils.isEmpty(filter.getNome())) {
            predicates.add(builder.like(builder.lower(root.get("nome")), "%" + filter.getNome().toLowerCase()));
        }
        
        return predicates.toArray(new Predicate[predicates.size()]);
    }
}

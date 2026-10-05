package com.cronos.cronosystem.repository.Prof;

import com.cronos.cronosystem.dto.ProfDto;
import com.cronos.cronosystem.repository.filter.ProfFilter;
import com.cronos.cronosystem.model.Prof;
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

public class ProfRepositoryImpl implements ProfRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<ProfDto> filtrar(ProfFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<ProfDto> criteria = builder.createQuery(ProfDto.class);
        Root<Prof> root = criteria.from(Prof.class);

        criteria.select(builder.construct(ProfDto.class,
                root.get("id"),
                root.get("nome"),
                root.get("chm")
        ));

        Predicate[] predicates = criarRestricoes(filter, builder, root);

        criteria.where(predicates);
        criteria.orderBy(builder.asc(root.get("nome")));

        TypedQuery<ProfDto> query = manager.createQuery(criteria);
        addRestPag(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private Long total(ProfFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Prof> root = criteria.from(Prof.class);

        Predicate[] predicates = criarRestricoes(filter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }

    private void addRestPag(TypedQuery<ProfDto> query, Pageable pageable) {
        int pagAtual = pageable.getPageNumber();
        int totalRegPorPag = pageable.getPageSize();
        int primRegPag = pagAtual * totalRegPorPag;

        query.setFirstResult(primRegPag);
        query.setMaxResults(totalRegPorPag);

    }

    private Predicate[] criarRestricoes(ProfFilter filter, CriteriaBuilder builder, Root<Prof> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(!StringUtils.isEmpty(filter.getNome())) {
            predicates.add(builder.like(builder.lower(root.get("nome")), "%" + filter.getNome().toLowerCase()));
        }

        return predicates.toArray(new Predicate[predicates.size()]);

    }
}

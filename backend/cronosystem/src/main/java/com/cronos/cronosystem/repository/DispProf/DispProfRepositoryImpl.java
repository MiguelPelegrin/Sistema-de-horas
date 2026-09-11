package com.cronos.cronosystem.repository.DispProf;

import com.cronos.cronosystem.dto.DispProfDto;
import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.repository.filter.DispProfFilter;
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

public class DispProfRepositoryImpl implements DispProfRepositoryQuery{

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<DispProfDto> filtrar(DispProfFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<DispProfDto> criteria = builder.createQuery(DispProfDto.class);
        Root<DispProf> root = criteria.from(DispProf.class);

        criteria.select(builder.construct(DispProfDto.class,
                root.get("id"),
                root.get("prof"),
                root.get("horario")
        ));

        Predicate[] predicates = criarRestricoes(filter, builder, root);

        criteria.where(predicates);
        criteria.orderBy(builder.asc(root.get("prof")));

        TypedQuery<DispProfDto> query = manager.createQuery(criteria);
        addRestPag(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private void addRestPag(TypedQuery<DispProfDto> query, Pageable pageable) {
        int pagAtual = pageable.getPageNumber();
        int totalRegPorPag = pageable.getPageSize();
        int primRegPag = pagAtual * totalRegPorPag;

        query.setFirstResult(primRegPag);
        query.setMaxResults(totalRegPorPag);
    }

    private Long total(DispProfFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<DispProf> root = criteria.from(DispProf.class);

        Predicate[] predicates = criarRestricoes(filter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }

    private Predicate[] criarRestricoes(DispProfFilter filter, CriteriaBuilder builder, Root<DispProf> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(!StringUtils.isEmpty(filter.getProf())) {
            predicates.add(builder.like(builder.lower(root.get("prof")), "%" + filter.getProf().toLowerCase()));
        }
        if (filter.getHorario() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("horario"), filter.getHorario()));
        }


        return predicates.toArray(new Predicate[predicates.size()]);
    }
}
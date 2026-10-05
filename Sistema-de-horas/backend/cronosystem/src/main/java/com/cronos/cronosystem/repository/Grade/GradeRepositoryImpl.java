package com.cronos.cronosystem.repository.Grade;

import com.cronos.cronosystem.dto.GradeDto;
import com.cronos.cronosystem.model.Grade;
import com.cronos.cronosystem.repository.filter.GradeFilter;
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

public class GradeRepositoryImpl implements GradeRepositoryQuery{

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<GradeDto> filtrar(GradeFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<GradeDto> criteria = builder.createQuery(GradeDto.class);
        Root<Grade> root = criteria.from(Grade.class);

        criteria.select(builder.construct(GradeDto.class,
                root.get("id"),
                root.get("turma"),
                root.get("materiaProf"),
                root.get("horario")
        ));

        Predicate[] predicates = criarRestricoes(filter, builder, root);

        criteria.where(predicates);
        criteria.orderBy(builder.asc(root.get("turma")));

        TypedQuery<GradeDto> query = manager.createQuery(criteria);
        addRestPag(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private void addRestPag(TypedQuery<GradeDto> query, Pageable pageable) {
        int pagAtual = pageable.getPageNumber();
        int totalRegPorPag = pageable.getPageSize();
        int primRegPag = pagAtual * totalRegPorPag;

        query.setFirstResult(primRegPag);
        query.setMaxResults(totalRegPorPag);
    }

    private Long total(GradeFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Grade> root = criteria.from(Grade.class);

        Predicate[] predicates = criarRestricoes(filter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }

    private Predicate[] criarRestricoes(GradeFilter filter, CriteriaBuilder builder, Root<Grade> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(!StringUtils.isEmpty(filter.getTurma())) {
            predicates.add(builder.like(builder.lower(root.get("turma")), "%" + filter.getTurma().toLowerCase()));
        }

        if(!StringUtils.isEmpty(filter.getMateriaProf())) {
            predicates.add(builder.like(builder.lower(root.get("materiaProf")), "%" + filter.getMateriaProf().toLowerCase()));
        }

        if (filter.getHorario() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("horario"), filter.getHorario()));
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }
}

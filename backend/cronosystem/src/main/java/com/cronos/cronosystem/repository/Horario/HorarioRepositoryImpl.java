package com.cronos.cronosystem.repository.Horario;

import com.cronos.cronosystem.dto.HorarioDto;
import com.cronos.cronosystem.model.Horario;
import com.cronos.cronosystem.repository.filter.HorarioFilter;
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

public class HorarioRepositoryImpl implements HorarioRepositoryQuery{

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<HorarioDto> filtrar(HorarioFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<HorarioDto> criteria = builder.createQuery(HorarioDto.class);
        Root<Horario> root = criteria.from(Horario.class);

        criteria.select(builder.construct(HorarioDto.class,
                root.get("id"),
                root.get("dia_semana"),
                root.get("tempo_aula"),
                root.get("turmasep")
        ));

        Predicate[] predicates = criarRestricoes(filter, builder, root);

        criteria.where(predicates);
        criteria.orderBy(builder.asc(root.get("dia_semana")));

        TypedQuery<HorarioDto> query = manager.createQuery(criteria);
        addRestPag(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private void addRestPag(TypedQuery<HorarioDto> query, Pageable pageable) {
        int pagAtual = pageable.getPageNumber();
        int totalRegPorPag = pageable.getPageSize();
        int primRegPag = pagAtual * totalRegPorPag;

        query.setFirstResult(primRegPag);
        query.setMaxResults(totalRegPorPag);
    }

    private Long total(HorarioFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Horario> root = criteria.from(Horario.class);

        Predicate[] predicates = criarRestricoes(filter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }

    private Predicate[] criarRestricoes(HorarioFilter filter, CriteriaBuilder builder, Root<Horario> root) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getDiaSemana() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("dia_semana"), filter.getDiaSemana()));
        }
        if (filter.getTempo_aula() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("tempo_aula"), filter.getTempo_aula()));
        }
        if(!StringUtils.isEmpty(filter.getTurmasep())) {
            predicates.add(builder.like(builder.lower(root.get("turmasep")), "%" + filter.getTurmasep().toLowerCase()));
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }
}


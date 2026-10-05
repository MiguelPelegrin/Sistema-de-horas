package com.cronos.cronosystem.repository.Usuario;

import com.cronos.cronosystem.dto.UsuarioDto;
import com.cronos.cronosystem.repository.filter.UsuarioFilter;
import com.cronos.cronosystem.model.Usuario;
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

public class UsuarioRepositoryImpl implements UsuarioRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<UsuarioDto> filtrar(UsuarioFilter filter, Pageable pageable) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<UsuarioDto> criteria = builder.createQuery(UsuarioDto.class);
        Root<Usuario> root = criteria.from(Usuario.class);

        criteria.select(builder.construct(UsuarioDto.class,
                root.get("id"),
                root.get("nome"),
                root.get("email"),
                root.get("senha")
        ));

        Predicate[] predicates = criarRestricoes(filter, builder, root);

        criteria.where(predicates);
        criteria.orderBy(builder.asc(root.get("nome")));

        TypedQuery<UsuarioDto> query = manager.createQuery(criteria);
        addRestPag(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(filter));
    }

    private Long total(UsuarioFilter filter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Usuario> root = criteria.from(Usuario.class);

        Predicate[] predicates = criarRestricoes(filter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));

        return manager.createQuery(criteria).getSingleResult();
    }

    private void addRestPag(TypedQuery<UsuarioDto> query, Pageable pageable) {
        int pagAtual = pageable.getPageNumber();
        int totalRegPorPag = pageable.getPageSize();
        int primRegPag = pagAtual * totalRegPorPag;

        query.setFirstResult(primRegPag);
        query.setMaxResults(totalRegPorPag);

    }

    private Predicate[] criarRestricoes(UsuarioFilter filter, CriteriaBuilder builder, Root<Usuario> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(!StringUtils.isEmpty(filter.getNome())) {
            predicates.add(builder.like(builder.lower(root.get("nome")), "%" + filter.getNome().toLowerCase()));
        }

        return predicates.toArray(new Predicate[predicates.size()]);

    }
}

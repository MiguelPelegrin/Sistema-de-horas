package com.cronos.cronosystem.repository.query;


import com.cronos.cronosystem.dto.TurmaDto;
import com.cronos.cronosystem.filter.TurmaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TurmaRepositoryQuery {
    public Page<TurmaDto> filtrar(TurmaFilter filter, Pageable pageable);
}

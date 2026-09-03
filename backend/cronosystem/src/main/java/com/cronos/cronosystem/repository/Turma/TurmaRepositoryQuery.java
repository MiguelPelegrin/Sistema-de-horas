package com.cronos.cronosystem.repository.Turma;


import com.cronos.cronosystem.dto.TurmaDto;
import com.cronos.cronosystem.repository.filter.TurmaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TurmaRepositoryQuery {
    public Page<TurmaDto> filtrar(TurmaFilter filter, Pageable pageable);
}

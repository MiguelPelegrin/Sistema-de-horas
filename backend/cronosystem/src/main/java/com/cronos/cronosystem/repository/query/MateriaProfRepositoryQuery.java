package com.cronos.cronosystem.repository.query;

import com.cronos.cronosystem.dto.MateriaProfDto;
import com.cronos.cronosystem.filter.ProfFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MateriaProfRepositoryQuery {
    public Page<MateriaProfDto> filtrar(ProfFilter filter, Pageable pageable);
}

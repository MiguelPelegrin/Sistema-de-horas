package com.cronos.cronosystem.repository.query;

import com.cronos.cronosystem.dto.ProfDto;
import com.cronos.cronosystem.filter.ProfFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfRepositoryQuery {
    public Page<ProfDto> filtrar(ProfFilter filter, Pageable pageable);
}

package com.cronos.cronosystem.repository.Prof;

import com.cronos.cronosystem.dto.ProfDto;
import com.cronos.cronosystem.repository.filter.ProfFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfRepositoryQuery {
    public Page<ProfDto> filtrar(ProfFilter filter, Pageable pageable);
}

package com.cronos.cronosystem.repository.DispProf;

import com.cronos.cronosystem.dto.DispProfDto;
import com.cronos.cronosystem.repository.filter.DispProfFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DispProfRepositoryQuery {
    Page<DispProfDto> filtrar(DispProfFilter filter, Pageable pageable);
}

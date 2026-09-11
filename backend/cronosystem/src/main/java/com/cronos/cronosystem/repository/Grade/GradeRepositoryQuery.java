package com.cronos.cronosystem.repository.Grade;

import com.cronos.cronosystem.dto.GradeDto;
import com.cronos.cronosystem.repository.filter.GradeFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GradeRepositoryQuery {
    Page<GradeDto> filtrar(GradeFilter filter, Pageable pageable);
}

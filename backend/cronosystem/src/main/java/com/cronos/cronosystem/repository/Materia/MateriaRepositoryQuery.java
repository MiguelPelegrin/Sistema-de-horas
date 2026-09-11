package com.cronos.cronosystem.repository.Materia;

import com.cronos.cronosystem.dto.MateriaDto;
import com.cronos.cronosystem.repository.filter.MateriaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MateriaRepositoryQuery {
    Page<MateriaDto> filtrar(MateriaFilter filter, Pageable pageable);
}

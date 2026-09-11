package com.cronos.cronosystem.repository.MateriaProf;

import com.cronos.cronosystem.dto.MateriaProfDto;
import com.cronos.cronosystem.repository.filter.MateriaProfFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MateriaProfRepositoryQuery {
    public Page<MateriaProfDto> filtrar(MateriaProfFilter filter, Pageable pageable);
}
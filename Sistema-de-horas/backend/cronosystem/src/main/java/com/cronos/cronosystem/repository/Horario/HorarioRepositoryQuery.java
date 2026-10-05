package com.cronos.cronosystem.repository.Horario;

import com.cronos.cronosystem.dto.HorarioDto;
import com.cronos.cronosystem.repository.filter.HorarioFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HorarioRepositoryQuery {
    Page<HorarioDto> filtrar(HorarioFilter filter, Pageable pageable);
}

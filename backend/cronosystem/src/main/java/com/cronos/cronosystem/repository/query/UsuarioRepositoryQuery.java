package com.cronos.cronosystem.repository.query;

import com.cronos.cronosystem.dto.UsuarioDto;
import com.cronos.cronosystem.filter.UsuarioFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioRepositoryQuery {
    public Page<UsuarioDto> filtrar(UsuarioFilter filter, Pageable pageable);
}

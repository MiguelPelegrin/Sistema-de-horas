package com.cronos.cronosystem.repository.Usuario;

import com.cronos.cronosystem.dto.UsuarioDto;
import com.cronos.cronosystem.repository.filter.UsuarioFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UsuarioRepositoryQuery {
    public Page<UsuarioDto> filtrar(UsuarioFilter filter, Pageable pageable);
}

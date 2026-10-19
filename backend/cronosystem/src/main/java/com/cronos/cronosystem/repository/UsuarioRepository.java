package com.cronos.cronosystem.repository;


import com.cronos.cronosystem.model.Usuario;
import com.cronos.cronosystem.repository.Usuario.UsuarioRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuarioRepositoryQuery {
    public Optional<Usuario> findByEmail(String email);
}

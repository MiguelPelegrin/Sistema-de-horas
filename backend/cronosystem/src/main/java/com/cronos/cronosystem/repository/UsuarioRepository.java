package com.cronos.cronosystem.repository;


import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioDto extends JpaRepository<Usuario, Long> {

}

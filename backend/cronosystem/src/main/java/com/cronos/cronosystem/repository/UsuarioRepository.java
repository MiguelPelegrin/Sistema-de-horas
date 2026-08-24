package com.cronos.cronosystem.repository;


import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}

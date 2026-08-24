package br.com.auth.SistemaCadastramento.Repository;

import br.com.auth.SistemaCadastramento.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long > {
}

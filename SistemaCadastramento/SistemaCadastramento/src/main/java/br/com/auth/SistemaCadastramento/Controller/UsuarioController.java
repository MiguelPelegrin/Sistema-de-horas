package br.com.auth.SistemaCadastramento.Controller;

import br.com.auth.SistemaCadastramento.Model.Usuario;
import br.com.auth.SistemaCadastramento.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    //Para salvar uma informação
    @PostMapping
    public Usuario criarUsuario(@RequestBody Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    //Para buscar o usuario
    @GetMapping
    public List<Usuario> listarUsuarios(){
        return usuarioRepository.findAll();
    }

    //Para buscar o usuario
    @GetMapping("/{id}")
    public Usuario buscarUsuario(@PathVariable Long id){
        return usuarioRepository.findById(id).orElse(null);
    }

    //Para apagar
    @DeleteMapping("/{id}")
    public void excluirUsuario(@PathVariable Long id){
        usuarioRepository.deleteById(id);
    }

}

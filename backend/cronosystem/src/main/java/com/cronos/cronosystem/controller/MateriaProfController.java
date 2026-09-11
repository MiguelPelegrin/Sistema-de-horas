package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.MateriaProf;
import com.cronos.cronosystem.service.MateriaProfService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materia-prof")
public class MateriaProfController {

    private final MateriaProfService service;

    public MateriaProfController(MateriaProfService service) {
        this.service = service;
    }

    @GetMapping
    public List<MateriaProf> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public MateriaProf buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MateriaProf adicionar(@RequestBody @Valid MateriaProf materiaProf) {
        return service.salvar(materiaProf);
    }

    @PutMapping("/{id}")
    public MateriaProf alterar(@PathVariable Long id, @RequestBody @Valid MateriaProf materiaProf) {
        MateriaProf atual = service.buscaroufalhar(id);
        atual.setProf(materiaProf.getProf());
        atual.setMateria(materiaProf.getMateria());
        return service.salvar(atual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.buscaroufalhar(id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

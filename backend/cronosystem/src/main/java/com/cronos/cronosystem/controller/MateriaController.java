package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.Materia;
import com.cronos.cronosystem.service.MateriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materia")
public class MateriaController {

    private final MateriaService service;

    public MateriaController(MateriaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Materia> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Materia buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Materia adicionar(@RequestBody @Valid Materia materia) {
        return service.salvar(materia);
    }

    @PutMapping("/{id}")
    public Materia alterar(@PathVariable Long id, @RequestBody @Valid Materia materia) {
        Materia atual = service.buscaroufalhar(id);
        atual.setNome(materia.getNome());
        return service.salvar(atual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.buscaroufalhar(id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

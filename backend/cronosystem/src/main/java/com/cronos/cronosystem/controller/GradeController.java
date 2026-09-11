package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.Grade;
import com.cronos.cronosystem.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grade")
public class GradeController {

    private final GradeService service;

    public GradeController(GradeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Grade> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Grade buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Grade adicionar(@RequestBody @Valid Grade grade) {
        return service.salvar(grade);
    }

    @PutMapping("/{id}")
    public Grade alterar(@PathVariable Long id, @RequestBody @Valid Grade grade) {
        Grade atual = service.buscaroufalhar(id);
        atual.setTurma(grade.getTurma());
        atual.setMateriaProf(grade.getMateriaProf());
        atual.setHorario(grade.getHorario());
        return service.salvar(atual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.buscaroufalhar(id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

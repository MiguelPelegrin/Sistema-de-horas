package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.Turma;
import com.cronos.cronosystem.service.TurmaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turma")
public class TurmaController {

    private final TurmaService service;

    public TurmaController(TurmaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Turma> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Turma buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Turma adicionar(@RequestBody @Valid Turma turma) {
        return service.salvar(turma);
    }

    @PutMapping("/{id}")
    public Turma alterar(@PathVariable Long id, @RequestBody @Valid Turma turma) {
        Turma atual = service.buscaroufalhar(id);
        atual.setNome(turma.getNome());
        atual.setTurno(turma.getTurno());
        return service.salvar(atual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.buscaroufalhar(id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}


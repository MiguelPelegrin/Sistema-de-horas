package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.Divisao;
import com.cronos.cronosystem.service.DivisaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/divisao")
public class DivisaoController {

    @Autowired
    private DivisaoService service;

    @GetMapping
    public List<Divisao> listar() {
        return service.listar();
    }

    @GetMapping("/turma/{turmaId}")
    public List<Divisao> listarPorTurma(@PathVariable Long turmaId) {
        return service.listarPorTurma(turmaId);
    }

    @GetMapping("/{id}")
    public Divisao buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Divisao add(@RequestBody @Valid Divisao model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }
}

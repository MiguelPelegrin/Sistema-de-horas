package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.TurmaMateria;
import com.cronos.cronosystem.service.TurmaMateriaService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turma-materia")
public class TurmaMateriaController {

    @Autowired
    private TurmaMateriaService service;

    @GetMapping
    public List<TurmaMateria> listar() {
        return service.listar();
    }

    @GetMapping("/turma/{turmaId}")
    public List<TurmaMateria> listarPorTurma(@PathVariable Long turmaId) {
        return service.listarPorTurma(turmaId);
    }

    @GetMapping("/{id}")
    public TurmaMateria buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public TurmaMateria add(@RequestBody @Valid TurmaMateria model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public TurmaMateria atualizar(@PathVariable Long id, @RequestBody @Valid TurmaMateria model) {
        TurmaMateria modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id");
        return service.salvar(modelAtual);
    }
}

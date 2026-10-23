package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.TurmaService;
import com.cronos.cronosystem.model.Turma;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turma")
public class TurmaController {

    @Autowired
    private TurmaService service;

    @GetMapping
    public List<Turma> listar() {
        return service.listar();
    }

    @GetMapping("/pornome")
    public List<Turma> pesquisar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Turma buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Turma add(@RequestBody @Valid Turma model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public Turma atualizar(@PathVariable Long id, @RequestBody @Valid Turma model) {
        Turma modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id", "escola");
        return service.salvar(modelAtual);
    }
}

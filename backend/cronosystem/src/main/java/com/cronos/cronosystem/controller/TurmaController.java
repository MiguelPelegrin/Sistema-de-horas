package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.TurmaService;
import com.cronos.cronosystem.model.Turma;
import com.cronos.cronosystem.repository.TurmaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turma")
public class TurmaController {

    @Autowired
    private TurmaRepository repository;

    @Autowired
    private TurmaService service;

    @GetMapping
    public List<Turma> listar() {
        return repository.findAll();
    }

    @GetMapping("/pornome")
    public List<Turma> pesquisar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Turma buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Turma add(@RequestBody Turma model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public Turma atualizar(@PathVariable Long id, @RequestBody Turma model) {
        Turma modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id");
        return service.salvar(modelAtual);
    }
}

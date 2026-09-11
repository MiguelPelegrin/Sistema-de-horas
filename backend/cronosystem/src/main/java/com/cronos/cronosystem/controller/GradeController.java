package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.GradeService;
import com.cronos.cronosystem.model.Grade;
import com.cronos.cronosystem.repository.GradeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grade")
public class GradeController {

    @Autowired
    private GradeRepository repository;

    @Autowired
    private GradeService service;

    @GetMapping
    public List<Grade> listar() {
        return repository.findAll();
    }

    @GetMapping("/pornome")
    public List<Grade> pesquisar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Grade buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Grade add(@RequestBody Grade model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public Grade atualizar(@PathVariable Long id, @RequestBody Grade model) {
        Grade modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id");
        return service.salvar(modelAtual);
    }
}
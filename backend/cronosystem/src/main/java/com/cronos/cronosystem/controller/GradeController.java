package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.Grade;
import com.cronos.cronosystem.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grade")
public class GradeController {

    @Autowired
    private GradeService service;

    @GetMapping
    public List<Grade> listar() {
        return service.listar();
    }

    @GetMapping("/pornome")
    public List<Grade> pesquisar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Grade buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Grade add(@RequestBody @Valid Grade model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public Grade atualizar(@PathVariable Long id, @RequestBody @Valid Grade model) {
        Grade modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id", "escola");
        return service.salvar(modelAtual);
    }
}

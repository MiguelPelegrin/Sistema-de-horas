package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.ProfService;
import com.cronos.cronosystem.model.Prof;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prof")
public class ProfController {

    @Autowired
    private ProfService service;

    @GetMapping
    public List<Prof> listar() {
        return service.listar();
    }

    @GetMapping("/pornome")
    public List<Prof> pesquisar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Prof buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Prof add(@RequestBody @Valid Prof model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public Prof atualizar(@PathVariable Long id, @RequestBody @Valid Prof model) {
        Prof modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id", "escola");
        return service.salvar(modelAtual);
    }
}

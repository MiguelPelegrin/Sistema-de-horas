package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.MateriaService;
import com.cronos.cronosystem.model.Materia;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materia")
public class MateriaController {

    @Autowired
    private MateriaService service;

    @GetMapping
    public List<Materia> listar() {
        return service.listar();
    }

    @GetMapping("/pornome")
    public List<Materia> pesquisar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Materia buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Materia add(@RequestBody @Valid Materia model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public Materia atualizar(@PathVariable Long id, @RequestBody @Valid Materia model) {
        Materia modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id", "escola");
        return service.salvar(modelAtual);
    }
}

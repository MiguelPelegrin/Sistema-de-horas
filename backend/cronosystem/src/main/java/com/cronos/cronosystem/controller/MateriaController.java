package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.MateriaService;
import com.cronos.cronosystem.model.Materia;
import com.cronos.cronosystem.repository.MateriaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materia")
public class MateriaController {

    @Autowired
    private MateriaRepository repository;

    @Autowired
    private MateriaService service;

    @GetMapping
    public List<Materia> listar() {
        return repository.findAll();
    }

    @GetMapping("/pornome")
    public List<Materia> pesquisar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Materia buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Materia add(@RequestBody Materia model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public Materia atualizar(@PathVariable Long id, @RequestBody Materia model) {
        Materia modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id");
        return service.salvar(modelAtual);
    }
}

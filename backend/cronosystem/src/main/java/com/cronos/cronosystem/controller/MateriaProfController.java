package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.MateriaProfService;
import com.cronos.cronosystem.model.MateriaProf;
import com.cronos.cronosystem.repository.MateriaProfRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materia-prof")
public class MateriaProfController {

    @Autowired
    private MateriaProfRepository repository;

    @Autowired
    private MateriaProfService service;

    @GetMapping
    public List<MateriaProf> listar() {
        return repository.findAll();
    }

    @GetMapping("/pornome")
    public List<MateriaProf> pesquisar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public MateriaProf buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public MateriaProf add(@RequestBody MateriaProf model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public MateriaProf atualizar(@PathVariable Long id, @RequestBody MateriaProf model) {
        MateriaProf modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id");
        return service.salvar(modelAtual);
    }
}

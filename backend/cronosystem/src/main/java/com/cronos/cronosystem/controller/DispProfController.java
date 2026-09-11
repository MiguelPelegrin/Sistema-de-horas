package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.DispProfService;
import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.repository.DispProfRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disp-prof")
public class DispProfController {

    @Autowired
    private DispProfRepository repository;

    @Autowired
    private DispProfService service;

    @GetMapping
    public List<DispProf> listar() {
        return repository.findAll();
    }

    @GetMapping("/pornome")
    public List<DispProf> pesquisar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public DispProf buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public DispProf add(@RequestBody DispProf model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public DispProf atualizar(@PathVariable Long id, @RequestBody DispProf model) {
        DispProf modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id");
        return service.salvar(modelAtual);
    }
}

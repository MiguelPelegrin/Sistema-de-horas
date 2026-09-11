package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.HorarioService;
import com.cronos.cronosystem.model.Horario;
import com.cronos.cronosystem.repository.HorarioRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horario")
public class HorarioController {

    @Autowired
    private HorarioRepository repository;

    @Autowired
    private HorarioService service;

    @GetMapping
    public List<Horario> listar() {
        return repository.findAll();
    }

    @GetMapping("/pornome")
    public List<Horario> pesquisar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Horario buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public Horario add(@RequestBody Horario model) {
        return service.salvar(model);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public Horario atualizar(@PathVariable Long id, @RequestBody Horario model) {
        Horario modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id");
        return service.salvar(modelAtual);
    }
}

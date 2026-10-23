package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.service.DispProfService;
import com.cronos.cronosystem.dto.DisponibilidadeProfDto;
import com.cronos.cronosystem.model.DispProf;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disp-prof")
public class DispProfController {

    @Autowired
    private DispProfService service;

    @GetMapping
    public List<DispProf> listar() {
        return service.listar();
    }

    @GetMapping("/pornome")
    public List<DispProf> pesquisar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public DispProf buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    public DispProf add(@RequestBody @Valid DispProf model) {
        return service.salvar(model);
    }

    @GetMapping("/professor/{profId}")
    public List<DispProf> listarPorProfessor(@PathVariable Long profId) {
        return service.listarPorProfessor(profId);
    }

    @PostMapping("/professor/{profId}")
    public List<DispProf> adicionarDisponibilidade(
            @PathVariable Long profId,
            @RequestBody List<DisponibilidadeProfDto> disponibilidades) {
        return service.salvarDisponibilidade(profId, disponibilidades);
    }

    @PutMapping("/professor/{profId}")
    public List<DispProf> substituirDisponibilidade(
            @PathVariable Long profId,
            @RequestBody List<DisponibilidadeProfDto> disponibilidades) {
        return service.substituirDisponibilidade(profId, disponibilidades);
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public DispProf atualizar(@PathVariable Long id, @RequestBody @Valid DispProf model) {
        DispProf modelAtual = service.buscaroufalhar(id);
        BeanUtils.copyProperties(model, modelAtual, "id");
        return service.salvar(modelAtual);
    }
}

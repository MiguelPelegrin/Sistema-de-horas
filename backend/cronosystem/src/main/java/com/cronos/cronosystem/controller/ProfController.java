package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.Prof;
import com.cronos.cronosystem.service.ProfService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prof")
public class ProfController {

    private final ProfService service;

    public ProfController(ProfService service) {
        this.service = service;
    }

    @GetMapping
    public List<Prof> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Prof buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Prof adicionar(@RequestBody @Valid Prof prof) {
        return service.salvar(prof);
    }

    @PutMapping("/{id}")
    public Prof alterar(@PathVariable Long id, @RequestBody @Valid Prof prof) {
        Prof atual = service.buscaroufalhar(id);
        atual.setNome(prof.getNome());
        atual.setChm(prof.getChm());
        return service.salvar(atual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.buscaroufalhar(id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

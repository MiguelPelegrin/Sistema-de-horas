package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.DispProf;
import com.cronos.cronosystem.service.DispProfService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disp-prof")
public class DispProfController {

    private final DispProfService service;

    public DispProfController(DispProfService service) {
        this.service = service;
    }

    @GetMapping
    public List<DispProf> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public DispProf buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DispProf adicionar(@RequestBody @Valid DispProf dispProf) {
        return service.salvar(dispProf);
    }

    @PutMapping("/{id}")
    public DispProf alterar(@PathVariable Long id, @RequestBody @Valid DispProf dispProf) {
        DispProf atual = service.buscaroufalhar(id);
        atual.setProf(dispProf.getProf());
        atual.setHorario(dispProf.getHorario());
        return service.salvar(atual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.buscaroufalhar(id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

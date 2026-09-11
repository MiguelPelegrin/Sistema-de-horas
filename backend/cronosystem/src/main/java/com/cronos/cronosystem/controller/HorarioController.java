package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.Horario;
import com.cronos.cronosystem.service.HorarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horario")
public class HorarioController {

    private final HorarioService service;

    public HorarioController(HorarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Horario> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Horario buscar(@PathVariable Long id) {
        return service.buscaroufalhar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Horario adicionar(@RequestBody @Valid Horario horario) {
        return service.salvar(horario);
    }

    @PutMapping("/{id}")
    public Horario alterar(@PathVariable Long id, @RequestBody @Valid Horario horario) {
        Horario atual = service.buscaroufalhar(id);
        atual.setDiaSemana(horario.getDiaSemana());
        atual.setTempo_aula(horario.getTempo_aula());
        atual.setTurmasep(horario.getTurmasep());
        return service.salvar(atual);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.buscaroufalhar(id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

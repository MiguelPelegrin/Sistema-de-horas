package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.model.Escola;
import com.cronos.cronosystem.service.EscolaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/escola")
public class EscolaController {

    @Autowired
    private EscolaService service;

    @GetMapping
    public Escola buscar() {
        return service.buscar();
    }

    @PutMapping
    public Escola atualizar(@RequestBody @Valid Escola model) {
        return service.atualizar(model);
    }
}

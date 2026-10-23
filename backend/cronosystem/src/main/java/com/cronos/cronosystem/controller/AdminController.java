package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.dto.EscolaCadastroDto;
import com.cronos.cronosystem.dto.EscolaResumoDto;
import com.cronos.cronosystem.service.EscolaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private EscolaService escolaService;

    @GetMapping("/escola")
    public List<EscolaResumoDto> listarEscolas() {
        return escolaService.listar();
    }

    @PostMapping("/escola")
    public EscolaResumoDto cadastrarEscola(@RequestBody @Valid EscolaCadastroDto dados) {
        return escolaService.cadastrar(dados);
    }
}

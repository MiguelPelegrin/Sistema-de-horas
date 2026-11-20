package com.cronos.cronosystem.controller;

import com.cronos.cronosystem.dto.PlanilhaDto;
import com.cronos.cronosystem.model.Planilha;
import com.cronos.cronosystem.service.PlanilhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/planilha")
public class PlanilhaController {

    @Autowired
    private PlanilhaService service;

    @GetMapping
    public List<PlanilhaDto> listar() {
        return service.listar();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PlanilhaDto add(@RequestParam("arquivo") MultipartFile arquivo,
                           @RequestParam(defaultValue = "false") boolean completa,
                           @RequestParam(defaultValue = "0") int aulasPendentes,
                           @RequestParam(defaultValue = "false") boolean solucaoSugerida) {
        return service.salvar(arquivo, completa, aulasPendentes, solucaoSugerida);
    }

    @GetMapping("/{id}/arquivo")
    public ResponseEntity<byte[]> baixar(@PathVariable Long id) {
        Planilha planilha = service.buscaroufalhar(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(planilha.getNomeArquivo(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(planilha.getArquivo());
    }

    @DeleteMapping("/{id}")
    public void remover(@PathVariable Long id) {
        service.excluir(id);
    }
}

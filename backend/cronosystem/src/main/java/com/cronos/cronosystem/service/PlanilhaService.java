package com.cronos.cronosystem.service;

import com.cronos.cronosystem.dto.PlanilhaDto;
import com.cronos.cronosystem.exception.RegraNegocioException;
import com.cronos.cronosystem.model.Planilha;
import com.cronos.cronosystem.repository.PlanilhaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlanilhaService {

    private static final int LIMITE_HISTORICO = 30;

    private static final long TAMANHO_MAXIMO = 5 * 1024 * 1024;

    @Autowired
    private PlanilhaRepository repository;

    @Autowired
    private EscolaService escolaService;

    public List<PlanilhaDto> listar() {
        return repository.listarResumo(escolaService.escolaLogadaId());
    }

    @Transactional
    public PlanilhaDto salvar(MultipartFile arquivo, boolean completa, int aulasPendentes, boolean solucaoSugerida) {
        byte[] conteudo = lerArquivo(arquivo);

        Planilha planilha = new Planilha();
        planilha.setNomeArquivo(nomeSeguro(arquivo.getOriginalFilename()));
        planilha.setArquivo(conteudo);
        planilha.setCompleta(completa);
        planilha.setAulasPendentes(Math.max(0, aulasPendentes));
        planilha.setSolucaoSugerida(solucaoSugerida);
        planilha.setDataGeracao(LocalDateTime.now());
        planilha.setEscola(escolaService.escolaLogada());
        planilha = repository.save(planilha);

        List<Long> ids = repository.listarIds(planilha.getEscola().getId());

        if (ids.size() > LIMITE_HISTORICO) {
            repository.deleteAllById(ids.subList(LIMITE_HISTORICO, ids.size()));
        }

        return resumo(planilha);
    }

    public Planilha buscaroufalhar(Long yId) {
        return repository.findByIdAndEscola_Id(yId, escolaService.escolaLogadaId())
                .orElseThrow(() -> new EntityNotFoundException("Planilha não encontrada com esse ID."));
    }

    @Transactional
    public void excluir(Long yId) {
        repository.delete(buscaroufalhar(yId));
    }

    private byte[] lerArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RegraNegocioException("Envie o arquivo da planilha.");
        }

        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new RegraNegocioException("A planilha pode ter no máximo 5 MB.");
        }

        try {
            byte[] conteudo = arquivo.getBytes();

            if (conteudo.length < 4 || conteudo[0] != 'P' || conteudo[1] != 'K') {
                throw new RegraNegocioException("O arquivo enviado não é uma planilha do Excel (.xlsx).");
            }

            return conteudo;
        } catch (IOException erro) {
            throw new RegraNegocioException("Não foi possível ler o arquivo da planilha.");
        }
    }

    private String nomeSeguro(String nome) {
        String limpo = nome == null ? "" : nome.replaceAll("[\\\\/:*?\"<>|\\r\\n]", "").trim();

        if (limpo.isEmpty()) {
            limpo = "Grade_Escolar";
        }

        if (!limpo.toLowerCase().endsWith(".xlsx")) {
            limpo = limpo + ".xlsx";
        }

        return limpo.length() > 255 ? limpo.substring(limpo.length() - 255) : limpo;
    }

    private PlanilhaDto resumo(Planilha planilha) {
        return new PlanilhaDto(planilha.getId(), planilha.getNomeArquivo(), planilha.getCompleta(),
                planilha.getAulasPendentes(), planilha.getSolucaoSugerida(), planilha.getDataGeracao());
    }
}

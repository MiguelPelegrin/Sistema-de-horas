package com.cronos.cronosystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "planilha")
public class Planilha {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @JsonIgnore
    @Lob
    @Column(name = "arquivo", columnDefinition = "LONGBLOB")
    private byte[] arquivo;

    private Boolean completa;

    @Column(name = "aulas_pendentes")
    private Integer aulasPendentes;

    @Column(name = "solucao_sugerida")
    private Boolean solucaoSugerida;

    @Column(name = "data_geracao")
    private LocalDateTime dataGeracao;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_escola")
    private Escola escola;
}

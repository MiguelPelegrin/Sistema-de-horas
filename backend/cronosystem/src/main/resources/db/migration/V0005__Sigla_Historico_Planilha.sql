ALTER TABLE materia
    ADD COLUMN sigla VARCHAR(20);

ALTER TABLE materia
    DROP COLUMN cor;

CREATE TABLE planilha (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_arquivo VARCHAR(255) NOT NULL,
    arquivo LONGBLOB NOT NULL,
    completa BOOLEAN NOT NULL,
    aulas_pendentes INT NOT NULL,
    solucao_sugerida BOOLEAN NOT NULL,
    data_geracao DATETIME NOT NULL,
    id_escola BIGINT NOT NULL,

    CONSTRAINT fk_planilha_escola FOREIGN KEY (id_escola) REFERENCES escola(id)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

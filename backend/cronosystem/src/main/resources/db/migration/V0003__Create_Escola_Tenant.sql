ALTER TABLE escola
    ADD COLUMN semestre VARCHAR(100),
    ADD COLUMN turno VARCHAR(50),
    ADD COLUMN dias_letivos VARCHAR(100);

ALTER TABLE usuario
    ADD COLUMN id_escola BIGINT,
    ADD CONSTRAINT uk_usuario_email UNIQUE (email),
    ADD CONSTRAINT fk_usuario_escola FOREIGN KEY (id_escola) REFERENCES escola(id);

ALTER TABLE prof
    ADD COLUMN id_escola BIGINT NOT NULL,
    ADD CONSTRAINT fk_prof_escola FOREIGN KEY (id_escola) REFERENCES escola(id);

ALTER TABLE materia
    ADD COLUMN cor VARCHAR(20),
    ADD COLUMN laboratorio VARCHAR(100),
    ADD COLUMN id_escola BIGINT NOT NULL,
    ADD CONSTRAINT fk_materia_escola FOREIGN KEY (id_escola) REFERENCES escola(id);

ALTER TABLE turma
    ADD COLUMN id_escola BIGINT NOT NULL,
    ADD CONSTRAINT fk_turma_escola FOREIGN KEY (id_escola) REFERENCES escola(id);

ALTER TABLE horario
    ADD COLUMN id_escola BIGINT NOT NULL,
    ADD CONSTRAINT fk_horario_escola FOREIGN KEY (id_escola) REFERENCES escola(id);

ALTER TABLE grade
    ADD COLUMN id_escola BIGINT NOT NULL,
    ADD CONSTRAINT fk_grade_escola FOREIGN KEY (id_escola) REFERENCES escola(id);

ALTER TABLE labs
    ADD COLUMN id_escola BIGINT NOT NULL,
    ADD CONSTRAINT fk_labs_escola FOREIGN KEY (id_escola) REFERENCES escola(id);

CREATE TABLE turma_materia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_turma BIGINT NOT NULL,
    id_materia BIGINT NOT NULL,
    qtd_aulas INT NOT NULL,

    CONSTRAINT fk_turma_materia_turma FOREIGN KEY (id_turma) REFERENCES turma(id),
    CONSTRAINT fk_turma_materia_materia FOREIGN KEY (id_materia) REFERENCES materia(id)
);

CREATE TABLE divisao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_turma BIGINT NOT NULL,
    id_materia_a BIGINT NOT NULL,
    id_materia_b BIGINT NOT NULL,

    CONSTRAINT fk_divisao_turma FOREIGN KEY (id_turma) REFERENCES turma(id),
    CONSTRAINT fk_divisao_materia_a FOREIGN KEY (id_materia_a) REFERENCES materia(id),
    CONSTRAINT fk_divisao_materia_b FOREIGN KEY (id_materia_b) REFERENCES materia(id)
);

INSERT INTO usuario (nome, email, senha, admin)
VALUES ('Administrador', 'admin@cronos.com', '$2a$10$hE06Deo2pUNjqeRsyhWj6.4ze1vce1vIW/wfdzAdsZJFKNgMkh/bO', TRUE);

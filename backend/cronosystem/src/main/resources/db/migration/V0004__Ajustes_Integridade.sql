ALTER TABLE escola CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE materia CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE usuario CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE turma CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE prof CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE horario CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE labs CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE disp_lab CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE permissao CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE materia
    ADD CONSTRAINT uk_materia_nome_escola UNIQUE (id_escola, nome);

ALTER TABLE horario
    ADD CONSTRAINT uk_horario_escola UNIQUE (id_escola, dia_semana, hora_inicio, hora_fim);

ALTER TABLE disp_prof
    ADD CONSTRAINT uk_disp_prof UNIQUE (id_prof, id_horario);

ALTER TABLE materia_prof
    ADD CONSTRAINT uk_materia_prof UNIQUE (id_prof, id_materia);

ALTER TABLE turma_materia
    ADD CONSTRAINT uk_turma_materia UNIQUE (id_turma, id_materia);

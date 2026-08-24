CREATE TABLE materia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE turma (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    turno VARCHAR(50)
);

CREATE TABLE prof (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    carga_horaria_maxima INT
);

CREATE TABLE horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dia_semana VARCHAR(20),
    tempo_aula INT DEFAULT 50,
    turmasep VARCHAR(20)
);

CREATE TABLE materia_prof (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_prof BIGINT,
    id_materia BIGINT,
    CONSTRAINT fk_materia_prof_prof FOREIGN KEY (id_prof) REFERENCES prof(id),
    CONSTRAINT fk_materia_prof_materia FOREIGN KEY (id_materia) REFERENCES materia(id)
);

CREATE TABLE grade (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_turma BIGINT,
    id_materia_prof BIGINT,
    id_horario BIGINT,
    CONSTRAINT fk_grade_turma FOREIGN KEY (id_turma) REFERENCES turma(id),
    CONSTRAINT fk_grade_materia_prof FOREIGN KEY (id_materia_prof) REFERENCES materia_prof(id),
    CONSTRAINT fk_grade_horario FOREIGN KEY (id_horario) REFERENCES horario(id)
);

CREATE TABLE disp_prof (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_prof BIGINT,
    id_horario BIGINT,
    CONSTRAINT fk_disp_prof_prof FOREIGN KEY (id_prof) REFERENCES prof(id),
    CONSTRAINT fk_disp_prof_horario FOREIGN KEY (id_horario) REFERENCES horario(id)
);

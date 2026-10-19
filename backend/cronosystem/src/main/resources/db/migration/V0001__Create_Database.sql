CREATE TABLE escola (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(225),
    coordenacao VARCHAR(255)
);

CREATE TABLE materia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    admin BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE turma (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    turno VARCHAR(50)
);

CREATE TABLE prof (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cor VARCHAR(20),
    carga_horaria_maxima INT
);

CREATE TABLE horario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dia_semana VARCHAR(20),
    tempo_aula INT DEFAULT 50,
    hora_inicio TIME,
    hora_fim TIME,
    turmasep VARCHAR(20)
);

CREATE TABLE materia_prof (
    id_prof BIGINT not null,
    id_materia BIGINT not null,

    primary key(id_prof, id_materia),

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
    id_prof BIGINT not null,
    id_horario BIGINT not null,

    primary key(id_prof, id_horario),

    CONSTRAINT fk_disp_prof_prof FOREIGN KEY (id_prof) REFERENCES prof(id),
    CONSTRAINT fk_disp_prof_horario FOREIGN KEY (id_horario) REFERENCES horario(id)
);

CREATE TABLE labs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(225)
);

CREATE TABLE disp_lab (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
<<<<<<< HEAD
    horario varchar(20),
    dia_semana varchar(20),
    id_materia BIGINT,

    CONSTRAINT fk_materia_disp_lab FOREIGN KEY(id_materia) REFERENCES materia(id)
);
=======
    horario VARCHAR(20),
    dia_semana VARCHAR(20),
    id_materia BIGINT,
    CONSTRAINT fk_materia_disp_lab FOREIGN KEY(id_materia) REFERENCES materia(id)
);
>>>>>>> 4924645349a55d0560c6f21c3105f99f822e3328

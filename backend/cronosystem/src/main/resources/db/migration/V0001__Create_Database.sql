CREATE TABLE materia (
    id INT PRIMARY KEY,
    nome VARCHAR(255)
);

CREATE TABLE usuario (
    id INT PRIMARY KEY,
    nome VARCHAR(255),
    email VARCHAR(255),
    senha VARCHAR(255),
);

CREATE TABLE turma (
    id INT PRIMARY KEY,
    nome VARCHAR(255),
    turno VARCHAR(50),
);

CREATE TABLE prof (
    id INT PRIMARY KEY,
    nome VARCHAR(255),
    carga_horaria_maxima INT,
);

CREATE TABLE horario (
    id INT PRIMARY KEY,
    dia_semana ENUM("seg", "ter", "qua", "qui", "sex"),
    tempo_aula INT DEFAULT 50,
    turmasep ENUM('junto', 'turmaA', 'turmaB'),
);

CREATE TABLE materia_prof (
    id INT PRIMARY KEY,
    id_prof INT,
    id_materia INT,
    FOREIGN KEY (id_prof) REFERENCES prof(id),
    FOREIGN KEY (id_materia) REFERENCES materia(id)
);

CREATE TABLE grade (
    id INT PRIMARY KEY,
    id_turma INT,
    id_materia_prof INT,
    id_horario INT,
    FOREIGN KEY (id_turma) REFERENCES turma(id),
    FOREIGN KEY (id_materia_prof) REFERENCES materia_prof(id),
    FOREIGN KEY (id_horario) REFERENCES horario(id)
);

CREATE TABLE disp_prof (
    id INT PRIMARY KEY,
    id_prof INT,
    id_horario INT,
    FOREIGN KEY (id_prof) REFERENCES prof(id),
    FOREIGN KEY (id_horario) REFERENCES horario(id)
);
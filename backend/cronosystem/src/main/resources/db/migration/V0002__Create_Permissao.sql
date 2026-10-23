create table permissao(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao varchar(255)
);

create table usuario_permissao (
    id_usuario bigint not null,
    id_permissao bigint not null,

    primary key(id_permissao, id_usuario),
    CONSTRAINT fk_usuario_usuario_permissao FOREIGN KEY(id_usuario) REFERENCES usuario(id),
    CONSTRAINT fk_permissao_usuario_permissao FOREIGN KEY(id_permissao) REFERENCES permissao(id)
);

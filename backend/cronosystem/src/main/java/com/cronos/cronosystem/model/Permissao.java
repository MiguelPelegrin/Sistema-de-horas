package com.cronos.cronosystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "permissao")
public class Permissao {

    @EqualsAndHashCode.Include
    @Id
    private Long id;

    private String descricao;

}

package br.com.auth.SistemaCadastramento.Model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;

public class Usuario {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(length = 45)
    private String usuario;

    @NotBlank
    @Column(length = 100)
    private String email;

    @NotBlank
    @Column(length = 45)
    private String senha;
}

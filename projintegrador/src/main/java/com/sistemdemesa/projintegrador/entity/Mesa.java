package com.sistemdemesa.projintegrador.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "mesas")
public class Mesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @NotNull
    @Min(1)
    private Integer numero;
    @NotNull
    @Min(1)
    private Integer quantidade = 4;

    public Long getId() {
        return id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer v) {
        numero = v;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer v) {
        quantidade = v;
    }
}

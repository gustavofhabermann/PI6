package com.sistemdemesa.projintegrador.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 100)
    private String nome;
    @Column(name = "preco_compra")
    @DecimalMin("0")
    private BigDecimal precoCompra;
    @Column(name = "preco_venda", nullable = false)
    @DecimalMin("0")
    private BigDecimal precoVenda;
    @Min(0)
    private Integer quantidade = 0;
    @Column(nullable = false)
    private String categoria = "diversos";

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String v) {
        nome = v;
    }

    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(BigDecimal v) {
        precoCompra = v;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal v) {
        precoVenda = v;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer v) {
        quantidade = v;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String v) {
        categoria = v;
    }
}

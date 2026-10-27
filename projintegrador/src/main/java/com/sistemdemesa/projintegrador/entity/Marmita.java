package com.sistemdemesa.projintegrador.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "marmitas")
public class Marmita {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nome_cliente")
    @NotBlank
    private String nomeCliente;
    @NotBlank
    private String telefone;
    @NotBlank
    private String endereco;
    @NotBlank
    private String tipo;
    @DecimalMin("0")
    private BigDecimal preco;
    @NotBlank
    @Pattern(regexp = "preparando|pronto|entregue")
    private String status = "preparando";

    public Long getId() {
        return id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String v) {
        nomeCliente = v;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String v) {
        telefone = v;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String v) {
        endereco = v;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String v) {
        tipo = v;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal v) {
        preco = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        status = v;
    }
}

package com.sistemdemesa.projintegrador.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pedido_produtos")
public class PedidoProduto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id")
    private Produto produto;
    private Integer quantidade;
    @Column(name = "preco_unitario_vendido")
    private BigDecimal precoUnitarioVendido;
    private BigDecimal subtotal;

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido v) {
        pedido = v;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto v) {
        produto = v;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer v) {
        quantidade = v;
    }

    public BigDecimal getPrecoUnitarioVendido() {
        return precoUnitarioVendido;
    }

    public void setPrecoUnitarioVendido(BigDecimal v) {
        precoUnitarioVendido = v;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal v) {
        subtotal = v;
    }
}

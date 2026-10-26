package com.sistemdemesa.projintegrador.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "pedidos")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "mesa_id")
    private Mesa mesa;
    private String status = "aberto";
    private BigDecimal total = BigDecimal.ZERO;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoProduto> produtosRelacao = new ArrayList<>();

    @PrePersist
    void pre() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa v) {
        mesa = v;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String v) {
        status = v;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal v) {
        total = v;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<PedidoProduto> getProdutosRelacao() {
        return produtosRelacao;
    }
}

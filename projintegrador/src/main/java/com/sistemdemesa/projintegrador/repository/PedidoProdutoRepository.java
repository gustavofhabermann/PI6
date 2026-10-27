package com.sistemdemesa.projintegrador.repository;

import com.sistemdemesa.projintegrador.entity.PedidoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoProdutoRepository extends JpaRepository<PedidoProduto, Long> {
    boolean existsByProduto_Id(Long produtoId);
}

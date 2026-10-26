package com.sistemdemesa.projintegrador.repository;

import com.sistemdemesa.projintegrador.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}

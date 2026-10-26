package com.sistemdemesa.projintegrador.repository;

import com.sistemdemesa.projintegrador.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}

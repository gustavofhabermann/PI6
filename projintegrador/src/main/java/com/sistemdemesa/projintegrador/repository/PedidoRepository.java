package com.sistemdemesa.projintegrador.repository;

import com.sistemdemesa.projintegrador.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByStatus(String status);

    boolean existsByMesa_Id(Long mesaId);
}

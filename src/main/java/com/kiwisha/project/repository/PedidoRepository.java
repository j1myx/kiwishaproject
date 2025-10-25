package com.kiwisha.project.repository;

import com.kiwisha.project.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pedido.
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    /**
     * Busca un pedido por su código único
     */
    Optional<Pedido> findByCodigo(String codigo);

    /**
     * Obtiene pedidos por cliente
     */
    Page<Pedido> findByClienteClienteIdOrderByCreadoEnDesc(Integer clienteId, Pageable pageable);

    /**
     * Obtiene pedidos por cliente (alternativa para pageable)
     */
    Page<Pedido> findByClienteClienteId(Integer clienteId, Pageable pageable);

    /**
     * Obtiene pedidos por email
     */
    Page<Pedido> findByEmail(String email, Pageable pageable);

    /**
     * Obtiene pedidos por estado
     */
    Page<Pedido> findByEstado(Pedido.EstadoPedido estado, Pageable pageable);

    /**
     * Obtiene pedidos en un rango de fechas
     */
    @Query("SELECT p FROM Pedido p WHERE p.creadoEn BETWEEN :fechaInicio AND :fechaFin ORDER BY p.creadoEn DESC")
    List<Pedido> findByRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                    @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Obtiene los últimos N pedidos
     */
    List<Pedido> findTop10ByOrderByCreadoEnDesc();

    /**
     * Cuenta pedidos por cliente
     */
    long countByClienteClienteId(Integer clienteId);

    /**
     * Cuenta pedidos por estado
     */
    long countByEstado(Pedido.EstadoPedido estado);
}

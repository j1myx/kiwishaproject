package com.kiwisha.project.repository;

import com.kiwisha.project.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad CarritoItem.
 */
@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Integer> {

    /**
     * Obtiene items del carrito por sesión
     */
    List<CarritoItem> findBySesionId(String sesionId);

    /**
     * Obtiene items del carrito por cliente
     */
    List<CarritoItem> findByClienteClienteId(Integer clienteId);

    /**
     * Busca un item específico en el carrito
     */
    Optional<CarritoItem> findBySesionIdAndProductoProductoId(String sesionId, Integer productoId);

    /**
     * Elimina items del carrito por sesión
     */
    void deleteBySesionId(String sesionId);

    /**
     * Cuenta items en el carrito por sesión
     */
    long countBySesionId(String sesionId);
}

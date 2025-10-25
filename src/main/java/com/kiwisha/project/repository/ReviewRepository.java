package com.kiwisha.project.repository;

import com.kiwisha.project.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Review.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /**
     * Obtiene reviews aprobadas de un producto
     */
    Page<Review> findByProductoProductoIdAndAprobadoTrueAndActivoTrueOrderByCreadoEnDesc(
            Integer productoId, Pageable pageable);

    /**
     * Obtiene reviews pendientes de aprobación
     */
    List<Review> findByAprobadoFalseOrderByCreadoEnAsc();

    /**
     * Obtiene reviews pendientes de aprobación con paginación
     */
    Page<Review> findByAprobadoFalseAndActivoTrueOrderByCreadoEnDesc(Pageable pageable);

    /**
     * Obtiene reviews de un cliente
     */
    List<Review> findByClienteClienteIdOrderByCreadoEnDesc(Integer clienteId);

    /**
     * Cuenta reviews aprobadas de un producto
     */
    long countByProductoProductoIdAndAprobadoTrueAndActivoTrue(Integer productoId);

    /**
     * Calcula el promedio de calificación de un producto
     */
    @Query("SELECT AVG(r.calificacion) FROM Review r WHERE r.producto.productoId = :productoId AND r.aprobado = true AND r.activo = true")
    Double obtenerPromedioCalificacion(@Param("productoId") Integer productoId);

    /**
     * Calcula el promedio de calificación de un producto (nombre alternativo)
     */
    @Query("SELECT AVG(r.calificacion) FROM Review r WHERE r.producto.productoId = :productoId AND r.aprobado = true AND r.activo = true")
    Double calcularPromedioCalificacion(@Param("productoId") Integer productoId);

    /**
     * Verifica si un cliente ya ha dejado una review para un producto
     */
    boolean existsByProductoProductoIdAndClienteClienteId(Integer productoId, Integer clienteId);
}

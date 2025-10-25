package com.kiwisha.project.repository;

import com.kiwisha.project.model.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cupon.
 */
@Repository
public interface CuponRepository extends JpaRepository<Cupon, Integer> {

    /**
     * Busca un cupón por su código
     */
    Optional<Cupon> findByCodigo(String codigo);

    /**
     * Busca un cupón activo por su código
     */
    Optional<Cupon> findByCodigoAndActivoTrue(String codigo);

    /**
     * Obtiene cupones válidos actualmente
     */
    @Query("SELECT c FROM Cupon c WHERE c.activo = true " +
           "AND c.fechaInicio <= :fecha AND c.fechaFin >= :fecha " +
           "AND (c.cantidadMaximaUsos IS NULL OR c.cantidadUsosActual < c.cantidadMaximaUsos)")
    List<Cupon> findCuponesValidos(@Param("fecha") LocalDateTime fecha);

    /**
     * Verifica si existe un cupón con el código dado
     */
    boolean existsByCodigo(String codigo);
}

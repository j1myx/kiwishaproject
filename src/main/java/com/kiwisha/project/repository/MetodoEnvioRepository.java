package com.kiwisha.project.repository;

import com.kiwisha.project.model.MetodoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad MetodoEnvio.
 */
@Repository
public interface MetodoEnvioRepository extends JpaRepository<MetodoEnvio, Integer> {

    /**
     * Obtiene métodos de envío activos
     */
    List<MetodoEnvio> findByActivoTrue();

    /**
     * Busca un método de envío por nombre
     */
    List<MetodoEnvio> findByNombreContainingIgnoreCase(String nombre);
}

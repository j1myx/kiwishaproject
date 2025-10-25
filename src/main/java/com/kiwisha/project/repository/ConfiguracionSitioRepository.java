package com.kiwisha.project.repository;

import com.kiwisha.project.model.ConfiguracionSitio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad ConfiguracionSitio.
 */
@Repository
public interface ConfiguracionSitioRepository extends JpaRepository<ConfiguracionSitio, Integer> {

    /**
     * Busca una configuración por su clave
     */
    Optional<ConfiguracionSitio> findByClave(String clave);

    /**
     * Obtiene todas las configuraciones activas
     */
    List<ConfiguracionSitio> findByActivoTrueOrderByClaveAsc();

    /**
     * Obtiene configuraciones editables
     */
    List<ConfiguracionSitio> findByEditableTrueOrderByClaveAsc();

    /**
     * Verifica si existe una configuración con la clave dada
     */
    boolean existsByClave(String clave);
}

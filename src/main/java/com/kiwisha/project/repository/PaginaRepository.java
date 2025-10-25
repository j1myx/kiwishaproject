package com.kiwisha.project.repository;

import com.kiwisha.project.model.Pagina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Pagina.
 */
@Repository
public interface PaginaRepository extends JpaRepository<Pagina, Integer> {

    /**
     * Busca una página por su URL
     */
    Optional<Pagina> findByUrl(String url);

    /**
     * Obtiene páginas publicadas por tipo
     */
    Page<Pagina> findByTipoAndPublicadoTrueOrderByCreadoEnDesc(
            Pagina.TipoPagina tipo, Pageable pageable);

    /**
     * Obtiene todas las páginas publicadas
     */
    List<Pagina> findByPublicadoTrueOrderByCreadoEnDesc();

    /**
     * Verifica si existe una página con la URL dada
     */
    boolean existsByUrl(String url);

    /**
     * Verifica si existe una página con la URL (excluyendo un ID específico)
     */
    boolean existsByUrlAndPaginaIdNot(String url, Integer paginaId);
}

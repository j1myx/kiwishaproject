package com.kiwisha.project.repository;

import com.kiwisha.project.model.Pagina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Pagina.
 */
@Repository
public interface PaginaRepository extends JpaRepository<Pagina, Integer> {

    @Query(
            value = """
                        FROM Pagina p
                        LEFT JOIN FETCH p.etiquetas e
                        WHERE (:titulo IS NULL OR :titulo = '') OR p.titulo = :titulo
                    """,
            countQuery = "SELECT count(*) FROM Pagina"
    )
    Page<Pagina> findAll(@Param("titulo") String titulo, Pageable pageable);

    @Query(
            value = """
                        FROM Pagina p
                        LEFT JOIN FETCH p.paginaImagenes
                        WHERE p.publicado = true AND p.tipo = 'NOTICIAS'
                        ORDER BY p.creadoEn DESC
                        LIMIT 3
                    """
    )
    List<Pagina> getTops();

    /**
     * Busca una página por su URL
     */
    @Query(
            value = """
                        FROM Pagina p
                        LEFT JOIN FETCH p.paginaImagenes
                        WHERE p.url = :url
                    """
    )
    Pagina findByUrl(String url);

    @Query(
            value = """
                        FROM Pagina p
                        LEFT JOIN FETCH p.paginaImagenes
                        WHERE p.paginaId = :paginaId
                    """
    )
    Pagina findByPaginaId(Integer paginaId);

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

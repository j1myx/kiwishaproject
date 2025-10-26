package com.kiwisha.project.repository;

import com.kiwisha.project.model.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Producto.
 * Proporciona métodos de consulta personalizados para productos.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    /**
     * Busca un producto por su slug
     */
    Optional<Producto> findBySlug(String slug);

    /**
     * Busca un producto por su SKU
     */
    Optional<Producto> findBySku(String sku);

    /**
     * Obtiene productos publicados
     */
    Page<Producto> findByPublicadoTrue(Pageable pageable);

    /**
     * Obtiene productos por categoría
     */
    Page<Producto> findByCategoriaCategoriaIdAndPublicadoTrue(Integer categoriaId, Pageable pageable);

    /**
     * Busca productos por título (búsqueda parcial)
     */
    @Query("SELECT p FROM Producto p WHERE LOWER(p.titulo) LIKE LOWER(CONCAT('%', :query, '%')) AND p.publicado = true")
    Page<Producto> buscarPorTitulo(@Param("query") String query, Pageable pageable);

    /**
     * Obtiene productos destacados
     */
    List<Producto> findByDestacadoTrueAndPublicadoTrueOrderByCreadoEnDesc();

    /**
     * Obtiene productos nuevos
     */
    List<Producto> findByNuevoTrueAndPublicadoTrueOrderByCreadoEnDesc();

    /**
     * Obtiene productos en oferta
     */
    List<Producto> findByEnOfertaTrueAndPublicadoTrueOrderByCreadoEnDesc();

    /**
     * Obtiene productos por rango de precio
     */
    @Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :precioMin AND :precioMax AND p.publicado = true")
    Page<Producto> findByRangoPrecio(@Param("precioMin") BigDecimal precioMin, 
                                      @Param("precioMax") BigDecimal precioMax, 
                                      Pageable pageable);

    /**
     * Obtiene productos con stock bajo (cantidad menor o igual al límite)
     */
    @Query("SELECT p FROM Producto p WHERE p.cantidad <= :limite ORDER BY p.cantidad ASC")
    List<Producto> findProductosStockBajo(@Param("limite") Integer limite);

    /**
     * Cuenta productos por categoría
     */
    long countByCategoriaCategoriaIdAndPublicadoTrue(Integer categoriaId);

    /**
     * Verifica si existe un producto con el slug dado (excluyendo un ID específico)
     */
    boolean existsBySlugAndProductoIdNot(String slug, Integer productoId);
}

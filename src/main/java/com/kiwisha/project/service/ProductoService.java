package com.kiwisha.project.service;

import com.kiwisha.project.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interfaz de servicio para operaciones de negocio relacionadas con productos.
 */
public interface ProductoService {

    /**
     * Obtiene todos los productos con paginación.
     */
    Page<ProductoDTO> obtenerTodosLosProductos(Pageable pageable);

    /**
     * Obtiene solo productos publicados con paginación.
     */
    Page<ProductoDTO> obtenerProductosPublicados(Pageable pageable);

    /**
     * Busca un producto por ID.
     */
    ProductoDTO obtenerProductoPorId(Integer id);

    /**
     * Busca un producto por slug.
     */
    ProductoDTO obtenerProductoPorSlug(String slug);

    /**
     * Obtiene productos por categoría.
     */
    Page<ProductoDTO> obtenerProductosPorCategoria(Integer categoriaId, Pageable pageable);

    /**
     * Busca productos por título.
     */
    Page<ProductoDTO> buscarProductos(String query, Pageable pageable);

    /**
     * Obtiene productos por rango de precio.
     */
    Page<ProductoDTO> obtenerProductosPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);

    /**
     * Obtiene productos destacados.
     */
    List<ProductoDTO> obtenerProductosDestacados();

    /**
     * Obtiene productos nuevos.
     */
    List<ProductoDTO> obtenerProductosNuevos();

    /**
     * Obtiene productos en oferta.
     */
    List<ProductoDTO> obtenerProductosEnOferta();

    /**
     * Crea un nuevo producto.
     */
    ProductoDTO crearProducto(CrearProductoDTO crearProductoDTO);

    /**
     * Actualiza un producto existente.
     */
    ProductoDTO actualizarProducto(Integer id, ActualizarProductoDTO actualizarProductoDTO);

    /**
     * Elimina un producto (soft delete cambiando publicado a false).
     */
    void eliminarProducto(Integer id);

    /**
     * Actualiza el stock de un producto.
     */
    ProductoDTO actualizarStock(Integer id, Integer cantidad);

    /**
     * Verifica si hay stock suficiente.
     */
    boolean verificarStock(Integer productoId, Integer cantidad);

    /**
     * Obtiene productos con stock bajo.
     */
    List<ProductoDTO> obtenerProductosStockBajo(Integer limite);
}

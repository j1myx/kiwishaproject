package com.kiwisha.project.service;

import com.kiwisha.project.dto.CategoriaDTO;
import com.kiwisha.project.dto.CrearCategoriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio para la gestión de categorías de productos.
 */
public interface CategoriaService {

    /**
     * Obtiene todas las categorías con paginación.
     */
    Page<CategoriaDTO> obtenerTodasCategorias(Pageable pageable);

    /**
     * Obtiene todas las categorías como lista.
     */
    List<CategoriaDTO> obtenerTodasCategoriasLista();

    /**
     * Obtiene una categoría por su ID.
     */
    CategoriaDTO obtenerCategoriaPorId(Integer id);

    /**
     * Crea una nueva categoría.
     */
    CategoriaDTO crearCategoria(CrearCategoriaDTO crearCategoriaDTO);

    /**
     * Actualiza una categoría existente.
     */
    CategoriaDTO actualizarCategoria(Integer id, CrearCategoriaDTO actualizarCategoriaDTO);

    /**
     * Elimina una categoría.
     */
    void eliminarCategoria(Integer id);

    /**
     * Obtiene el número de productos en una categoría.
     */
    long contarProductosPorCategoria(Integer categoriaId);
}

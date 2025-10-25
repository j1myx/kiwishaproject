package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.CategoriaDTO;
import com.kiwisha.project.dto.CrearCategoriaDTO;
import com.kiwisha.project.exception.BusinessException;
import com.kiwisha.project.exception.ResourceNotFoundException;
import com.kiwisha.project.model.Categoria;
import com.kiwisha.project.repository.CategoriaRepository;
import com.kiwisha.project.repository.ProductoRepository;
import com.kiwisha.project.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de categorías.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaDTO> obtenerTodasCategorias(Pageable pageable) {
        log.debug("Obteniendo todas las categorías con paginación: {}", pageable);
        return categoriaRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> obtenerTodasCategoriasLista() {
        log.debug("Obteniendo todas las categorías como lista");
        return categoriaRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerCategoriaPorId(Integer id) {
        log.debug("Obteniendo categoría por ID: {}", id);
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));
        return convertirADTO(categoria);
    }

    @Override
    public CategoriaDTO crearCategoria(CrearCategoriaDTO crearCategoriaDTO) {
        log.info("Creando nueva categoría: {}", crearCategoriaDTO.getTitulo());

        Categoria categoria = new Categoria();
        categoria.setTitulo(crearCategoriaDTO.getTitulo());
        categoria.setResumen(crearCategoriaDTO.getResumen());

        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        log.info("Categoría creada exitosamente con ID: {}", categoriaGuardada.getCategoriaId());

        return convertirADTO(categoriaGuardada);
    }

    @Override
    public CategoriaDTO actualizarCategoria(Integer id, CrearCategoriaDTO actualizarCategoriaDTO) {
        log.info("Actualizando categoría ID: {}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        categoria.setTitulo(actualizarCategoriaDTO.getTitulo());
        categoria.setResumen(actualizarCategoriaDTO.getResumen());

        Categoria categoriaActualizada = categoriaRepository.save(categoria);
        log.info("Categoría actualizada exitosamente ID: {}", id);

        return convertirADTO(categoriaActualizada);
    }

    @Override
    public void eliminarCategoria(Integer id) {
        log.info("Eliminando categoría ID: {}", id);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", id));

        // Verificar que no tenga productos asociados
        long cantidadProductos = productoRepository.countByCategoriaCategoriIdAndPublicadoTrue(id);
        if (cantidadProductos > 0) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene " + 
                    cantidadProductos + " productos asociados");
        }

        categoriaRepository.delete(categoria);
        log.info("Categoría eliminada exitosamente ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarProductosPorCategoria(Integer categoriaId) {
        log.debug("Contando productos de la categoría ID: {}", categoriaId);
        
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoria", "id", categoriaId);
        }
        
        return productoRepository.countByCategoriaCategoriIdAndPublicadoTrue(categoriaId);
    }

    /**
     * Convierte una entidad Categoria a CategoriaDTO.
     */
    private CategoriaDTO convertirADTO(Categoria categoria) {
        CategoriaDTO dto = modelMapper.map(categoria, CategoriaDTO.class);
        
        // Contar productos en la categoría
        long cantidadProductos = productoRepository.countByCategoriaCategoriIdAndPublicadoTrue(
                categoria.getCategoriaId());
        dto.setCantidadProductos(cantidadProductos);
        
        return dto;
    }
}

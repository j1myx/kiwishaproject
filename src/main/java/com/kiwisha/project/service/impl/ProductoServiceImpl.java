package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.*;
import com.kiwisha.project.exception.BusinessException;
import com.kiwisha.project.exception.ResourceNotFoundException;
import com.kiwisha.project.model.Categoria;
import com.kiwisha.project.model.Producto;
import com.kiwisha.project.repository.CategoriaRepository;
import com.kiwisha.project.repository.ProductoRepository;
import com.kiwisha.project.service.ProductoService;
import com.kiwisha.project.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de productos.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> obtenerTodosLosProductos(Pageable pageable) {
        log.debug("Obteniendo todos los productos con paginación: {}", pageable);
        return productoRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> obtenerProductosPublicados(Pageable pageable) {
        log.debug("Obteniendo productos publicados con paginación: {}", pageable);
        return productoRepository.findByPublicadoTrue(pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoPorId(Integer id) {
        log.debug("Obteniendo producto por ID: {}", id);
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        return convertirADTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerProductoPorSlug(String slug) {
        log.debug("Obteniendo producto por slug: {}", slug);
        Producto producto = productoRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "slug", slug));
        return convertirADTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> obtenerProductosPorCategoria(Integer categoriaId, Pageable pageable) {
        log.debug("Obteniendo productos por categoría ID: {}", categoriaId);
        // Verificar que la categoría existe
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoria", "id", categoriaId);
        }
        return productoRepository.findByCategoriaCategoriIdAndPublicadoTrue(categoriaId, pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> buscarProductos(String query, Pageable pageable) {
        log.debug("Buscando productos con query: {}", query);
        return productoRepository.buscarPorTitulo(query, pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> obtenerProductosPorRangoPrecio(BigDecimal precioMin, BigDecimal precioMax, Pageable pageable) {
        log.debug("Obteniendo productos por rango de precio: {} - {}", precioMin, precioMax);
        return productoRepository.findByRangoPrecio(precioMin, precioMax, pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosDestacados() {
        log.debug("Obteniendo productos destacados");
        return productoRepository.findByDestacadoTrueAndPublicadoTrueOrderByCreadoEnDesc()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosNuevos() {
        log.debug("Obteniendo productos nuevos");
        return productoRepository.findByNuevoTrueAndPublicadoTrueOrderByCreadoEnDesc()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosEnOferta() {
        log.debug("Obteniendo productos en oferta");
        return productoRepository.findByEnOfertaTrueAndPublicadoTrueOrderByCreadoEnDesc()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoDTO crearProducto(CrearProductoDTO crearProductoDTO) {
        log.info("Creando nuevo producto: {}", crearProductoDTO.getTitulo());

        // Verificar que la categoría existe
        Categoria categoria = categoriaRepository.findById(crearProductoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", crearProductoDTO.getCategoriaId()));

        // Crear el producto
        Producto producto = Producto.builder()
                .titulo(crearProductoDTO.getTitulo())
                .resumen(crearProductoDTO.getResumen())
                .descripcion(crearProductoDTO.getDescripcion())
                .precio(crearProductoDTO.getPrecio())
                .precioAnterior(crearProductoDTO.getPrecioAnterior())
                .cantidad(crearProductoDTO.getCantidad())
                .categoria(categoria)
                .sku(crearProductoDTO.getSku())
                .peso(crearProductoDTO.getPeso())
                .unidadMedida(crearProductoDTO.getUnidadMedida())
                .imagen(crearProductoDTO.getImagen())
                .metaTitulo(crearProductoDTO.getMetaTitulo())
                .metaDescripcion(crearProductoDTO.getMetaDescripcion())
                .build();

        // Configurar valores booleanos con valores seguros
        producto.setPublicado(crearProductoDTO.getPublicado() != null ? crearProductoDTO.getPublicado() : false);
        producto.setDestacado(crearProductoDTO.getDestacado() != null ? crearProductoDTO.getDestacado() : false);
        producto.setNuevo(crearProductoDTO.getNuevo() != null ? crearProductoDTO.getNuevo() : false);
        producto.setEnOferta(crearProductoDTO.getEnOferta() != null ? crearProductoDTO.getEnOferta() : false);

        // Generar slug único
        String baseSlug = SlugGenerator.generateSlug(crearProductoDTO.getTitulo());
        String slug = generarSlugUnico(baseSlug, 0);
        producto.setSlug(slug);

        // Guardar
        Producto productoGuardado = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", productoGuardado.getProductoId());

        return convertirADTO(productoGuardado);
    }

    @Override
    public ProductoDTO actualizarProducto(Integer id, ActualizarProductoDTO actualizarProductoDTO) {
        log.info("Actualizando producto ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        // Actualizar campos si están presentes
        if (actualizarProductoDTO.getTitulo() != null) {
            producto.setTitulo(actualizarProductoDTO.getTitulo());
            // Regenerar slug si cambió el título
            String baseSlug = SlugGenerator.generateSlug(actualizarProductoDTO.getTitulo());
            if (!producto.getSlug().startsWith(baseSlug)) {
                String nuevoSlug = generarSlugUnico(baseSlug, 0, id);
                producto.setSlug(nuevoSlug);
            }
        }
        if (actualizarProductoDTO.getResumen() != null) {
            producto.setResumen(actualizarProductoDTO.getResumen());
        }
        if (actualizarProductoDTO.getDescripcion() != null) {
            producto.setDescripcion(actualizarProductoDTO.getDescripcion());
        }
        if (actualizarProductoDTO.getPrecio() != null) {
            producto.setPrecio(actualizarProductoDTO.getPrecio());
        }
        if (actualizarProductoDTO.getPrecioAnterior() != null) {
            producto.setPrecioAnterior(actualizarProductoDTO.getPrecioAnterior());
        }
        if (actualizarProductoDTO.getCantidad() != null) {
            producto.setCantidad(actualizarProductoDTO.getCantidad());
        }
        if (actualizarProductoDTO.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(actualizarProductoDTO.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", actualizarProductoDTO.getCategoriaId()));
            producto.setCategoria(categoria);
        }
        if (actualizarProductoDTO.getSku() != null) {
            producto.setSku(actualizarProductoDTO.getSku());
        }
        if (actualizarProductoDTO.getPeso() != null) {
            producto.setPeso(actualizarProductoDTO.getPeso());
        }
        if (actualizarProductoDTO.getUnidadMedida() != null) {
            producto.setUnidadMedida(actualizarProductoDTO.getUnidadMedida());
        }
        if (actualizarProductoDTO.getImagen() != null) {
            producto.setImagen(actualizarProductoDTO.getImagen());
        }
        if (actualizarProductoDTO.getPublicado() != null) {
            producto.setPublicado(actualizarProductoDTO.getPublicado());
        }
        if (actualizarProductoDTO.getDestacado() != null) {
            producto.setDestacado(actualizarProductoDTO.getDestacado());
        }
        if (actualizarProductoDTO.getNuevo() != null) {
            producto.setNuevo(actualizarProductoDTO.getNuevo());
        }
        if (actualizarProductoDTO.getEnOferta() != null) {
            producto.setEnOferta(actualizarProductoDTO.getEnOferta());
        }
        if (actualizarProductoDTO.getMetaTitulo() != null) {
            producto.setMetaTitulo(actualizarProductoDTO.getMetaTitulo());
        }
        if (actualizarProductoDTO.getMetaDescripcion() != null) {
            producto.setMetaDescripcion(actualizarProductoDTO.getMetaDescripcion());
        }

        Producto productoActualizado = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente ID: {}", id);

        return convertirADTO(productoActualizado);
    }

    @Override
    public void eliminarProducto(Integer id) {
        log.info("Eliminando producto ID: {}", id);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        // Soft delete: cambiar publicado a false
        producto.setPublicado(false);
        productoRepository.save(producto);

        log.info("Producto eliminado (despublicado) exitosamente ID: {}", id);
    }

    @Override
    public ProductoDTO actualizarStock(Integer id, Integer cantidad) {
        log.info("Actualizando stock del producto ID: {} a cantidad: {}", id, cantidad);

        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        if (cantidad < 0) {
            throw new BusinessException("La cantidad no puede ser negativa");
        }

        producto.setCantidad(cantidad);
        Producto productoActualizado = productoRepository.save(producto);

        log.info("Stock actualizado exitosamente para producto ID: {}", id);
        return convertirADTO(productoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verificarStock(Integer productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));
        return producto.tieneSuficienteStock(cantidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> obtenerProductosStockBajo(Integer limite) {
        log.debug("Obteniendo productos con stock bajo (límite: {})", limite);
        return productoRepository.findProductosStockBajo(limite)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Genera un slug único verificando que no exista en la base de datos.
     */
    private String generarSlugUnico(String baseSlug, int counter) {
        String slug = SlugGenerator.generateUniqueSlug(baseSlug, counter);
        if (productoRepository.findBySlug(slug).isPresent()) {
            return generarSlugUnico(baseSlug, counter + 1);
        }
        return slug;
    }

    /**
     * Genera un slug único excluyendo un producto específico (para actualizaciones).
     */
    private String generarSlugUnico(String baseSlug, int counter, Integer productoId) {
        String slug = SlugGenerator.generateUniqueSlug(baseSlug, counter);
        if (productoRepository.existsBySlugAndProductoIdNot(slug, productoId)) {
            return generarSlugUnico(baseSlug, counter + 1, productoId);
        }
        return slug;
    }

    /**
     * Convierte una entidad Producto a ProductoDTO.
     */
    private ProductoDTO convertirADTO(Producto producto) {
        ProductoDTO dto = modelMapper.map(producto, ProductoDTO.class);
        
        // Mapear campos adicionales
        dto.setCategoriaId(producto.getCategoria().getCategoriaId());
        dto.setCategoriaNombre(producto.getCategoria().getTitulo());
        dto.setPromedioCalificacion(producto.getPromedioCalificacion());
        dto.setCantidadReviews(producto.getCantidadReviews());
        dto.setPorcentajeDescuento(producto.getPorcentajeDescuento());
        dto.setDisponible(producto.estaDisponible());
        
        // Mapear imágenes adicionales si existen
        if (producto.getProductoImagenes() != null && !producto.getProductoImagenes().isEmpty()) {
            List<String> imagenes = producto.getProductoImagenes().stream()
                    .map(img -> img.getRuta())
                    .collect(Collectors.toList());
            dto.setImagenesAdicionales(imagenes);
        }
        
        return dto;
    }
}

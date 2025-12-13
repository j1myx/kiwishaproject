package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.*;
import com.kiwisha.project.exception.BusinessException;
import com.kiwisha.project.exception.ResourceNotFoundException;
import com.kiwisha.project.model.Categoria;
import com.kiwisha.project.model.EstadoProducto;
import com.kiwisha.project.model.Producto;
import com.kiwisha.project.repository.CategoriaRepository;
import com.kiwisha.project.repository.ProductoRepository;
import com.kiwisha.project.service.ProductoService;
import com.kiwisha.project.util.SlugGenerator;
import jakarta.persistence.EntityManager;
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

    private static final String DEFAULT_PRODUCT_IMAGE_PATH = "defaults/producto-default.svg";

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;

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
        return productoRepository.findByCategoriaCategoriaIdAndPublicadoTrue(categoriaId, pageable)
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

        // Verificar que la categoría existe (si se proporciona)
        Categoria categoria = null;
        if (crearProductoDTO.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(crearProductoDTO.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", "id", crearProductoDTO.getCategoriaId()));
        }

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
                .imagen((crearProductoDTO.getImagen() == null || crearProductoDTO.getImagen().isBlank())
                    ? DEFAULT_PRODUCT_IMAGE_PATH
                    : crearProductoDTO.getImagen())
                .estado(crearProductoDTO.getEstado() != null ? crearProductoDTO.getEstado() : EstadoProducto.BORRADOR)
                .metaTitulo(crearProductoDTO.getMetaTitulo())
                .metaDescripcion(crearProductoDTO.getMetaDescripcion())
                .build();

            // Mantener consistencia: `publicado` debe reflejar el `estado`
            producto.setPublicado(producto.getEstado() == EstadoProducto.PUBLICADO);
        producto.setDestacado(Boolean.TRUE.equals(crearProductoDTO.getDestacado()));
        producto.setNuevo(Boolean.TRUE.equals(crearProductoDTO.getNuevo()));
        producto.setEnOferta(Boolean.TRUE.equals(crearProductoDTO.getEnOferta()));

        // Generar slug único
        String baseSlug = SlugGenerator.generateSlug(crearProductoDTO.getTitulo());
        String slug = generarSlugUnico(baseSlug, 0);
        producto.setSlug(slug);

        // Generar SKU automáticamente si no se proporciona
        if (crearProductoDTO.getSku() == null || crearProductoDTO.getSku().trim().isEmpty()) {
            String skuGenerado = generarSkuUnico(crearProductoDTO.getTitulo());
            producto.setSku(skuGenerado);
        }

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
        if (actualizarProductoDTO.getEstado() != null) {
            log.debug("Aplicando estado: {} al producto ID: {}", actualizarProductoDTO.getEstado(), id);
            producto.setEstado(actualizarProductoDTO.getEstado());
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

        // Mantener consistencia: `publicado` debe reflejar el `estado`
        producto.setPublicado(producto.getEstado() == EstadoProducto.PUBLICADO);

        Producto productoActualizado = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente ID: {}", id);

        return convertirADTO(productoActualizado);
    }

    @Override
    @Transactional
    public void eliminarProducto(Integer id) {
        log.info("Eliminando producto ID: {}", id);

        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto", "id", id);
        }

        // Eliminación directa usando deleteById
        productoRepository.deleteById(id);

        log.info("Producto eliminado exitosamente ID: {}", id);
    }

    @Override
    @Transactional
    public ProductoDTO duplicarProducto(Integer id) {
        log.info("Duplicando producto ID: {}", id);

        Producto productoOriginal = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        // Crear nuevo producto con datos del original
        Producto productoDuplicado = new Producto();
        productoDuplicado.setTitulo(productoOriginal.getTitulo() + " (Copia)");
        productoDuplicado.setResumen(productoOriginal.getResumen());
        productoDuplicado.setDescripcion(productoOriginal.getDescripcion());
        productoDuplicado.setPrecio(productoOriginal.getPrecio());
        productoDuplicado.setPrecioAnterior(productoOriginal.getPrecioAnterior());
        productoDuplicado.setCantidad(0); // Stock en 0 para duplicados
        productoDuplicado.setCategoria(productoOriginal.getCategoria());
        productoDuplicado.setPeso(productoOriginal.getPeso());
        productoDuplicado.setUnidadMedida(productoOriginal.getUnidadMedida());
        
        // Estado siempre en BORRADOR
        productoDuplicado.setEstado(EstadoProducto.BORRADOR);
        productoDuplicado.setPublicado(false);
        productoDuplicado.setDestacado(false);
        productoDuplicado.setNuevo(false);
        productoDuplicado.setEnOferta(false);
        
        // Generar SKU único
        String baseSku = productoOriginal.getSku() != null ? 
            productoOriginal.getSku() + "-COPIA" : 
            "PROD-COPIA-" + System.currentTimeMillis();
        productoDuplicado.setSku(baseSku);
        
        // Generar slug único
        String baseSlug = productoOriginal.getSlug() != null ? 
            productoOriginal.getSlug() + "-copia" : 
            SlugGenerator.generateSlug(productoDuplicado.getTitulo());
        productoDuplicado.setSlug(generarSlugUnico(baseSlug, 0));
        
        // SEO
        productoDuplicado.setMetaTitulo(productoOriginal.getMetaTitulo());
        productoDuplicado.setMetaDescripcion(productoOriginal.getMetaDescripcion());
        
        Producto guardado = productoRepository.save(productoDuplicado);
        
        log.info("Producto duplicado exitosamente. Original ID: {}, Nuevo ID: {}", id, guardado.getProductoId());
        return convertirADTO(guardado);
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

    @Override
    @Transactional(readOnly = true)
    public long contarProductos() {
        log.debug("Contando total de productos");
        return productoRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarProductosPublicados() {
        log.debug("Contando productos publicados");
        return productoRepository.countByEstado(EstadoProducto.PUBLICADO);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarProductosStockBajo(Integer limite) {
        log.debug("Contando productos con stock bajo (límite: {})", limite);
        return productoRepository.countProductosStockBajo(limite);
    }

    @Override
    public long eliminarTodosLosProductos() {
        log.warn("Eliminando TODOS los productos y relaciones directas (operación administrativa)");

        // Importante: borrar primero tablas que referencian a productos para evitar violaciones de FK.
        // Tablas detectadas en el modelo:
        // - carrito_items (CarritoItem -> Producto)
        // - pedido_elementos (PedidoElemento -> Producto)
        // - reviews (Review -> Producto)
        // - producto_imagenes (ProductoImagen -> Producto)
        // - productos_paginas (ProductoPagina -> Producto)

        entityManager.createNativeQuery("DELETE FROM carrito_items").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM pedido_elementos").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM reviews").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM producto_imagenes").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM productos_paginas").executeUpdate();

        int productosEliminados = entityManager.createNativeQuery("DELETE FROM productos").executeUpdate();
        return productosEliminados;
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
     * Genera un SKU único basado en el título del producto.
     * Formato: XXX-NNNN (ej: KIW-0001, QUI-0002)
     */
    private String generarSkuUnico(String titulo) {
        // Extraer las primeras 3 letras del título (mayúsculas, sin espacios)
        String prefijo = titulo.toUpperCase()
                .replaceAll("[^A-Z]", "")
                .substring(0, Math.min(3, titulo.replaceAll("[^A-Za-z]", "").length()));
        
        // Si el prefijo es muy corto, rellenarlo con "X"
        while (prefijo.length() < 3) {
            prefijo += "X";
        }
        
        // Buscar el último número usado con este prefijo
        Long ultimoNumero = productoRepository.findMaxSkuNumber(prefijo);
        
        // Generar el siguiente número
        long siguienteNumero = (ultimoNumero != null ? ultimoNumero : 0) + 1;
        
        // Formatear el SKU: XXX-NNNN
        return String.format("%s-%04d", prefijo, siguienteNumero);
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

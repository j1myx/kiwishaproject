package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.*;
import com.kiwisha.project.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para la gestión de productos
 * Endpoints públicos y administrativos
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "API para gestión de productos")
public class ProductoApiController {

    private final ProductoService productoService;

    // ==================== ENDPOINTS PÚBLICOS ====================

    @GetMapping("/public/productos")
    @Operation(summary = "Listar todos los productos", description = "Obtiene todos los productos publicados con paginación")
    public ResponseEntity<ApiResponseDTO<Page<ProductoDTO>>> listarProductos(
            @Parameter(description = "Página a obtener (0-indexed)") Pageable pageable) {
        
        Page<ProductoDTO> productos = productoService.obtenerProductosPublicados(pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<ProductoDTO>>builder()
                .success(true)
                .message("Productos obtenidos exitosamente")
                .data(productos)
                .build()
        );
    }

    @GetMapping("/public/productos/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico por su ID")
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> obtenerProductoPorId(
            @Parameter(description = "ID del producto") @PathVariable Integer id) {
        
        ProductoDTO producto = productoService.obtenerProductoPorId(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ProductoDTO>builder()
                .success(true)
                .message("Producto encontrado")
                .data(producto)
                .build()
        );
    }

    @GetMapping("/public/productos/slug/{slug}")
    @Operation(summary = "Obtener producto por slug", description = "Obtiene un producto específico por su slug (URL-friendly)")
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> obtenerProductoPorSlug(
            @Parameter(description = "Slug del producto") @PathVariable String slug) {
        
        ProductoDTO producto = productoService.obtenerProductoPorSlug(slug);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ProductoDTO>builder()
                .success(true)
                .message("Producto encontrado")
                .data(producto)
                .build()
        );
    }

    @GetMapping("/public/productos/categoria/{categoriaId}")
    @Operation(summary = "Buscar productos por categoría", description = "Obtiene productos de una categoría específica")
    public ResponseEntity<ApiResponseDTO<Page<ProductoDTO>>> buscarPorCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Integer categoriaId,
            Pageable pageable) {
        
        Page<ProductoDTO> productos = productoService.obtenerProductosPorCategoria(categoriaId, pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<ProductoDTO>>builder()
                .success(true)
                .message("Productos de categoría obtenidos exitosamente")
                .data(productos)
                .build()
        );
    }

    @GetMapping("/public/productos/buscar")
    @Operation(summary = "Buscar productos por título", description = "Busca productos que contengan el texto en el título")
    public ResponseEntity<ApiResponseDTO<Page<ProductoDTO>>> buscarPorTitulo(
            @Parameter(description = "Texto a buscar en el título") @RequestParam String titulo,
            Pageable pageable) {
        
        Page<ProductoDTO> productos = productoService.buscarProductos(titulo, pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<ProductoDTO>>builder()
                .success(true)
                .message("Búsqueda completada exitosamente")
                .data(productos)
                .build()
        );
    }

    @GetMapping("/public/productos/filtrar")
    @Operation(summary = "Filtrar productos por rango de precio", description = "Obtiene productos dentro de un rango de precio")
    public ResponseEntity<ApiResponseDTO<Page<ProductoDTO>>> filtrarPorPrecio(
            @Parameter(description = "Precio mínimo") @RequestParam BigDecimal precioMin,
            @Parameter(description = "Precio máximo") @RequestParam BigDecimal precioMax,
            Pageable pageable) {
        
        Page<ProductoDTO> productos = productoService.obtenerProductosPorRangoPrecio(precioMin, precioMax, pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<ProductoDTO>>builder()
                .success(true)
                .message("Productos filtrados exitosamente")
                .data(productos)
                .build()
        );
    }

    @GetMapping("/public/productos/destacados")
    @Operation(summary = "Obtener productos destacados", description = "Obtiene productos marcados como destacados")
    public ResponseEntity<ApiResponseDTO<List<ProductoDTO>>> obtenerProductosDestacados() {
        
        List<ProductoDTO> productos = productoService.obtenerProductosDestacados();
        
        return ResponseEntity.ok(
            ApiResponseDTO.<List<ProductoDTO>>builder()
                .success(true)
                .message("Productos destacados obtenidos exitosamente")
                .data(productos)
                .build()
        );
    }

    @GetMapping("/public/productos/nuevos")
    @Operation(summary = "Obtener productos nuevos", description = "Obtiene los productos más recientes")
    public ResponseEntity<ApiResponseDTO<List<ProductoDTO>>> obtenerProductosNuevos() {
        
        List<ProductoDTO> productos = productoService.obtenerProductosNuevos();
        
        return ResponseEntity.ok(
            ApiResponseDTO.<List<ProductoDTO>>builder()
                .success(true)
                .message("Productos nuevos obtenidos exitosamente")
                .data(productos)
                .build()
        );
    }

    @GetMapping("/public/productos/ofertas")
    @Operation(summary = "Obtener productos en oferta", description = "Obtiene productos con descuento activo")
    public ResponseEntity<ApiResponseDTO<List<ProductoDTO>>> obtenerProductosEnOferta() {
        
        List<ProductoDTO> productos = productoService.obtenerProductosEnOferta();
        
        return ResponseEntity.ok(
            ApiResponseDTO.<List<ProductoDTO>>builder()
                .success(true)
                .message("Productos en oferta obtenidos exitosamente")
                .data(productos)
                .build()
        );
    }

    // ==================== ENDPOINTS ADMINISTRATIVOS ====================

    @PostMapping("/admin/productos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Crear producto", description = "Crea un nuevo producto (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> crearProducto(
            @Valid @RequestBody CrearProductoDTO crearProductoDTO) {
        
        ProductoDTO producto = productoService.crearProducto(crearProductoDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<ProductoDTO>builder()
                .success(true)
                .message("Producto creado exitosamente")
                .data(producto)
                .build()
        );
    }

    @PutMapping("/admin/productos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Actualizar producto", description = "Actualiza un producto existente (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> actualizarProducto(
            @Parameter(description = "ID del producto") @PathVariable Integer id,
            @Valid @RequestBody ActualizarProductoDTO actualizarProductoDTO) {
        
        ProductoDTO producto = productoService.actualizarProducto(id, actualizarProductoDTO);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ProductoDTO>builder()
                .success(true)
                .message("Producto actualizado exitosamente")
                .data(producto)
                .build()
        );
    }

    @DeleteMapping("/admin/productos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Eliminar producto", description = "Elimina un producto (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarProducto(
            @Parameter(description = "ID del producto") @PathVariable Integer id) {
        
        productoService.eliminarProducto(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Producto eliminado exitosamente")
                .build()
        );
    }

    @PostMapping("/admin/productos/{id}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Eliminar producto (POST)", description = "Elimina un producto usando POST (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarProductoPost(
            @Parameter(description = "ID del producto") @PathVariable Integer id) {
        
        productoService.eliminarProducto(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Producto eliminado exitosamente")
                .build()
        );
    }

    @PatchMapping("/admin/productos/{id}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Actualizar stock", description = "Actualiza el stock de un producto (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<ProductoDTO>> actualizarStock(
            @Parameter(description = "ID del producto") @PathVariable Integer id,
            @Parameter(description = "Cantidad a sumar/restar") @RequestParam Integer cantidad) {
        
        ProductoDTO producto = productoService.actualizarStock(id, cantidad);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ProductoDTO>builder()
                .success(true)
                .message("Stock actualizado exitosamente")
                .data(producto)
                .build()
        );
    }

    @GetMapping("/admin/productos/stock-bajo")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Productos con stock bajo", description = "Obtiene productos con stock menor al umbral (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<List<ProductoDTO>>> obtenerProductosStockBajo(
            @Parameter(description = "Umbral de stock") @RequestParam(defaultValue = "10") Integer umbral) {
        
        List<ProductoDTO> productos = productoService.obtenerProductosStockBajo(umbral);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<List<ProductoDTO>>builder()
                .success(true)
                .message("Productos con stock bajo obtenidos exitosamente")
                .data(productos)
                .build()
        );
    }
}

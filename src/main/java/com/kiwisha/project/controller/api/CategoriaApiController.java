package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.ApiResponseDTO;
import com.kiwisha.project.dto.CategoriaDTO;
import com.kiwisha.project.dto.CrearCategoriaDTO;
import com.kiwisha.project.service.CategoriaService;
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

import java.util.List;

/**
 * Controlador REST para la gestión de categorías
 * Endpoints públicos y administrativos
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "API para gestión de categorías de productos")
public class CategoriaApiController {

    private final CategoriaService categoriaService;

    // ==================== ENDPOINTS PÚBLICOS ====================

    @GetMapping("/public/categorias")
    @Operation(summary = "Listar todas las categorías", description = "Obtiene todas las categorías activas")
    public ResponseEntity<ApiResponseDTO<List<CategoriaDTO>>> listarCategorias() {
        List<CategoriaDTO> categorias = categoriaService.obtenerTodasCategoriasLista();
        
        return ResponseEntity.ok(
            ApiResponseDTO.<List<CategoriaDTO>>builder()
                .success(true)
                .message("Categorías obtenidas exitosamente")
                .data(categorias)
                .build()
        );
    }

    @GetMapping("/public/categorias/paginadas")
    @Operation(summary = "Listar categorías con paginación", description = "Obtiene categorías con paginación")
    public ResponseEntity<ApiResponseDTO<Page<CategoriaDTO>>> listarCategoriasPaginadas(Pageable pageable) {
        Page<CategoriaDTO> categorias = categoriaService.obtenerTodasCategorias(pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<CategoriaDTO>>builder()
                .success(true)
                .message("Categorías obtenidas exitosamente")
                .data(categorias)
                .build()
        );
    }

    @GetMapping("/public/categorias/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Obtiene una categoría específica por su ID")
    public ResponseEntity<ApiResponseDTO<CategoriaDTO>> obtenerCategoriaPorId(
            @Parameter(description = "ID de la categoría") @PathVariable Integer id) {
        
        CategoriaDTO categoria = categoriaService.obtenerCategoriaPorId(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<CategoriaDTO>builder()
                .success(true)
                .message("Categoría encontrada")
                .data(categoria)
                .build()
        );
    }

    @GetMapping("/public/categorias/{id}/productos/count")
    @Operation(summary = "Contar productos por categoría", description = "Obtiene el número de productos en una categoría")
    public ResponseEntity<ApiResponseDTO<Long>> contarProductosPorCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Integer id) {
        
        long count = categoriaService.contarProductosPorCategoria(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Long>builder()
                .success(true)
                .message("Conteo de productos obtenido exitosamente")
                .data(count)
                .build()
        );
    }

    // ==================== ENDPOINTS ADMINISTRATIVOS ====================

    @PostMapping("/admin/categorias")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Crear categoría", description = "Crea una nueva categoría (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<CategoriaDTO>> crearCategoria(
            @Valid @RequestBody CrearCategoriaDTO crearCategoriaDTO) {
        
        CategoriaDTO categoria = categoriaService.crearCategoria(crearCategoriaDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<CategoriaDTO>builder()
                .success(true)
                .message("Categoría creada exitosamente")
                .data(categoria)
                .build()
        );
    }

    @PutMapping("/admin/categorias/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Actualizar categoría", description = "Actualiza una categoría existente (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<CategoriaDTO>> actualizarCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Integer id,
            @Valid @RequestBody CrearCategoriaDTO crearCategoriaDTO) {
        
        CategoriaDTO categoria = categoriaService.actualizarCategoria(id, crearCategoriaDTO);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<CategoriaDTO>builder()
                .success(true)
                .message("Categoría actualizada exitosamente")
                .data(categoria)
                .build()
        );
    }

    @DeleteMapping("/admin/categorias/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Eliminar categoría", description = "Elimina una categoría si no tiene productos asociados (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarCategoria(
            @Parameter(description = "ID de la categoría") @PathVariable Integer id) {
        
        categoriaService.eliminarCategoria(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Categoría eliminada exitosamente")
                .build()
        );
    }
}

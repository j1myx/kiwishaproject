package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.ApiResponseDTO;
import com.kiwisha.project.dto.CrearReviewDTO;
import com.kiwisha.project.dto.ReviewDTO;
import com.kiwisha.project.service.ReviewService;
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

/**
 * Controlador REST para la gestión de reviews (valoraciones de productos)
 * Endpoints públicos y administrativos
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "API para gestión de valoraciones de productos")
public class ReviewApiController {

    private final ReviewService reviewService;

    // ==================== ENDPOINTS PÚBLICOS ====================

    @PostMapping("/public/reviews")
    @Operation(summary = "Crear review", description = "Crea una nueva valoración de producto (pendiente de aprobación)")
    public ResponseEntity<ApiResponseDTO<ReviewDTO>> crearReview(
            @Valid @RequestBody CrearReviewDTO crearReviewDTO) {
        
        ReviewDTO review = reviewService.crearReview(crearReviewDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<ReviewDTO>builder()
                .success(true)
                .message("Review creada exitosamente. Pendiente de aprobación.")
                .data(review)
                .build()
        );
    }

    @GetMapping("/public/productos/{productoId}/reviews")
    @Operation(summary = "Obtener reviews de producto", description = "Obtiene las reviews aprobadas de un producto")
    public ResponseEntity<ApiResponseDTO<Page<ReviewDTO>>> obtenerReviewsPorProducto(
            @Parameter(description = "ID del producto") @PathVariable Integer productoId,
            Pageable pageable) {
        
        Page<ReviewDTO> reviews = reviewService.obtenerReviewsPorProducto(productoId, pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<ReviewDTO>>builder()
                .success(true)
                .message("Reviews obtenidas exitosamente")
                .data(reviews)
                .build()
        );
    }

    @GetMapping("/public/productos/{productoId}/reviews/promedio")
    @Operation(summary = "Obtener promedio de calificación", description = "Obtiene el promedio de calificación de un producto")
    public ResponseEntity<ApiResponseDTO<Double>> obtenerPromedioCalificacion(
            @Parameter(description = "ID del producto") @PathVariable Integer productoId) {
        
        Double promedio = reviewService.calcularPromedioCalificacion(productoId);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Double>builder()
                .success(true)
                .message("Promedio de calificación obtenido exitosamente")
                .data(promedio)
                .build()
        );
    }

    @GetMapping("/public/productos/{productoId}/reviews/count")
    @Operation(summary = "Contar reviews de producto", description = "Obtiene el número de reviews aprobadas de un producto")
    public ResponseEntity<ApiResponseDTO<Long>> contarReviewsPorProducto(
            @Parameter(description = "ID del producto") @PathVariable Integer productoId) {
        
        long count = reviewService.contarReviewsPorProducto(productoId);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Long>builder()
                .success(true)
                .message("Conteo de reviews obtenido exitosamente")
                .data(count)
                .build()
        );
    }

    // ==================== ENDPOINTS ADMINISTRATIVOS ====================

    @GetMapping("/admin/reviews")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Listar todas las reviews", description = "Obtiene todas las reviews con paginación (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<Page<ReviewDTO>>> listarReviews(Pageable pageable) {
        Page<ReviewDTO> reviews = reviewService.obtenerTodasReviews(pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<ReviewDTO>>builder()
                .success(true)
                .message("Reviews obtenidas exitosamente")
                .data(reviews)
                .build()
        );
    }

    @GetMapping("/admin/reviews/pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Obtener reviews pendientes", description = "Obtiene reviews pendientes de aprobación (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<Page<ReviewDTO>>> obtenerReviewsPendientes(Pageable pageable) {
        Page<ReviewDTO> reviews = reviewService.obtenerReviewsPendientes(pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<ReviewDTO>>builder()
                .success(true)
                .message("Reviews pendientes obtenidas exitosamente")
                .data(reviews)
                .build()
        );
    }

    @GetMapping("/admin/reviews/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Obtener review por ID", description = "Obtiene una review específica (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<ReviewDTO>> obtenerReviewPorId(
            @Parameter(description = "ID de la review") @PathVariable Integer id) {
        
        ReviewDTO review = reviewService.obtenerReviewPorId(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ReviewDTO>builder()
                .success(true)
                .message("Review encontrada")
                .data(review)
                .build()
        );
    }

    @PatchMapping("/admin/reviews/{id}/aprobar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Aprobar review", description = "Aprueba una review para que sea visible públicamente (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<ReviewDTO>> aprobarReview(
            @Parameter(description = "ID de la review") @PathVariable Integer id) {
        
        ReviewDTO review = reviewService.aprobarReview(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ReviewDTO>builder()
                .success(true)
                .message("Review aprobada exitosamente")
                .data(review)
                .build()
        );
    }

    @PatchMapping("/admin/reviews/{id}/rechazar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Rechazar review", description = "Rechaza una review y la marca como inactiva (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<ReviewDTO>> rechazarReview(
            @Parameter(description = "ID de la review") @PathVariable Integer id) {
        
        ReviewDTO review = reviewService.rechazarReview(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ReviewDTO>builder()
                .success(true)
                .message("Review rechazada exitosamente")
                .data(review)
                .build()
        );
    }

    @DeleteMapping("/admin/reviews/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Eliminar review", description = "Elimina permanentemente una review (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarReview(
            @Parameter(description = "ID de la review") @PathVariable Integer id) {
        
        reviewService.eliminarReview(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Review eliminada exitosamente")
                .build()
        );
    }
}

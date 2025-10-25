package com.kiwisha.project.service;

import com.kiwisha.project.dto.CrearReviewDTO;
import com.kiwisha.project.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Servicio para la gestión de reviews de productos.
 */
public interface ReviewService {

    /**
     * Obtiene todas las reviews con paginación.
     */
    Page<ReviewDTO> obtenerTodasReviews(Pageable pageable);

    /**
     * Obtiene una review por su ID.
     */
    ReviewDTO obtenerReviewPorId(Integer id);

    /**
     * Obtiene reviews de un producto específico (solo aprobadas y visibles).
     */
    Page<ReviewDTO> obtenerReviewsPorProducto(Integer productoId, Pageable pageable);

    /**
     * Obtiene reviews pendientes de aprobación.
     */
    Page<ReviewDTO> obtenerReviewsPendientes(Pageable pageable);

    /**
     * Crea una nueva review.
     */
    ReviewDTO crearReview(CrearReviewDTO crearReviewDTO);

    /**
     * Aprueba una review.
     */
    ReviewDTO aprobarReview(Integer id);

    /**
     * Rechaza una review.
     */
    ReviewDTO rechazarReview(Integer id);

    /**
     * Elimina una review.
     */
    void eliminarReview(Integer id);

    /**
     * Calcula el promedio de calificación de un producto.
     */
    Double calcularPromedioCalificacion(Integer productoId);

    /**
     * Cuenta el número de reviews de un producto.
     */
    long contarReviewsPorProducto(Integer productoId);
}

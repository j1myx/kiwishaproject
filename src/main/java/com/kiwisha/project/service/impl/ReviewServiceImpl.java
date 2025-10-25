package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.CrearReviewDTO;
import com.kiwisha.project.dto.ReviewDTO;
import com.kiwisha.project.exception.BusinessException;
import com.kiwisha.project.exception.ResourceNotFoundException;
import com.kiwisha.project.model.Cliente;
import com.kiwisha.project.model.Producto;
import com.kiwisha.project.model.Review;
import com.kiwisha.project.repository.ClienteRepository;
import com.kiwisha.project.repository.ProductoRepository;
import com.kiwisha.project.repository.ReviewRepository;
import com.kiwisha.project.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de reviews de productos.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> obtenerTodasReviews(Pageable pageable) {
        log.debug("Obteniendo todas las reviews con paginación: {}", pageable);
        return reviewRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO obtenerReviewPorId(Integer id) {
        log.debug("Obteniendo review por ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));
        return convertirADTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> obtenerReviewsPorProducto(Integer productoId, Pageable pageable) {
        log.debug("Obteniendo reviews del producto ID: {}", productoId);
        
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto", "id", productoId);
        }
        
        return reviewRepository.findByProductoProductoIdAndAprobadoTrueAndActivoTrueOrderByCreadoEnDesc(
                productoId, pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> obtenerReviewsPendientes(Pageable pageable) {
        log.debug("Obteniendo reviews pendientes de aprobación");
        return reviewRepository.findByAprobadoFalseAndActivoTrueOrderByCreadoEnDesc(pageable)
                .map(this::convertirADTO);
    }

    @Override
    public ReviewDTO crearReview(CrearReviewDTO crearReviewDTO) {
        log.info("Creando nueva review para producto ID: {}", crearReviewDTO.getProductoId());

        // Validar producto
        Producto producto = productoRepository.findById(crearReviewDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", 
                        crearReviewDTO.getProductoId()));

        // Validar cliente
        Cliente cliente = clienteRepository.findById(crearReviewDTO.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", 
                        crearReviewDTO.getClienteId()));

        // Validar calificación
        if (crearReviewDTO.getCalificacion() < 1 || crearReviewDTO.getCalificacion() > 5) {
            throw new BusinessException("La calificación debe estar entre 1 y 5");
        }

        // Crear review
        Review review = Review.builder()
                .producto(producto)
                .cliente(cliente)
                .calificacion(crearReviewDTO.getCalificacion())
                .titulo(crearReviewDTO.getTitulo())
                .comentario(crearReviewDTO.getComentario())
                .aprobado(false) // Por defecto no aprobado
                .activo(true)
                .compraVerificada(false)
                .utilCount(0)
                .build();

        Review reviewGuardada = reviewRepository.save(review);
        log.info("Review creada exitosamente con ID: {}", reviewGuardada.getReviewId());

        return convertirADTO(reviewGuardada);
    }

    @Override
    public ReviewDTO aprobarReview(Integer id) {
        log.info("Aprobando review ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        review.setAprobado(true);
        Review reviewActualizada = reviewRepository.save(review);

        log.info("Review aprobada exitosamente ID: {}", id);
        return convertirADTO(reviewActualizada);
    }

    @Override
    public ReviewDTO rechazarReview(Integer id) {
        log.info("Rechazando review ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        review.setAprobado(false);
        review.setActivo(false);
        Review reviewActualizada = reviewRepository.save(review);

        log.info("Review rechazada exitosamente ID: {}", id);
        return convertirADTO(reviewActualizada);
    }

    @Override
    public void eliminarReview(Integer id) {
        log.info("Eliminando review ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

        reviewRepository.delete(review);
        log.info("Review eliminada exitosamente ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularPromedioCalificacion(Integer productoId) {
        log.debug("Calculando promedio de calificación para producto ID: {}", productoId);
        
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto", "id", productoId);
        }
        
        return reviewRepository.calcularPromedioCalificacion(productoId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarReviewsPorProducto(Integer productoId) {
        log.debug("Contando reviews del producto ID: {}", productoId);
        
        if (!productoRepository.existsById(productoId)) {
            throw new ResourceNotFoundException("Producto", "id", productoId);
        }
        
        return reviewRepository.countByProductoProductoIdAndAprobadoTrueAndActivoTrue(productoId);
    }

    /**
     * Convierte una entidad Review a ReviewDTO.
     */
    private ReviewDTO convertirADTO(Review review) {
        ReviewDTO dto = modelMapper.map(review, ReviewDTO.class);
        
        // Mapear producto
        dto.setProductoId(review.getProducto().getProductoId());
        dto.setProductoTitulo(review.getProducto().getTitulo());
        
        // Mapear cliente
        dto.setClienteId(review.getCliente().getClienteId());
        dto.setClienteNombre(review.getCliente().getNombre());
        
        return dto;
    }
}

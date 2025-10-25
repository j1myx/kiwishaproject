package com.kiwisha.project.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO para visualizar reviews/reseñas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    
    private Integer reviewId;
    private Integer productoId;
    private String productoTitulo;
    private Integer clienteId;
    private String clienteNombre;
    private Integer calificacion;
    private String titulo;
    private String comentario;
    private Boolean aprobado;
    private Boolean compraVerificada;
    private Integer utilCount;
    private LocalDateTime creadoEn;
}

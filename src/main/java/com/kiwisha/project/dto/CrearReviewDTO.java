package com.kiwisha.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para crear una nueva review/reseña de producto.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearReviewDTO {
    
    @NotNull(message = "El producto es obligatorio")
    private Integer productoId;
    
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;
    
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;
    
    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 10, max = 1000, message = "El comentario debe tener entre 10 y 1000 caracteres")
    private String comentario;
}

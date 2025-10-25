package com.kiwisha.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO para crear una nueva categoría.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearCategoriaDTO {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 50, message = "El título debe tener entre 3 y 50 caracteres")
    private String titulo;
    
    @Size(max = 200, message = "El resumen no puede exceder 200 caracteres")
    private String resumen;
}

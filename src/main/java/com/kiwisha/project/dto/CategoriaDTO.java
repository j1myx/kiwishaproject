package com.kiwisha.project.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO para categorías de productos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDTO {
    
    private Integer categoriaId;
    private String titulo;
    private String resumen;
    private Long cantidadProductos;
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}

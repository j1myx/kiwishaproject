package com.kiwisha.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * DTO para actualizar un producto existente.
 * Similar a CrearProductoDTO pero todos los campos son opcionales.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarProductoDTO {
    
    @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
    private String titulo;
    
    @Size(max = 500, message = "El resumen no puede exceder 500 caracteres")
    private String resumen;
    
    private String descripcion;
    
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    private BigDecimal precioAnterior;
    
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad;
    
    private Integer categoriaId;
    
    private String sku;
    private BigDecimal peso;
    private String unidadMedida;
    private String imagen;
    
    private Boolean publicado;
    private Boolean destacado;
    private Boolean nuevo;
    private Boolean enOferta;
    
    @Size(max = 200, message = "El meta título no puede exceder 200 caracteres")
    private String metaTitulo;
    
    @Size(max = 500, message = "La meta descripción no puede exceder 500 caracteres")
    private String metaDescripcion;
}

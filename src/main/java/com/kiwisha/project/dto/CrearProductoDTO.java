package com.kiwisha.project.dto;

import com.kiwisha.project.model.EstadoProducto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * DTO para crear un nuevo producto.
 * Contiene validaciones para el formulario.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearProductoDTO {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
    private String titulo;
    
    @Size(max = 500, message = "El resumen no puede exceder 500 caracteres")
    private String resumen;
    
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;
    
    private BigDecimal precioAnterior;
    
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad = 0;  // Valor por defecto: 0
    
    private Integer categoriaId;  // Opcional
    
    private String sku;
    
    @Size(max = 200, message = "El slug no puede exceder 200 caracteres")
    @Pattern(regexp = "^[a-z0-9-]*$", message = "El slug solo puede contener letras minúsculas, números y guiones")
    private String slug;
    
    private BigDecimal peso;
    private String unidadMedida;
    private String imagen;
    
    // Precio de oferta
    private BigDecimal precioOferta;
    
    private Boolean publicado = false;
    private EstadoProducto estado = EstadoProducto.BORRADOR;  // Por defecto: BORRADOR
    private Boolean destacado = false;
    private Boolean nuevo = false;
    private Boolean enOferta = false;
    
    // SEO
    @Size(max = 200, message = "El meta título no puede exceder 200 caracteres")
    private String metaTitulo;
    
    @Size(max = 500, message = "La meta descripción no puede exceder 500 caracteres")
    private String metaDescripcion;
    
    // Métodos adicionales para compatibilidad con Lombok
    public Boolean getPublicado() {
        return publicado;
    }
    
    public Boolean getDestacado() {
        return destacado;
    }
    
    public Boolean getNuevo() {
        return nuevo;
    }
    
    public Boolean getEnOferta() {
        return enOferta;
    }
}

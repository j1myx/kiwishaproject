package com.kiwisha.project.dto;

import com.kiwisha.project.model.EstadoProducto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para visualización de productos.
 * Usado en respuestas de API y vistas.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {
    
    private Integer productoId;
    private String titulo;
    private String resumen;
    private String descripcion;
    private String imagen;
    private BigDecimal precio;
    private BigDecimal precioAnterior;
    private Integer cantidad;
    private String sku;
    private BigDecimal peso;
    private String unidadMedida;
    private Boolean publicado;
    private EstadoProducto estado;  // BORRADOR, PUBLICADO, ARCHIVADO
    private Boolean destacado;
    private Boolean nuevo;
    private Boolean enOferta;
    private String slug;
    
    // Información de la categoría
    private Integer categoriaId;
    private String categoriaNombre;
    
    // SEO
    private String metaTitulo;
    private String metaDescripcion;
    
    // Información calculada
    private Double promedioCalificacion;
    private Integer cantidadReviews;
    private Integer porcentajeDescuento;
    private Boolean disponible;
    
    // Imágenes adicionales
    private List<String> imagenesAdicionales;
    
    // Auditoría
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
}

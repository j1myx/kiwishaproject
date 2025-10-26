package com.kiwisha.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un producto en el sistema de e-commerce.
 * Incluye información básica, inventario, precios y relaciones con imágenes y reviews.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "productos", indexes = {
    @Index(name = "idx_producto_categoria", columnList = "categoria_id"),
    @Index(name = "idx_producto_publicado", columnList = "publicado"),
    @Index(name = "idx_producto_titulo", columnList = "titulo")
})
public class Producto extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "producto_id")
    private Integer productoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @NotNull(message = "La categoría es obligatoria")
    private Categoria categoria;

    @Column(name = "titulo", nullable = false, length = 200)
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 200, message = "El título debe tener entre 3 y 200 caracteres")
    private String titulo;

    @Column(name = "resumen", length = 500)
    @Size(max = 500, message = "El resumen no puede exceder 500 caracteres")
    private String resumen;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "imagen", length = 255)
    private String imagen;

    @Column(name = "precio", nullable = false, precision = 16, scale = 4)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Column(name = "precio_anterior", precision = 16, scale = 4)
    private BigDecimal precioAnterior;

    @Column(name = "cantidad", nullable = false)
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer cantidad = 0;

    @Column(name = "sku", unique = true, length = 50)
    private String sku;

    @Column(name = "peso", precision = 10, scale = 2)
    private BigDecimal peso;

    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida;

    @Column(name = "publicado", nullable = false)
    private Boolean publicado = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private EstadoProducto estado = EstadoProducto.BORRADOR;

    @Column(name = "destacado")
    private Boolean destacado = false;

    @Column(name = "nuevo")
    private Boolean nuevo = false;

    @Column(name = "en_oferta")
    private Boolean enOferta = false;

    @Column(name = "meta_titulo", length = 200)
    private String metaTitulo;

    @Column(name = "meta_descripcion", length = 500)
    private String metaDescripcion;

    @Column(name = "slug", unique = true, length = 200)
    private String slug;

    // Relaciones
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductoImagen> productoImagenes = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PedidoElemento> pedidoElementos = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductoPagina> productoPaginas = new ArrayList<>();

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    // Métodos de utilidad
    
    /**
     * Verifica si el producto está disponible para la venta
     * Requiere estar publicado (estado PUBLICADO) y tener stock disponible
     */
    public boolean estaDisponible() {
        return estado == EstadoProducto.PUBLICADO && cantidad > 0;
    }

    /**
     * Verifica si hay suficiente stock para una cantidad solicitada
     */
    public boolean tieneSuficienteStock(int cantidadSolicitada) {
        return cantidad >= cantidadSolicitada;
    }

    /**
     * Reduce el stock del producto
     */
    public void reducirStock(int cantidadVendida) {
        if (cantidadVendida > cantidad) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        this.cantidad -= cantidadVendida;
    }

    /**
     * Incrementa el stock del producto
     */
    public void aumentarStock(int cantidadAgregada) {
        this.cantidad += cantidadAgregada;
    }

    /**
     * Calcula el promedio de calificaciones del producto
     */
    public Double getPromedioCalificacion() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .filter(Review::esVisible)
                .mapToInt(Review::getCalificacion)
                .average()
                .orElse(0.0);
    }

    /**
     * Obtiene el número total de reviews aprobadas
     */
    public int getCantidadReviews() {
        if (reviews == null) {
            return 0;
        }
        return (int) reviews.stream()
                .filter(Review::esVisible)
                .count();
    }

    /**
     * Calcula el porcentaje de descuento si hay precio anterior
     */
    public Integer getPorcentajeDescuento() {
        if (precioAnterior == null || precioAnterior.compareTo(precio) <= 0) {
            return 0;
        }
        BigDecimal diferencia = precioAnterior.subtract(precio);
        BigDecimal porcentaje = diferencia.divide(precioAnterior, 4, BigDecimal.ROUND_HALF_UP)
                                          .multiply(BigDecimal.valueOf(100));
        return porcentaje.intValue();
    }
}

package com.kiwisha.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidad que representa las reseñas/valoraciones de productos.
 * Los clientes pueden dejar valoraciones y comentarios sobre productos comprados.
 */
@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    @Column(nullable = false)
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    @Column(length = 200)
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "El comentario es obligatorio")
    private String comentario;

    @Column(nullable = false)
    private Boolean aprobado = false;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "compra_verificada")
    private Boolean compraVerificada = false;

    @Column(name = "util_count")
    private Integer utilCount = 0;

    /**
     * Verifica si la review es visible públicamente
     */
    public boolean esVisible() {
        return activo && aprobado;
    }
}

package com.kiwisha.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad que representa un elemento individual de un pedido.
 */
@Entity
@Table(name = "pedido_elementos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoElemento extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_elemento_id")
    private Integer pedidoElementoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @NotNull(message = "El pedido es obligatorio")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Producto producto;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El precio es obligatorio")
    private BigDecimal precio;

    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @Column(precision = 5, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    /**
     * Calcula el subtotal del elemento (precio × cantidad)
     */
    public BigDecimal getSubtotal() {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    /**
     * Calcula el subtotal con descuento aplicado
     */
    public BigDecimal getSubtotalConDescuento() {
        BigDecimal subtotal = getSubtotal();
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal montoDescuento = subtotal.multiply(descuento).divide(BigDecimal.valueOf(100));
            return subtotal.subtract(montoDescuento);
        }
        return subtotal;
    }
}

package com.kiwisha.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa cupones de descuento.
 * Permite aplicar descuentos porcentuales o de monto fijo a las compras.
 */
@Entity
@Table(name = "cupones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cupon extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cupon_id")
    private Integer cuponId;

    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false)
    @NotNull(message = "El tipo de descuento es obligatorio")
    private TipoDescuento tipoDescuento;

    @Column(name = "valor_descuento", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El valor de descuento es obligatorio")
    private BigDecimal valorDescuento;

    @Column(name = "compra_minima", precision = 10, scale = 2)
    private BigDecimal compraMinima;

    @Column(name = "descuento_maximo", precision = 10, scale = 2)
    private BigDecimal descuentoMaximo;

    @Column(name = "fecha_inicio", nullable = false)
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @Column(name = "cantidad_maxima_usos")
    private Integer cantidadMaximaUsos;

    @Column(name = "cantidad_usos_actual")
    private Integer cantidadUsosActual = 0;

    @Column(name = "uso_por_cliente")
    private Integer usoPorCliente = 1;

    @Column(nullable = false)
    private Boolean activo = true;

    public enum TipoDescuento {
        PORCENTAJE,
        MONTO_FIJO
    }

    /**
     * Verifica si el cupón es válido en la fecha actual
     */
    public boolean esValido() {
        LocalDateTime ahora = LocalDateTime.now();
        return activo 
            && ahora.isAfter(fechaInicio) 
            && ahora.isBefore(fechaFin)
            && (cantidadMaximaUsos == null || cantidadUsosActual < cantidadMaximaUsos);
    }

    /**
     * Calcula el descuento aplicable para un monto dado
     */
    public BigDecimal calcularDescuento(BigDecimal montoCompra) {
        if (!esValido() || (compraMinima != null && montoCompra.compareTo(compraMinima) < 0)) {
            return BigDecimal.ZERO;
        }

        BigDecimal descuento;
        if (tipoDescuento == TipoDescuento.PORCENTAJE) {
            descuento = montoCompra.multiply(valorDescuento).divide(BigDecimal.valueOf(100));
        } else {
            descuento = valorDescuento;
        }

        // Aplicar descuento máximo si está definido
        if (descuentoMaximo != null && descuento.compareTo(descuentoMaximo) > 0) {
            descuento = descuentoMaximo;
        }

        return descuento;
    }
}

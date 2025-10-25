package com.kiwisha.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad que representa los métodos de envío disponibles.
 * Ejemplos: Envío Estándar, Envío Express, Recojo en tienda.
 */
@Entity
@Table(name = "metodos_envio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetodoEnvio extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metodo_envio_id")
    private Integer metodoEnvioId;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El costo es obligatorio")
    private BigDecimal costo;

    @Column(name = "dias_estimados")
    private Integer diasEstimados;

    @Column(nullable = false)
    private Boolean activo = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoEnvio tipo = TipoEnvio.ESTANDAR;

    public enum TipoEnvio {
        ESTANDAR,
        EXPRESS,
        INTERNACIONAL,
        RECOJO_EN_TIENDA
    }
}

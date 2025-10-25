package com.kiwisha.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un pedido realizado por un cliente.
 * Incluye información de contacto, envío y estado del pedido.
 */
@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pedido_id")
    private Integer pedidoId;

    @Column(name = "codigo", unique = true, nullable = false, length = 50)
    @NotBlank(message = "El código es obligatorio")
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Información del cliente (para clientes no registrados)
    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Column(nullable = false, length = 150)
    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Column(length = 20)
    private String telefono;

    // Información de envío
    @Column(nullable = false, length = 255)
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El país es obligatorio")
    private String pais;

    // Método de envío
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_envio_id")
    private MetodoEnvio metodoEnvio;

    // Totales
    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El subtotal es obligatorio")
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El total es obligatorio")
    private BigDecimal total;

    // Cupón aplicado (DEPRECATED - funcionalidad removida en v1.5.1)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "cupon_id")
    // private Cupon cupon;

    // Estado del pedido
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "El estado es obligatorio")
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String notas;

    // Relaciones
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<PedidoElemento> pedidoElementos = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private List<Transaccion> transacciones = new ArrayList<>();

    public enum EstadoPedido {
        PENDIENTE,
        CONFIRMADO,
        ENVIADO,
        ENTREGADO,
        CANCELADO
    }

    /**
     * Agrega un elemento al pedido
     */
    public void agregarElemento(PedidoElemento elemento) {
        pedidoElementos.add(elemento);
        elemento.setPedido(this);
    }

    /**
     * Verifica si el pedido puede ser cancelado
     */
    public boolean puedeCancelarse() {
        return estado == EstadoPedido.PENDIENTE || estado == EstadoPedido.CONFIRMADO;
    }

    /**
     * Obtiene el nombre completo del cliente
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Obtiene la dirección completa de envío
     */
    public String getDireccionCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append(direccion).append(", ");
        sb.append(ciudad).append(", ");
        sb.append(provincia);
        if (codigoPostal != null) {
            sb.append(" ").append(codigoPostal);
        }
        sb.append(", ").append(pais);
        return sb.toString();
    }
}

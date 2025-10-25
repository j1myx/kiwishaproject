package com.kiwisha.project.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para información completa de pedidos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoDTO {
    
    private Integer pedidoId;
    private String codigo;
    private Integer clienteId;
    private String clienteNombre;
    private String clienteEmail;
    
    // Información del cliente
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    
    // Dirección de envío
    private String direccion;
    private String ciudad;
    private String provincia;
    private String codigoPostal;
    private String pais;
    
    // Método de envío
    private Integer metodoEnvioId;
    private String metodoEnvioNombre;
    
    // Totales
    private BigDecimal subtotal;
    private BigDecimal descuento;  // Reservado para descuentos futuros
    private BigDecimal costoEnvio;
    private BigDecimal total;
    
    // Estado
    private String estado;
    private String notas;
    
    // Fechas
    private LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;
    
    // Items del pedido
    private List<PedidoElementoDTO> elementos;
}

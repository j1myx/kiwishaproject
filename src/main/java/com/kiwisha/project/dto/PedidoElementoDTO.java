package com.kiwisha.project.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO para elementos de un pedido.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoElementoDTO {
    
    private Integer pedidoElementoId;
    private Integer productoId;
    private String productoTitulo;
    private String productoImagen;
    private BigDecimal precio;
    private Integer cantidad;
    private BigDecimal descuento;
    private BigDecimal subtotal;
}

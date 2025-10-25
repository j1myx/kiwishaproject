package com.kiwisha.project.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * DTO para items del carrito de compras.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoItemDTO {
    
    private Integer carritoItemId;
    private Integer productoId;
    private String productoTitulo;
    private String productoImagen;
    private BigDecimal precio;
    private Integer cantidad;
    private BigDecimal subtotal;
    private Boolean disponible;
    private Integer stockDisponible;
}

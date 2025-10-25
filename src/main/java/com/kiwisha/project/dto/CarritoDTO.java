package com.kiwisha.project.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para el carrito completo de compras.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoDTO {
    
    private List<CarritoItemDTO> items;
    private Integer cantidadItems;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal costoEnvio;
    private BigDecimal total;
    private String codigoCupon;
}

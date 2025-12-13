package com.kiwisha.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoImagenItemDTO {
    private Integer productoImagenId;
    private String nombre;
    private String ruta;
}

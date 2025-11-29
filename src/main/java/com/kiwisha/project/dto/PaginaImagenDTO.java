package com.kiwisha.project.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginaImagenDTO {
    private Integer paginaImagenId;
    private PaginaDTO pagina;
    private String nombre;
    private String ruta;
    private Integer creadoPor;
    private LocalDateTime creadoEn;
    private Integer actualizadoPor;
    private LocalDateTime actualizadoEn;
}

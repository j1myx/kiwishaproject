package com.kiwisha.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

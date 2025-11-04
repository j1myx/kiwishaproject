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
public class PaginaEtiquetaDTO {
    private Integer paginaEtiquetaId;
    private PaginaDTO pagina;
    private EtiquetaDTO etiqueta;
    private Integer creadoPor;
    private LocalDateTime creadoEn;
    private Integer actualizadoPor;
    private LocalDateTime actualizadoEn;
}

package com.kiwisha.project.dto;

import com.kiwisha.project.model.Pagina;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaginaDTO {
    private Integer paginaId;
    private String titulo;
    private String url;
    private String resumen;
    private String contenido;
    private Pagina.TipoPagina tipo;
    private Boolean publicado;
    private Integer creadoPor;
    private LocalDateTime creadoEn;
    private Integer actualizadoPor;
    private LocalDateTime actualizadoEn;
    private List<EtiquetaDTO> etiquetas;
}

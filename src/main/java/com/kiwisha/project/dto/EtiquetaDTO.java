package com.kiwisha.project.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtiquetaDTO {
    private Integer etiquetaId;
    private String nombre;
    private Integer creadoPor;
    private LocalDateTime creadoEn;
    private Integer actualizadoPor;
    private LocalDateTime actualizadoEn;
}

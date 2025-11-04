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
public class EtiquetaDTO {
    private Integer etiquetaId;
    private String nombre;
    private Integer creadoPor;
    private LocalDateTime creadoEn;
    private Integer actualizadoPor;
    private LocalDateTime actualizadoEn;
}

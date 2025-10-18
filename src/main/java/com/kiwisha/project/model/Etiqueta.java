package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "etiquetas")
public class Etiqueta {
    @Id
    @Column(name = "etiqueta_id")
    private Integer etiquetaId;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "creado_por")
    private Integer creadoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_por")
    private Integer actualizadoPor;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "etiqueta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaginaEtiqueta> paginaEtiquetas;
}

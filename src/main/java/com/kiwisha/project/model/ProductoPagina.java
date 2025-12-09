package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "productos_paginas")
public class ProductoPagina {
    @Id
    @Column(name = "producto_pagina_id")
    private Integer productoPaginaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagina_id")
    private Pagina pagina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "creado_por")
    private Integer creadoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_por")
    private Integer actualizadoPor;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
    
}

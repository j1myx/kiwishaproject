package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pagina_imagenes")
public class PaginaImagen {
    @Id
    @Column(name = "pagina_imagen_id")
    private Integer paginaImagenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagina_id")
    private Pagina pagina;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "ruta", length = 100)
    private String ruta;

    @Column(name = "creado_por")
    private Integer creadoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_por")
    private Integer actualizadoPor;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
}

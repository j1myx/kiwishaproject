package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pagina_imagenes")
public class PaginaImagen extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pagina_imagen_id")
    private Integer paginaImagenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagina_id")
    private Pagina pagina;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "ruta", length = 100)
    private String ruta;
}

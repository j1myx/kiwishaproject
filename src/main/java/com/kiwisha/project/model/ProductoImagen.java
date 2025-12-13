package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "producto_imagenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoImagen extends AuditableEntity {
    @Id
    @Column(name = "producto_imagen_id")
    private Integer productoImagenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "ruta", length = 100)
    private String ruta;
}

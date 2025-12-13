package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "etiquetas")
public class Etiqueta extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "etiqueta_id")
    private Integer etiquetaId;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "resumen", length = 255)
    private String resumen;

    @OneToMany(mappedBy = "etiqueta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaginaEtiqueta> paginaEtiquetas;
}

package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "etiquetas")
public class Etiqueta extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "etiqueta_id")
    private Integer etiquetaId;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @OneToMany(mappedBy = "etiqueta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaginaEtiqueta> paginaEtiquetas;
}

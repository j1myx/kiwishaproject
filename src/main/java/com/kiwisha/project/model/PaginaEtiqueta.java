package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "paginas_etiquetas")
public class PaginaEtiqueta extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pagina_etiqueta_id")
    private Integer paginaEtiquetaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pagina_id")
    private Pagina pagina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etiqueta_id")
    private Etiqueta etiqueta;
}

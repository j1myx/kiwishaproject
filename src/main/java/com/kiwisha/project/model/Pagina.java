package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "paginas")
public class Pagina extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pagina_id")
    private Integer paginaId;

    @Column(name = "titulo", length = 50)
    private String titulo;

    @Column(name = "url", length = 50)
    private String url;

    @Column(name = "resumen", length = 50)
    private String resumen;

    @Lob
    @Column(name = "contenido", columnDefinition = "LONGTEXT")
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoPagina tipo;

    @Column(name = "publicado")
    private Boolean publicado;

    @OneToMany(mappedBy = "pagina", fetch = FetchType.LAZY)
    private List<PaginaImagen> paginaImagenes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "paginas_etiquetas",
            joinColumns = @JoinColumn(name = "pagina_id", insertable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "etiqueta_id", insertable = false, updatable = false))
    private List<Etiqueta> etiquetas;

    @OneToMany(mappedBy = "pagina", fetch = FetchType.LAZY)
    private List<PaginaEtiqueta> paginaEtiquetas;

    @OneToMany(mappedBy = "pagina", fetch = FetchType.LAZY)
    private List<ProductoPagina> productoPaginas;

    public enum TipoPagina {
        BASE, ARTICULOS
    }
}

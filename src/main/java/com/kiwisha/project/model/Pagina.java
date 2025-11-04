package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
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
    @Column(name = "contenido")
    private String contenido;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoPagina tipo;

    @Column(name = "publicado")
    private Boolean publicado;

    @OneToMany(mappedBy = "pagina", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaginaImagen> paginaImagenes;

    @OneToMany(mappedBy = "pagina", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaginaEtiqueta> paginaEtiquetas;

    @OneToMany(mappedBy = "pagina", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductoPagina> productoPaginas;

    public enum TipoPagina {
        BASE, NOTICIAS, ARTICULOS
    }
}

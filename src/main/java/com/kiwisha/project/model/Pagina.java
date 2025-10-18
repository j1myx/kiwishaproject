package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "paginas")
public class Pagina {
    @Id
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

    @Column(name = "creado_por")
    private Integer creadoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_por")
    private Integer actualizadoPor;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

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

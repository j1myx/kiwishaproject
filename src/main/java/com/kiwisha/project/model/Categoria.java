package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @Column(name = "categoria_id")
    private Integer categoriaId;

    @Column(name = "titulo", length = 50)
    private String titulo;

    @Column(name = "resumen", length = 50)
    private String resumen;

    @Column(name = "creado_por")
    private Integer creadoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_por")
    private Integer actualizadoPor;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Producto> productos;
}

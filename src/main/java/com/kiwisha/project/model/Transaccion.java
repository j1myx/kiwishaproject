package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transacciones")
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaccion_id")
    private Integer transaccionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "codigo", length = 100)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoTransaccion tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo")
    private ModoTransaccion modo;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "creado_por")
    private Integer creadoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_por")
    private Integer actualizadoPor;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public enum TipoTransaccion {
        PRESENCIAL, VIRTUAL
    }

    public enum ModoTransaccion {
        EFECTIVO, TARJETA
    }
}

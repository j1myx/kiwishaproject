package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cliente_id")
    private Integer clienteId;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "telefono", length = 100)
    private String telefono;

    @Column(name = "direccion", length = 100)
    private String direccion;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "creado_por")
    private Integer creadoPor;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_por")
    private Integer actualizadoPor;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos;

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
        if (creadoPor == null) {
            creadoPor = 1;
        }
        if (actualizadoEn == null) {
            actualizadoEn = LocalDateTime.now();
        }
        if (actualizadoPor == null) {
            actualizadoPor = creadoPor != null ? creadoPor : 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
        if (actualizadoPor == null) {
            actualizadoPor = 1;
        }
    }
}

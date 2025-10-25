package com.kiwisha.project.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Clase base abstracta que proporciona campos de auditoría automática
 * para todas las entidades del sistema.
 * 
 * Esta clase implementa el patrón Template Method para auditoría JPA.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    @CreatedBy
    @Column(name = "creado_por", nullable = false, updatable = false)
    private Integer creadoPor;

    @CreatedDate
    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @LastModifiedBy
    @Column(name = "actualizado_por")
    private Integer actualizadoPor;

    @LastModifiedDate
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) {
            creadoEn = LocalDateTime.now();
        }
        if (creadoPor == null) {
            creadoPor = 1; // Usuario del sistema por defecto
        }
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
        if (actualizadoPor == null) {
            actualizadoPor = 1; // Usuario del sistema por defecto
        }
    }
}

package com.kiwisha.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Entidad que representa las direcciones de envío de los clientes.
 * Un cliente puede tener múltiples direcciones registradas.
 */
@Entity
@Table(name = "direcciones_envio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DireccionEnvio extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "direccion_id")
    private Integer direccionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "nombre_completo", nullable = false, length = 200)
    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @Column(nullable = false, length = 20)
    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 7, max = 20, message = "El teléfono debe tener entre 7 y 20 caracteres")
    private String telefono;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @Column(length = 100)
    private String provincia;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El país es obligatorio")
    private String pais;

    @Column(name = "es_principal")
    private Boolean esPrincipal = false;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(columnDefinition = "TEXT")
    private String referencia;
}

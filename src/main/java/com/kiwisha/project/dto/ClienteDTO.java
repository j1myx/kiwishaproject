package com.kiwisha.project.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * DTO para información de clientes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteDTO {
    
    private Integer clienteId;
    private String nombre;
    private String telefono;
    private String direccion;
    private String email;
    private Integer cantidadPedidos;
    private LocalDateTime creadoEn;
}

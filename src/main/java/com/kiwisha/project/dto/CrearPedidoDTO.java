package com.kiwisha.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO para crear un nuevo pedido (checkout).
 * Contiene toda la información del formulario de checkout.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearPedidoDTO {
    
    // ID del cliente (si está registrado) - opcional
    private Integer clienteId;
    
    // Datos del cliente
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    // Dirección de envío
    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;
    
    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;
    
    @NotBlank(message = "La provincia es obligatoria")
    private String provincia;
    
    private String codigoPostal;
    
    @NotBlank(message = "El país es obligatorio")
    private String pais;
    
    // Método de envío
    @NotNull(message = "El método de envío es obligatorio")
    private Integer metodoEnvioId;
    
    // Cupón de descuento (opcional)
    private String codigoCupon;
    
    // Notas adicionales (opcional)
    private String notas;
    
    // Aceptación de términos
    @AssertTrue(message = "Debe aceptar los términos y condiciones")
    private Boolean aceptaTerminos;
}

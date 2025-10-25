package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.ApiResponseDTO;
import com.kiwisha.project.dto.ClienteDTO;
import com.kiwisha.project.dto.CrearClienteDTO;
import com.kiwisha.project.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de clientes
 * Solo endpoints administrativos (clientes registran desde checkout)
 */
@RestController
@RequestMapping("/api/admin/clientes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Clientes", description = "API para gestión de clientes (solo ADMIN)")
public class ClienteApiController {

    private final ClienteService clienteService;

    @GetMapping
    @Operation(summary = "[ADMIN] Listar todos los clientes", description = "Obtiene todos los clientes con paginación")
    public ResponseEntity<ApiResponseDTO<Page<ClienteDTO>>> listarClientes(Pageable pageable) {
        Page<ClienteDTO> clientes = clienteService.obtenerTodosClientes(pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<ClienteDTO>>builder()
                .success(true)
                .message("Clientes obtenidos exitosamente")
                .data(clientes)
                .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "[ADMIN] Obtener cliente por ID", description = "Obtiene un cliente específico por su ID")
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> obtenerClientePorId(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {
        
        ClienteDTO cliente = clienteService.obtenerClientePorId(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ClienteDTO>builder()
                .success(true)
                .message("Cliente encontrado")
                .data(cliente)
                .build()
        );
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "[ADMIN] Buscar cliente por email", description = "Busca un cliente por su dirección de email")
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> buscarPorEmail(
            @Parameter(description = "Email del cliente") @PathVariable String email) {
        
        ClienteDTO cliente = clienteService.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con email: " + email));
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ClienteDTO>builder()
                .success(true)
                .message("Cliente encontrado")
                .data(cliente)
                .build()
        );
    }

    @GetMapping("/telefono/{telefono}")
    @Operation(summary = "[ADMIN] Buscar cliente por teléfono", description = "Busca un cliente por su número de teléfono")
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> buscarPorTelefono(
            @Parameter(description = "Teléfono del cliente") @PathVariable String telefono) {
        
        ClienteDTO cliente = clienteService.buscarPorTelefono(telefono)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con teléfono: " + telefono));
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ClienteDTO>builder()
                .success(true)
                .message("Cliente encontrado")
                .data(cliente)
                .build()
        );
    }

    @GetMapping("/{id}/pedidos/count")
    @Operation(summary = "[ADMIN] Contar pedidos de cliente", description = "Obtiene el número de pedidos de un cliente")
    public ResponseEntity<ApiResponseDTO<Long>> contarPedidosPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {
        
        long count = clienteService.contarPedidosPorCliente(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Long>builder()
                .success(true)
                .message("Conteo de pedidos obtenido exitosamente")
                .data(count)
                .build()
        );
    }

    @PostMapping
    @Operation(summary = "[ADMIN] Crear cliente", description = "Crea un nuevo cliente manualmente")
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> crearCliente(
            @Valid @RequestBody CrearClienteDTO crearClienteDTO) {
        
        ClienteDTO cliente = clienteService.crearCliente(crearClienteDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<ClienteDTO>builder()
                .success(true)
                .message("Cliente creado exitosamente")
                .data(cliente)
                .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "[ADMIN] Actualizar cliente", description = "Actualiza los datos de un cliente")
    public ResponseEntity<ApiResponseDTO<ClienteDTO>> actualizarCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer id,
            @Valid @RequestBody CrearClienteDTO crearClienteDTO) {
        
        ClienteDTO cliente = clienteService.actualizarCliente(id, crearClienteDTO);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<ClienteDTO>builder()
                .success(true)
                .message("Cliente actualizado exitosamente")
                .data(cliente)
                .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "[ADMIN] Eliminar cliente", description = "Elimina un cliente si no tiene pedidos asociados")
    public ResponseEntity<ApiResponseDTO<Void>> eliminarCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {
        
        clienteService.eliminarCliente(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Cliente eliminado exitosamente")
                .build()
        );
    }
}

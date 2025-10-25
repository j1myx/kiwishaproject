package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.ApiResponseDTO;
import com.kiwisha.project.dto.CrearPedidoDTO;
import com.kiwisha.project.dto.PedidoDTO;
import com.kiwisha.project.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de pedidos
 * Endpoints públicos para clientes y administrativos
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "API para gestión de pedidos")
public class PedidoApiController {

    private final PedidoService pedidoService;

    // ==================== ENDPOINTS PÚBLICOS ====================

    @PostMapping("/public/pedidos")
    @Operation(summary = "Crear pedido", description = "Crea un nuevo pedido desde el carrito actual")
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> crearPedido(
            @Valid @RequestBody CrearPedidoDTO crearPedidoDTO,
            HttpSession session) {
        
        String sessionId = session.getId();
        PedidoDTO pedido = pedidoService.crearPedido(sessionId, crearPedidoDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<PedidoDTO>builder()
                .success(true)
                .message("Pedido creado exitosamente")
                .data(pedido)
                .build()
        );
    }

    @GetMapping("/public/pedidos/codigo/{codigo}")
    @Operation(summary = "Buscar pedido por código", description = "Obtiene un pedido por su código único")
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> obtenerPedidoPorCodigo(
            @Parameter(description = "Código del pedido") @PathVariable String codigo) {
        
        PedidoDTO pedido = pedidoService.obtenerPedidoPorCodigo(codigo);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<PedidoDTO>builder()
                .success(true)
                .message("Pedido encontrado")
                .data(pedido)
                .build()
        );
    }

    @GetMapping("/public/pedidos/cliente/email")
    @Operation(summary = "Buscar pedidos por email", description = "Obtiene los pedidos de un cliente por su email")
    public ResponseEntity<ApiResponseDTO<Page<PedidoDTO>>> obtenerPedidosPorEmail(
            @Parameter(description = "Email del cliente") @RequestParam String email,
            Pageable pageable) {
        
        Page<PedidoDTO> pedidos = pedidoService.obtenerPedidosPorEmail(email, pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<PedidoDTO>>builder()
                .success(true)
                .message("Pedidos obtenidos exitosamente")
                .data(pedidos)
                .build()
        );
    }

    // ==================== ENDPOINTS ADMINISTRATIVOS ====================

    @GetMapping("/admin/pedidos")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Listar todos los pedidos", description = "Obtiene todos los pedidos con paginación (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<Page<PedidoDTO>>> listarPedidos(Pageable pageable) {
        Page<PedidoDTO> pedidos = pedidoService.obtenerTodosPedidos(pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<PedidoDTO>>builder()
                .success(true)
                .message("Pedidos obtenidos exitosamente")
                .data(pedidos)
                .build()
        );
    }

    @GetMapping("/admin/pedidos/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Obtener pedido por ID", description = "Obtiene un pedido específico por su ID (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> obtenerPedidoPorId(
            @Parameter(description = "ID del pedido") @PathVariable Integer id) {
        
        PedidoDTO pedido = pedidoService.obtenerPedidoPorId(id);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<PedidoDTO>builder()
                .success(true)
                .message("Pedido encontrado")
                .data(pedido)
                .build()
        );
    }

    @GetMapping("/admin/pedidos/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Obtener pedidos por cliente", description = "Obtiene todos los pedidos de un cliente (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<Page<PedidoDTO>>> obtenerPedidosPorCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer clienteId,
            Pageable pageable) {
        
        Page<PedidoDTO> pedidos = pedidoService.obtenerPedidosPorCliente(clienteId, pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<PedidoDTO>>builder()
                .success(true)
                .message("Pedidos del cliente obtenidos exitosamente")
                .data(pedidos)
                .build()
        );
    }

    @GetMapping("/admin/pedidos/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Buscar pedidos por estado", description = "Obtiene pedidos por estado (PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO) (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<Page<PedidoDTO>>> obtenerPedidosPorEstado(
            @Parameter(description = "Estado del pedido") @PathVariable String estado,
            Pageable pageable) {
        
        Page<PedidoDTO> pedidos = pedidoService.obtenerPedidosPorEstado(estado, pageable);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Page<PedidoDTO>>builder()
                .success(true)
                .message("Pedidos obtenidos exitosamente")
                .data(pedidos)
                .build()
        );
    }

    @PatchMapping("/admin/pedidos/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Actualizar estado del pedido", description = "Actualiza el estado de un pedido (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> actualizarEstadoPedido(
            @Parameter(description = "ID del pedido") @PathVariable Integer id,
            @Parameter(description = "Nuevo estado") @RequestParam String estado) {
        
        PedidoDTO pedido = pedidoService.actualizarEstadoPedido(id, estado);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<PedidoDTO>builder()
                .success(true)
                .message("Estado del pedido actualizado exitosamente")
                .data(pedido)
                .build()
        );
    }

    @DeleteMapping("/admin/pedidos/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[ADMIN] Cancelar pedido", description = "Cancela un pedido y restaura el stock (requiere rol ADMIN)")
    public ResponseEntity<ApiResponseDTO<PedidoDTO>> cancelarPedido(
            @Parameter(description = "ID del pedido") @PathVariable Integer id,
            @Parameter(description = "Motivo de cancelación") @RequestParam(defaultValue = "Cancelado por administrador") String motivo) {
        
        PedidoDTO pedido = pedidoService.cancelarPedido(id, motivo);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<PedidoDTO>builder()
                .success(true)
                .message("Pedido cancelado exitosamente")
                .data(pedido)
                .build()
        );
    }
}

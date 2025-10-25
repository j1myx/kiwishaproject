package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.AgregarCarritoDTO;
import com.kiwisha.project.dto.ApiResponseDTO;
import com.kiwisha.project.dto.CarritoDTO;
import com.kiwisha.project.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión del carrito de compras
 * Maneja operaciones del carrito basado en sesión
 */
@RestController
@RequestMapping("/api/public/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "API para gestión del carrito de compras")
public class CarritoApiController {

    private final CarritoService carritoService;

    @GetMapping
    @Operation(summary = "Obtener carrito actual", description = "Obtiene el carrito de compras de la sesión actual")
    public ResponseEntity<ApiResponseDTO<CarritoDTO>> obtenerCarrito(HttpSession session) {
        String sessionId = session.getId();
        CarritoDTO carrito = carritoService.obtenerCarrito(sessionId);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<CarritoDTO>builder()
                .success(true)
                .message("Carrito obtenido exitosamente")
                .data(carrito)
                .build()
        );
    }

    @PostMapping("/agregar")
    @Operation(summary = "Agregar producto al carrito", description = "Agrega un producto al carrito o actualiza cantidad si ya existe")
    public ResponseEntity<ApiResponseDTO<CarritoDTO>> agregarAlCarrito(
            @Valid @RequestBody AgregarCarritoDTO agregarCarritoDTO,
            HttpSession session) {
        
        String sessionId = session.getId();
        CarritoDTO carrito = carritoService.agregarItem(sessionId, agregarCarritoDTO);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<CarritoDTO>builder()
                .success(true)
                .message("Producto agregado al carrito")
                .data(carrito)
                .build()
        );
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Actualizar cantidad de item", description = "Actualiza la cantidad de un item específico del carrito")
    public ResponseEntity<ApiResponseDTO<CarritoDTO>> actualizarCantidad(
            @Parameter(description = "ID del item del carrito") @PathVariable Integer itemId,
            @Parameter(description = "Nueva cantidad") @RequestParam Integer cantidad,
            HttpSession session) {
        
        String sessionId = session.getId();
        CarritoDTO carrito = carritoService.actualizarCantidad(sessionId, itemId, cantidad);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<CarritoDTO>builder()
                .success(true)
                .message("Cantidad actualizada exitosamente")
                .data(carrito)
                .build()
        );
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Eliminar item del carrito", description = "Elimina un item específico del carrito")
    public ResponseEntity<ApiResponseDTO<CarritoDTO>> eliminarItem(
            @Parameter(description = "ID del item del carrito") @PathVariable Integer itemId,
            HttpSession session) {
        
        String sessionId = session.getId();
        carritoService.eliminarItem(sessionId, itemId);
        CarritoDTO carrito = carritoService.obtenerCarrito(sessionId);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<CarritoDTO>builder()
                .success(true)
                .message("Item eliminado del carrito")
                .data(carrito)
                .build()
        );
    }

    @DeleteMapping("/limpiar")
    @Operation(summary = "Limpiar carrito", description = "Elimina todos los items del carrito")
    public ResponseEntity<ApiResponseDTO<Void>> limpiarCarrito(HttpSession session) {
        String sessionId = session.getId();
        carritoService.limpiarCarrito(sessionId);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Void>builder()
                .success(true)
                .message("Carrito limpiado exitosamente")
                .build()
        );
    }

    @GetMapping("/validar-stock")
    @Operation(summary = "Validar stock del carrito", description = "Verifica que todos los productos del carrito tengan stock disponible")
    public ResponseEntity<ApiResponseDTO<Boolean>> validarStock(HttpSession session) {
        String sessionId = session.getId();
        boolean stockValido = carritoService.validarStockCarrito(sessionId);
        
        return ResponseEntity.ok(
            ApiResponseDTO.<Boolean>builder()
                .success(true)
                .message(stockValido ? "Stock disponible" : "Stock insuficiente para algunos productos")
                .data(stockValido)
                .build()
        );
    }
}

package com.kiwisha.project.service;

import com.kiwisha.project.dto.CrearPedidoDTO;
import com.kiwisha.project.dto.PedidoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para la gestión de pedidos.
 * Implementación simplificada sin integración con couriers externos.
 */
public interface PedidoService {

    /**
     * Crea un nuevo pedido desde el carrito actual.
     * Valida stock, aplica cupón si existe, genera pedido y elementos,
     * reduce stock de productos y limpia el carrito.
     */
    PedidoDTO crearPedido(String sessionId, CrearPedidoDTO crearPedidoDTO);

    /**
     * Obtiene un pedido por su ID.
     */
    PedidoDTO obtenerPedidoPorId(Integer id);

    /**
     * Obtiene un pedido por su código único.
     */
    PedidoDTO obtenerPedidoPorCodigo(String codigo);

    /**
     * Obtiene todos los pedidos con paginación.
     */
    Page<PedidoDTO> obtenerTodosPedidos(Pageable pageable);

    /**
     * Obtiene pedidos filtrados por estado.
     */
    Page<PedidoDTO> obtenerPedidosPorEstado(String estado, Pageable pageable);

    /**
     * Obtiene pedidos de un cliente específico.
     */
    Page<PedidoDTO> obtenerPedidosPorCliente(Integer clienteId, Pageable pageable);

    /**
     * Obtiene pedidos por email del cliente (para clientes no registrados).
     */
    Page<PedidoDTO> obtenerPedidosPorEmail(String email, Pageable pageable);

    /**
     * Actualiza el estado de un pedido.
     * Estados: PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO
     */
    PedidoDTO actualizarEstadoPedido(Integer id, String nuevoEstado);

    /**
     * Cancela un pedido y restaura el stock de los productos.
     */
    PedidoDTO cancelarPedido(Integer id, String motivo);

    /**
     * Cuenta todos los pedidos.
     */
    long contarPedidos();
}

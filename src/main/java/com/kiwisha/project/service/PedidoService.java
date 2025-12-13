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
        * Valida stock y genera pedido + elementos.
        *
        * Importante: el stock NO se descuenta aquí. El descuento de stock debe ocurrir
        * cuando el pago quede confirmado (por ejemplo, Mercado Pago approved).
     */
    PedidoDTO crearPedido(String sessionId, CrearPedidoDTO crearPedidoDTO);

        /**
        * Confirma un pedido (pago aprobado) y descuenta el stock de los productos.
        * Debe ser idempotente para tolerar reintentos/polling.
        */
        PedidoDTO confirmarPedido(Integer id);

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
        * Cancela un pedido. Si el pedido ya había sido confirmado (stock descontado),
        * restaura el stock de los productos.
     */
    PedidoDTO cancelarPedido(Integer id, String motivo);

    /**
     * Cuenta todos los pedidos.
     */
    long contarPedidos();
}

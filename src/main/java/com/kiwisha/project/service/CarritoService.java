package com.kiwisha.project.service;

import com.kiwisha.project.dto.AgregarCarritoDTO;
import com.kiwisha.project.dto.CarritoDTO;

/**
 * Servicio para la gestión del carrito de compras basado en sesión.
 */
public interface CarritoService {

    /**
     * Agrega un producto al carrito o incrementa su cantidad si ya existe.
     */
    CarritoDTO agregarItem(String sessionId, AgregarCarritoDTO agregarCarritoDTO);

    /**
     * Actualiza la cantidad de un item en el carrito.
     */
    CarritoDTO actualizarCantidad(String sessionId, Integer productoId, Integer cantidad);

    /**
     * Elimina un item del carrito.
     */
    CarritoDTO eliminarItem(String sessionId, Integer productoId);

    /**
     * Obtiene el carrito completo con todos sus items y totales calculados.
     */
    CarritoDTO obtenerCarrito(String sessionId);

    /**
     * Limpia todos los items del carrito.
     */
    void limpiarCarrito(String sessionId);

    /**
     * Valida que todos los items del carrito tengan stock suficiente.
     */
    boolean validarStockCarrito(String sessionId);
}

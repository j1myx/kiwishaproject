package com.kiwisha.project.model;

/**
 * Enum que representa los posibles estados de un producto en el sistema.
 * Controla la visibilidad y disponibilidad del producto en la tienda.
 */
public enum EstadoProducto {
    /**
     * Producto en estado de borrador, no visible en la tienda.
     * Solo visible para administradores.
     */
    BORRADOR,
    
    /**
     * Producto publicado y visible en la tienda.
     * Disponible para compra por clientes.
     */
    PUBLICADO,
    
    /**
     * Producto archivado, no visible en la tienda.
     * Mantiene histórico pero no está activo.
     */
    ARCHIVADO
}

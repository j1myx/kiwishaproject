package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.AgregarCarritoDTO;
import com.kiwisha.project.dto.CarritoDTO;
import com.kiwisha.project.dto.CarritoItemDTO;
import com.kiwisha.project.exception.BusinessException;
import com.kiwisha.project.exception.ResourceNotFoundException;
import com.kiwisha.project.model.CarritoItem;
import com.kiwisha.project.model.Producto;
import com.kiwisha.project.repository.CarritoItemRepository;
import com.kiwisha.project.repository.ProductoRepository;
import com.kiwisha.project.service.CarritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de carrito de compras.
 * Gestiona items del carrito basados en sessionId.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CarritoServiceImpl implements CarritoService {

    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final ModelMapper modelMapper;

    @Override
    public CarritoDTO agregarItem(String sessionId, AgregarCarritoDTO agregarCarritoDTO) {
        log.info("Agregando producto {} al carrito de sesión: {}", 
                agregarCarritoDTO.getProductoId(), sessionId);

        // Validar producto
        Producto producto = productoRepository.findById(agregarCarritoDTO.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", 
                        agregarCarritoDTO.getProductoId()));

        if (!producto.estaDisponible()) {
            throw new BusinessException("El producto no está disponible");
        }

        // Verificar si el producto ya está en el carrito
        Optional<CarritoItem> itemExistente = carritoItemRepository
                .findBySesionIdAndProductoProductoId(sessionId, agregarCarritoDTO.getProductoId());

        CarritoItem carritoItem;
        if (itemExistente.isPresent()) {
            // Actualizar cantidad
            carritoItem = itemExistente.get();
            int nuevaCantidad = carritoItem.getCantidad() + agregarCarritoDTO.getCantidad();
            
            if (!producto.tieneSuficienteStock(nuevaCantidad)) {
                throw new BusinessException("Stock insuficiente. Disponible: " + producto.getCantidad());
            }
            
            carritoItem.setCantidad(nuevaCantidad);
            carritoItem.setActualizadoEn(LocalDateTime.now());
            log.info("Cantidad actualizada a {} para producto en carrito", nuevaCantidad);
        } else {
            // Crear nuevo item
            if (!producto.tieneSuficienteStock(agregarCarritoDTO.getCantidad())) {
                throw new BusinessException("Stock insuficiente. Disponible: " + producto.getCantidad());
            }

            carritoItem = CarritoItem.builder()
                    .sesionId(sessionId)
                    .producto(producto)
                    .cantidad(agregarCarritoDTO.getCantidad())
                    .precio(producto.getPrecio())
                    .build();
            log.info("Nuevo item creado en el carrito");
        }

        carritoItemRepository.save(carritoItem);
        return obtenerCarrito(sessionId);
    }

    @Override
    public CarritoDTO actualizarCantidad(String sessionId, Integer productoId, Integer cantidad) {
        log.info("Actualizando cantidad del producto {} a {} en carrito: {}", 
                productoId, cantidad, sessionId);

        if (cantidad < 1) {
            throw new BusinessException("La cantidad debe ser al menos 1");
        }

        CarritoItem carritoItem = carritoItemRepository
                .findBySesionIdAndProductoProductoId(sessionId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Item en carrito", "productoId", productoId));

        Producto producto = carritoItem.getProducto();
        if (!producto.tieneSuficienteStock(cantidad)) {
            throw new BusinessException("Stock insuficiente. Disponible: " + producto.getCantidad());
        }

        carritoItem.setCantidad(cantidad);
        carritoItem.setActualizadoEn(LocalDateTime.now());
        carritoItemRepository.save(carritoItem);

        return obtenerCarrito(sessionId);
    }

    @Override
    public CarritoDTO eliminarItem(String sessionId, Integer productoId) {
        log.info("Eliminando producto {} del carrito: {}", productoId, sessionId);

        CarritoItem carritoItem = carritoItemRepository
                .findBySesionIdAndProductoProductoId(sessionId, productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Item en carrito", "productoId", productoId));

        carritoItemRepository.delete(carritoItem);
        log.info("Item eliminado del carrito exitosamente");

        return obtenerCarrito(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public CarritoDTO obtenerCarrito(String sessionId) {
        log.debug("Obteniendo carrito para sesión: {}", sessionId);

        List<CarritoItem> items = carritoItemRepository.findBySesionId(sessionId);
        
        CarritoDTO carritoDTO = new CarritoDTO();
        carritoDTO.setItems(items.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList()));

        // Calcular totales
        calcularTotales(carritoDTO);

        return carritoDTO;
    }

    @Override
    public void limpiarCarrito(String sessionId) {
        log.info("Limpiando carrito para sesión: {}", sessionId);
        
        List<CarritoItem> items = carritoItemRepository.findBySesionId(sessionId);
        carritoItemRepository.deleteAll(items);
        
        log.info("Carrito limpiado exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validarStockCarrito(String sessionId) {
        log.debug("Validando stock del carrito: {}", sessionId);
        
        List<CarritoItem> items = carritoItemRepository.findBySesionId(sessionId);
        
        for (CarritoItem item : items) {
            Producto producto = item.getProducto();
            if (!producto.estaDisponible() || !producto.tieneSuficienteStock(item.getCantidad())) {
                log.warn("Stock insuficiente para producto: {} (disponible: {}, requerido: {})",
                        producto.getProductoId(), producto.getCantidad(), item.getCantidad());
                return false;
            }
        }
        
        return true;
    }

    /**
     * Calcula los totales del carrito (subtotal, descuentos, total).
     */
    private void calcularTotales(CarritoDTO carritoDTO) {
        BigDecimal subtotal = BigDecimal.ZERO;
        int cantidadTotalItems = 0;

        for (CarritoItemDTO item : carritoDTO.getItems()) {
            BigDecimal precioItem = item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            subtotal = subtotal.add(precioItem);
            cantidadTotalItems += item.getCantidad();
        }

        carritoDTO.setSubtotal(subtotal);
        carritoDTO.setCantidadItems(cantidadTotalItems);

        // Total = Subtotal - Descuento de cupón
        BigDecimal descuento = carritoDTO.getDescuento() != null ? 
                carritoDTO.getDescuento() : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(descuento);

        // Asegurar que el total no sea negativo
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        carritoDTO.setTotal(total);
    }

    /**
     * Convierte CarritoItem a CarritoItemDTO.
     */
    private CarritoItemDTO convertirADTO(CarritoItem carritoItem) {
        CarritoItemDTO dto = modelMapper.map(carritoItem, CarritoItemDTO.class);
        
        Producto producto = carritoItem.getProducto();
        dto.setProductoId(producto.getProductoId());
        dto.setProductoTitulo(producto.getTitulo());
        dto.setProductoImagen(producto.getImagen());
        dto.setPrecio(carritoItem.getPrecio());
        dto.setDisponible(producto.estaDisponible());
        dto.setStockDisponible(producto.getCantidad());
        
        // Calcular subtotal del item
        BigDecimal subtotalItem = carritoItem.getPrecio()
                .multiply(BigDecimal.valueOf(carritoItem.getCantidad()));
        dto.setSubtotal(subtotalItem);
        
        return dto;
    }
}

package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.CrearPedidoDTO;
import com.kiwisha.project.dto.PedidoDTO;
import com.kiwisha.project.dto.PedidoElementoDTO;
import com.kiwisha.project.exception.BusinessException;
import com.kiwisha.project.exception.ResourceNotFoundException;
import com.kiwisha.project.model.*;
import com.kiwisha.project.repository.*;
import com.kiwisha.project.service.CarritoService;
import com.kiwisha.project.service.PedidoService;
import com.kiwisha.project.util.SessionIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de pedidos.
 * Versión simplificada sin integración con couriers externos.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final MetodoEnvioRepository metodoEnvioRepository;
    private final CarritoService carritoService;
    private final ModelMapper modelMapper;

    @Override
    public PedidoDTO crearPedido(String sessionId, CrearPedidoDTO crearPedidoDTO) {
        log.info("Creando pedido para sesión: {}", sessionId);

        // 1. Obtener items del carrito
        List<CarritoItem> items = carritoItemRepository.findBySesionId(sessionId);
        if (items.isEmpty()) {
            throw new BusinessException("El carrito está vacío");
        }

        // 2. Validar stock de todos los productos
        if (!carritoService.validarStockCarrito(sessionId)) {
            throw new BusinessException("Algunos productos no tienen stock suficiente");
        }

        // 3. Obtener método de envío
        MetodoEnvio metodoEnvio = metodoEnvioRepository.findById(crearPedidoDTO.getMetodoEnvioId())
                .orElseThrow(() -> new ResourceNotFoundException("Método de envío", "id", 
                        crearPedidoDTO.getMetodoEnvioId()));

        // 4. Calcular subtotal
        BigDecimal subtotal = items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Calcular descuento (actualmente sin cupones - reservado para futuro)
        BigDecimal descuento = BigDecimal.ZERO;

        // 6. Calcular total
        BigDecimal costoEnvio = metodoEnvio.getCosto() != null ? metodoEnvio.getCosto() : BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(descuento).add(costoEnvio);

        // 7. Buscar cliente si está registrado
        Cliente cliente = null;
        if (crearPedidoDTO.getClienteId() != null) {
            cliente = clienteRepository.findById(crearPedidoDTO.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", 
                            crearPedidoDTO.getClienteId()));
        }

        // 8. Crear el pedido
        String codigoPedido = generarCodigoPedido();
        Pedido pedido = Pedido.builder()
                .codigo(codigoPedido)
                .cliente(cliente)
                .nombre(crearPedidoDTO.getNombre())
                .apellido(crearPedidoDTO.getApellido())
                .email(crearPedidoDTO.getEmail())
                .telefono(crearPedidoDTO.getTelefono())
                .direccion(crearPedidoDTO.getDireccion())
                .ciudad(crearPedidoDTO.getCiudad())
                .provincia(crearPedidoDTO.getProvincia())
                .codigoPostal(crearPedidoDTO.getCodigoPostal())
                .pais(crearPedidoDTO.getPais())
                .metodoEnvio(metodoEnvio)
                .subtotal(subtotal)
                .descuento(descuento)
                .costoEnvio(costoEnvio)
                .total(total)
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .notas(crearPedidoDTO.getNotas())
                .build();

        // 9. Crear elementos del pedido y reducir stock
        for (CarritoItem item : items) {
            Producto producto = item.getProducto();

            // Validar y reducir stock
            if (!producto.tieneSuficienteStock(item.getCantidad())) {
                throw new BusinessException("Stock insuficiente para el producto: " + producto.getTitulo());
            }
            producto.reducirStock(item.getCantidad());
            productoRepository.save(producto);

            // Crear elemento del pedido
            PedidoElemento elemento = PedidoElemento.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precio(item.getPrecio())
                    .descuento(producto.getPorcentajeDescuento() != null ? 
                        BigDecimal.valueOf(producto.getPorcentajeDescuento()) : BigDecimal.ZERO)
                    .build();

            pedido.agregarElemento(elemento);
        }

        // 10. Guardar el pedido
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        log.info("Pedido creado exitosamente con código: {}", codigoPedido);

        // 12. Limpiar el carrito
        carritoService.limpiarCarrito(sessionId);
        log.info("Carrito limpiado para sesión: {}", sessionId);

        return convertirADTO(pedidoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDTO obtenerPedidoPorId(Integer id) {
        log.debug("Obteniendo pedido por ID: {}", id);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));
        return convertirADTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoDTO obtenerPedidoPorCodigo(String codigo) {
        log.debug("Obteniendo pedido por código: {}", codigo);
        Pedido pedido = pedidoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "codigo", codigo));
        return convertirADTO(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoDTO> obtenerTodosPedidos(Pageable pageable) {
        log.debug("Obteniendo todos los pedidos con paginación: {}", pageable);
        return pedidoRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoDTO> obtenerPedidosPorEstado(String estado, Pageable pageable) {
        log.debug("Obteniendo pedidos por estado: {}", estado);
        try {
            Pedido.EstadoPedido estadoPedido = Pedido.EstadoPedido.valueOf(estado.toUpperCase());
            return pedidoRepository.findByEstado(estadoPedido, pageable)
                    .map(this::convertirADTO);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de pedido inválido: " + estado);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoDTO> obtenerPedidosPorCliente(Integer clienteId, Pageable pageable) {
        log.debug("Obteniendo pedidos del cliente ID: {}", clienteId);
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente", "id", clienteId);
        }
        return pedidoRepository.findByClienteClienteId(clienteId, pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PedidoDTO> obtenerPedidosPorEmail(String email, Pageable pageable) {
        log.debug("Obteniendo pedidos por email: {}", email);
        return pedidoRepository.findByEmail(email, pageable)
                .map(this::convertirADTO);
    }

    @Override
    public PedidoDTO actualizarEstadoPedido(Integer id, String nuevoEstado) {
        log.info("Actualizando estado del pedido {} a {}", id, nuevoEstado);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        try {
            Pedido.EstadoPedido estado = Pedido.EstadoPedido.valueOf(nuevoEstado.toUpperCase());
            pedido.setEstado(estado);
            Pedido pedidoActualizado = pedidoRepository.save(pedido);
            
            log.info("Estado del pedido actualizado exitosamente");
            return convertirADTO(pedidoActualizado);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de pedido inválido: " + nuevoEstado);
        }
    }

    @Override
    public PedidoDTO cancelarPedido(Integer id, String motivo) {
        log.info("Cancelando pedido ID: {} por motivo: {}", id, motivo);

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        if (!pedido.puedeCancelarse()) {
            throw new BusinessException("El pedido no puede ser cancelado en su estado actual: " + pedido.getEstado());
        }

        // Restaurar stock de los productos
        for (PedidoElemento elemento : pedido.getPedidoElementos()) {
            Producto producto = elemento.getProducto();
            producto.aumentarStock(elemento.getCantidad());
            productoRepository.save(producto);
            log.debug("Stock restaurado para producto ID: {}", producto.getProductoId());
        }

        // Actualizar estado del pedido
        pedido.setEstado(Pedido.EstadoPedido.CANCELADO);
        if (motivo != null) {
            String notasActuales = pedido.getNotas() != null ? pedido.getNotas() : "";
            pedido.setNotas(notasActuales + "\nMotivo de cancelación: " + motivo);
        }

        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        log.info("Pedido cancelado exitosamente");

        return convertirADTO(pedidoActualizado);
    }

    /**
     * Genera un código único para el pedido.
     */
    private String generarCodigoPedido() {
        String codigo;
        do {
            codigo = "PED-" + SessionIdGenerator.generateSessionId().substring(0, 12).toUpperCase();
        } while (pedidoRepository.findByCodigo(codigo).isPresent());
        return codigo;
    }

    /**
     * Convierte una entidad Pedido a PedidoDTO.
     */
    private PedidoDTO convertirADTO(Pedido pedido) {
        PedidoDTO dto = modelMapper.map(pedido, PedidoDTO.class);

        // Mapear cliente si existe
        if (pedido.getCliente() != null) {
            dto.setClienteId(pedido.getCliente().getClienteId());
        }

        // Mapear método de envío
        if (pedido.getMetodoEnvio() != null) {
            dto.setMetodoEnvioId(pedido.getMetodoEnvio().getMetodoEnvioId());
            dto.setMetodoEnvioNombre(pedido.getMetodoEnvio().getNombre());
        }

        // Mapear elementos del pedido
        List<PedidoElementoDTO> elementosDTO = pedido.getPedidoElementos().stream()
                .map(this::convertirElementoADTO)
                .collect(Collectors.toList());
        dto.setElementos(elementosDTO);

        return dto;
    }

    /**
     * Convierte PedidoElemento a PedidoElementoDTO.
     */
    private PedidoElementoDTO convertirElementoADTO(PedidoElemento elemento) {
        PedidoElementoDTO dto = modelMapper.map(elemento, PedidoElementoDTO.class);
        
        Producto producto = elemento.getProducto();
        dto.setProductoId(producto.getProductoId());
        dto.setProductoTitulo(producto.getTitulo());
        dto.setProductoImagen(producto.getImagen());
        
        // Calcular subtotal
        BigDecimal subtotal = elemento.getPrecio()
                .multiply(BigDecimal.valueOf(elemento.getCantidad()));
        dto.setSubtotal(subtotal);
        
        return dto;
    }
}

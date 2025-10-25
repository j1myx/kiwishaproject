package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.ClienteDTO;
import com.kiwisha.project.dto.CrearClienteDTO;
import com.kiwisha.project.exception.BusinessException;
import com.kiwisha.project.exception.ResourceNotFoundException;
import com.kiwisha.project.model.Cliente;
import com.kiwisha.project.repository.ClienteRepository;
import com.kiwisha.project.repository.PedidoRepository;
import com.kiwisha.project.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación del servicio de clientes.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDTO> obtenerTodosClientes(Pageable pageable) {
        log.debug("Obteniendo todos los clientes con paginación: {}", pageable);
        return clienteRepository.findAll(pageable)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO obtenerClientePorId(Integer id) {
        log.debug("Obteniendo cliente por ID: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return convertirADTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> buscarPorEmail(String email) {
        log.debug("Buscando cliente por email: {}", email);
        return clienteRepository.findByEmail(email)
                .map(this::convertirADTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> buscarPorTelefono(String telefono) {
        log.debug("Buscando cliente por teléfono: {}", telefono);
        return clienteRepository.findByTelefono(telefono)
                .map(this::convertirADTO);
    }

    @Override
    public ClienteDTO crearCliente(CrearClienteDTO crearClienteDTO) {
        log.info("Creando nuevo cliente: {}", crearClienteDTO.getEmail());

        // Validar que el email no exista
        if (clienteRepository.existsByEmail(crearClienteDTO.getEmail())) {
            throw new BusinessException("Ya existe un cliente con el email: " + crearClienteDTO.getEmail());
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(crearClienteDTO.getNombre());
        cliente.setEmail(crearClienteDTO.getEmail());
        cliente.setTelefono(crearClienteDTO.getTelefono());
        cliente.setDireccion(crearClienteDTO.getDireccion());

        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente creado exitosamente con ID: {}", clienteGuardado.getClienteId());

        return convertirADTO(clienteGuardado);
    }

    @Override
    public ClienteDTO actualizarCliente(Integer id, CrearClienteDTO actualizarClienteDTO) {
        log.info("Actualizando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Validar email único si cambió
        if (!cliente.getEmail().equals(actualizarClienteDTO.getEmail())) {
            if (clienteRepository.existsByEmail(actualizarClienteDTO.getEmail())) {
                throw new BusinessException("Ya existe un cliente con el email: " + 
                        actualizarClienteDTO.getEmail());
            }
        }

        cliente.setNombre(actualizarClienteDTO.getNombre());
        cliente.setEmail(actualizarClienteDTO.getEmail());
        cliente.setTelefono(actualizarClienteDTO.getTelefono());
        cliente.setDireccion(actualizarClienteDTO.getDireccion());

        Cliente clienteActualizado = clienteRepository.save(cliente);
        log.info("Cliente actualizado exitosamente ID: {}", id);

        return convertirADTO(clienteActualizado);
    }

    @Override
    public void eliminarCliente(Integer id) {
        log.info("Eliminando cliente ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Verificar que no tenga pedidos asociados
        long cantidadPedidos = pedidoRepository.countByClienteClienteId(id);
        if (cantidadPedidos > 0) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene " + 
                    cantidadPedidos + " pedidos asociados");
        }

        clienteRepository.delete(cliente);
        log.info("Cliente eliminado exitosamente ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPedidosPorCliente(Integer clienteId) {
        log.debug("Contando pedidos del cliente ID: {}", clienteId);
        
        if (!clienteRepository.existsById(clienteId)) {
            throw new ResourceNotFoundException("Cliente", "id", clienteId);
        }
        
        return pedidoRepository.countByClienteClienteId(clienteId);
    }

    /**
     * Convierte una entidad Cliente a ClienteDTO.
     */
    private ClienteDTO convertirADTO(Cliente cliente) {
        ClienteDTO dto = modelMapper.map(cliente, ClienteDTO.class);
        
        // Contar pedidos del cliente
        long cantidadPedidos = pedidoRepository.countByClienteClienteId(cliente.getClienteId());
        dto.setCantidadPedidos((int) cantidadPedidos);
        
        return dto;
    }
}

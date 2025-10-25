package com.kiwisha.project.service;

import com.kiwisha.project.dto.ClienteDTO;
import com.kiwisha.project.dto.CrearClienteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Servicio para la gestión de clientes.
 */
public interface ClienteService {

    /**
     * Obtiene todos los clientes con paginación.
     */
    Page<ClienteDTO> obtenerTodosClientes(Pageable pageable);

    /**
     * Obtiene un cliente por su ID.
     */
    ClienteDTO obtenerClientePorId(Integer id);

    /**
     * Busca un cliente por su email.
     */
    Optional<ClienteDTO> buscarPorEmail(String email);

    /**
     * Busca un cliente por su teléfono.
     */
    Optional<ClienteDTO> buscarPorTelefono(String telefono);

    /**
     * Crea un nuevo cliente.
     */
    ClienteDTO crearCliente(CrearClienteDTO crearClienteDTO);

    /**
     * Actualiza los datos de un cliente.
     */
    ClienteDTO actualizarCliente(Integer id, CrearClienteDTO actualizarClienteDTO);

    /**
     * Elimina un cliente.
     */
    void eliminarCliente(Integer id);

    /**
     * Verifica si un email ya está registrado.
     */
    boolean existeEmail(String email);

    /**
     * Obtiene el número de pedidos de un cliente.
     */
    long contarPedidosPorCliente(Integer clienteId);
}

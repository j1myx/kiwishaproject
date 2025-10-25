package com.kiwisha.project.repository;

import com.kiwisha.project.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Cliente.
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Busca un cliente por email
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Busca un cliente por teléfono
     */
    Optional<Cliente> findByTelefono(String telefono);

    /**
     * Verifica si existe un cliente con el email dado
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un cliente con el email (excluyendo un ID específico)
     */
    boolean existsByEmailAndClienteIdNot(String email, Integer clienteId);
}

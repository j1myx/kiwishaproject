package com.kiwisha.project.repository;

import com.kiwisha.project.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por nombre de usuario
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    /**
     * Busca un usuario por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario activo por nombre de usuario
     */
    Optional<Usuario> findByNombreUsuarioAndActivoTrue(String nombreUsuario);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado
     */
    boolean existsByNombreUsuario(String nombreUsuario);

    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);
}

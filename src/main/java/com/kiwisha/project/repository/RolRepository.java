package com.kiwisha.project.repository;

import com.kiwisha.project.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Rol
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    /**
     * Busca un rol por su nombre
     */
    Optional<Rol> findByNombre(String nombre);
}

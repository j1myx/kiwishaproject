package com.kiwisha.project.repository;

import com.kiwisha.project.model.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad RolUsuario
 */
@Repository
public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Integer> {
    
    /**
     * Busca los roles de un usuario específico
     */
    List<RolUsuario> findByUsuarioUsuarioId(Integer usuarioId);
}

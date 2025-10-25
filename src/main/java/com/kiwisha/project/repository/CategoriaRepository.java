package com.kiwisha.project.repository;

import com.kiwisha.project.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Categoria.
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    /**
     * Busca una categoría por su título
     */
    Optional<Categoria> findByTitulo(String titulo);

    /**
     * Obtiene todas las categorías ordenadas por título
     */
    List<Categoria> findAllByOrderByTituloAsc();

    /**
     * Verifica si existe una categoría con el título dado
     */
    boolean existsByTitulo(String titulo);
}

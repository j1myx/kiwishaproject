package com.kiwisha.project.repository;

import com.kiwisha.project.model.PaginaEtiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaginaEtiquetaRepository extends JpaRepository<PaginaEtiqueta, Integer> {
    @Query("""
            FROM PaginaEtiqueta pe
            INNER JOIN FETCH pe.etiqueta e
            WHERE pe.pagina.paginaId = :paginaId
            """)
    List<PaginaEtiqueta>  findByPaginaId(@Param("paginaId") Integer paginaId);
}

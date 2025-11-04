package com.kiwisha.project.repository;

import com.kiwisha.project.model.PaginaEtiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaginaEtiquetaRepository extends JpaRepository<PaginaEtiqueta, Integer> {
}

package com.kiwisha.project.repository;

import com.kiwisha.project.model.PaginaImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaginaImagenRepository extends JpaRepository<PaginaImagen, Integer> {
}

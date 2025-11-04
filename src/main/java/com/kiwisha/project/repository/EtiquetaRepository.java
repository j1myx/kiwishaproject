package com.kiwisha.project.repository;

import com.kiwisha.project.model.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Integer> {
    Etiqueta findByNombre(String nombre);
}

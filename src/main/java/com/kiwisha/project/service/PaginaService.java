package com.kiwisha.project.service;

import com.kiwisha.project.dto.PaginaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaginaService {
    Page<PaginaDTO> findAll(String titulo, Pageable pageable);
    PaginaDTO findByUrl(String url);
    PaginaDTO findById(Integer paginaId);
    List<PaginaDTO> getTops();
    void delete(Integer paginaId);
    List<PaginaDTO> findByEtiqueta(String etiqueta);
    List<PaginaDTO> findArticulos();
}

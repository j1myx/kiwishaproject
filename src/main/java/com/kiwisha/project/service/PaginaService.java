package com.kiwisha.project.service;

import com.kiwisha.project.dto.PaginaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaginaService {
    Page<PaginaDTO> findAll(Pageable pageable);
    PaginaDTO findByUrl(String url);
    List<PaginaDTO> getTops();
    void delete(Integer paginaId);
}

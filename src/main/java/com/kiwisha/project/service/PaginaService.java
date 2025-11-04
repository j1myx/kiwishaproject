package com.kiwisha.project.service;

import com.kiwisha.project.dto.PaginaDTO;

import java.util.List;

public interface PaginaService {
    List<PaginaDTO> findAll();
    PaginaDTO findByUrl(String url);
}

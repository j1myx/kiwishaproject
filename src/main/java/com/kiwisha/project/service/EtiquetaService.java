package com.kiwisha.project.service;

import com.kiwisha.project.dto.EtiquetaDTO;

import java.util.List;

public interface EtiquetaService {
    List<EtiquetaDTO> findAll();
    EtiquetaDTO findByNombre(String nombre);
}

package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.EtiquetaDTO;
import com.kiwisha.project.repository.EtiquetaRepository;
import com.kiwisha.project.service.EtiquetaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EtiquetaServiceImpl implements EtiquetaService {
    private final EtiquetaRepository etiquetaRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<EtiquetaDTO> findAll() {
        var etiquetas =  etiquetaRepository.findAll();

        return etiquetas.stream()
                .map(etiqueta -> modelMapper.map(etiqueta, EtiquetaDTO.class))
                .toList();
    }

    @Override
    public EtiquetaDTO findByNombre(String nombre) {
        var etiqueta = etiquetaRepository.findByNombre(nombre);

        return modelMapper.map(etiqueta, EtiquetaDTO.class);
    }
}

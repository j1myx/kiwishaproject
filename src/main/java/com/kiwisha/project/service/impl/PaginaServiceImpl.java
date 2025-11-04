package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.PaginaDTO;
import com.kiwisha.project.model.Pagina;
import com.kiwisha.project.repository.PaginaRepository;
import com.kiwisha.project.service.PaginaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaginaServiceImpl implements PaginaService {
    private final PaginaRepository paginaRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PaginaDTO> findAll() {
        var paginas =  paginaRepository.findAll();

        return paginas.stream()
                .map(pagina -> modelMapper.map(pagina, PaginaDTO.class))
                .toList();
    }

    @Override
    public PaginaDTO findByUrl(String url) {
        var pagina =  paginaRepository.findByUrl(url);

        return modelMapper.map(pagina, PaginaDTO.class);
    }
}

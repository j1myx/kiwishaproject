package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.PaginaDTO;
import com.kiwisha.project.model.Pagina;
import com.kiwisha.project.repository.PaginaRepository;
import com.kiwisha.project.service.PaginaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaginaServiceImpl implements PaginaService {
    private final PaginaRepository paginaRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<PaginaDTO> findAll(Pageable pageable) {
        Page<Pagina> page =  paginaRepository.findAll(pageable);

        return page.map(pagina -> modelMapper.map(pagina, PaginaDTO.class));
    }

    @Override
    public PaginaDTO findByUrl(String url) {
        var pagina =  paginaRepository.findByUrl(url);

        return modelMapper.map(pagina, PaginaDTO.class);
    }
}

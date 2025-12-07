package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.EtiquetaDTO;
import com.kiwisha.project.dto.PaginaDTO;
import com.kiwisha.project.dto.PaginaImagenDTO;
import com.kiwisha.project.model.Pagina;
import com.kiwisha.project.repository.PaginaRepository;
import com.kiwisha.project.service.PaginaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaginaServiceImpl implements PaginaService {
    private final PaginaRepository paginaRepository;

    @Override
    public Page<PaginaDTO> findAll(Pageable pageable) {
        Page<Pagina> page =  paginaRepository.findAll(pageable);

        return page.map(pagina -> PaginaDTO.builder()
                .paginaId(pagina.getPaginaId())
                .titulo(pagina.getTitulo())
                .url(pagina.getUrl())
                .resumen(pagina.getResumen())
                .contenido(pagina.getContenido())
                .tipo(pagina.getTipo())
                .publicado(pagina.getPublicado())
                .creadoPor(pagina.getCreadoPor())
                .creadoEn(pagina.getCreadoEn())
                .etiquetas(pagina.getEtiquetas().stream().map(etiqueta -> EtiquetaDTO.builder()
                        .etiquetaId(etiqueta.getEtiquetaId())
                        .nombre(etiqueta.getNombre())
                        .build()).toList())
                .build());
    }

    @Override
    public PaginaDTO findByUrl(String url) {
        var pagina =  paginaRepository.findByUrl(url);

        return PaginaDTO.builder()
                .titulo(pagina.getTitulo())
                .resumen(pagina.getResumen())
                .contenido(pagina.getContenido())
                .url(pagina.getUrl())
                .paginaImagenes(pagina.getPaginaImagenes()
                        .stream()
                        .map(imagen -> PaginaImagenDTO.builder()
                                .ruta(imagen.getRuta())
                                .build())
                        .toList())
                .build();
    }

    @Override
    public List<PaginaDTO> getTops() {
        return paginaRepository.getTops()
                .stream()
                .map(pagina -> PaginaDTO.builder()
                        .titulo(pagina.getTitulo())
                        .resumen(pagina.getResumen())
                        .url(pagina.getUrl())
                        .paginaImagenes(pagina.getPaginaImagenes()
                                .stream()
                                .map(imagen -> PaginaImagenDTO.builder()
                                        .ruta(imagen.getRuta())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }

    @Override
    public void delete(Integer paginaId) {
        paginaRepository.deleteById(paginaId);
    }
}

package com.kiwisha.project.service.impl;

import com.kiwisha.project.dto.EtiquetaDTO;
import com.kiwisha.project.dto.PaginaDTO;
import com.kiwisha.project.dto.PaginaImagenDTO;
import com.kiwisha.project.model.Pagina;
import com.kiwisha.project.model.PaginaEtiqueta;
import com.kiwisha.project.repository.PaginaEtiquetaRepository;
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
    private final PaginaEtiquetaRepository paginaEtiquetaRepository;

    @Override
    public Page<PaginaDTO> findAll(String titulo, Pageable pageable) {
        Page<Pagina> page =  paginaRepository.findAll(titulo, pageable);

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
        var paginaEtiquetas = paginaEtiquetaRepository.findByPaginaId(pagina.getPaginaId());

        return PaginaDTO.builder()
                .paginaId(pagina.getPaginaId())
                .titulo(pagina.getTitulo())
                .resumen(pagina.getResumen())
                .contenido(pagina.getContenido())
                .tipo(pagina.getTipo())
                .url(pagina.getUrl())
                .paginaImagenes(pagina.getPaginaImagenes()
                        .stream()
                        .map(imagen -> PaginaImagenDTO.builder()
                                .paginaImagenId(imagen.getPaginaImagenId())
                                .nombre(imagen.getNombre())
                                .ruta(imagen.getRuta())
                                .build())
                        .toList())
                .etiquetas(paginaEtiquetas.stream()
                        .map(PaginaEtiqueta::getEtiqueta)
                        .map(e -> EtiquetaDTO.builder()
                                .etiquetaId(e.getEtiquetaId())
                                .nombre(e.getNombre())
                                .build())
                        .toList())
                .publicado(pagina.getPublicado())
                .build();
    }

    @Override
    public PaginaDTO findById(Integer paginaId) {
        var pagina =  paginaRepository.findByPaginaId(paginaId);
        var paginaEtiquetas = paginaEtiquetaRepository.findByPaginaId(paginaId);

        return PaginaDTO.builder()
                .paginaId(paginaId)
                .titulo(pagina.getTitulo())
                .resumen(pagina.getResumen())
                .contenido(pagina.getContenido())
                .tipo(pagina.getTipo())
                .url(pagina.getUrl())
                .paginaImagenes(pagina.getPaginaImagenes()
                        .stream()
                        .map(imagen -> PaginaImagenDTO.builder()
                                .paginaImagenId(imagen.getPaginaImagenId())
                                .nombre(imagen.getNombre())
                                .ruta(imagen.getRuta())
                                .build())
                        .toList())
                .etiquetas(paginaEtiquetas.stream()
                        .map(PaginaEtiqueta::getEtiqueta)
                        .map(e -> EtiquetaDTO.builder()
                                .etiquetaId(e.getEtiquetaId())
                                .nombre(e.getNombre())
                                .build())
                        .toList())
                .publicado(pagina.getPublicado())
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

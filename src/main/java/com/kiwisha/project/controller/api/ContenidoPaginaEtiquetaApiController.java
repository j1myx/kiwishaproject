package com.kiwisha.project.controller.api;

import com.kiwisha.project.model.PaginaEtiqueta;
import com.kiwisha.project.repository.PaginaEtiquetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenido/pagina-etiquetas")
@RequiredArgsConstructor
public class ContenidoPaginaEtiquetaApiController {
    private final PaginaEtiquetaRepository paginaEtiquetaRepository;

    @PostMapping
    public List<PaginaEtiqueta> saveAll(@RequestBody List<PaginaEtiqueta> paginaEtiquetas) {
        return paginaEtiquetaRepository.saveAll(paginaEtiquetas);
    }

    @DeleteMapping
    public void deleteAllById(@RequestBody List<Integer> paginaEtiquetaIds) {
        paginaEtiquetaRepository.deleteAllById(paginaEtiquetaIds);
    }
}

package com.kiwisha.project.controller.api;

import com.kiwisha.project.model.PaginaImagen;
import com.kiwisha.project.repository.PaginaImagenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/contenido/pagina-imagenes")
@RequiredArgsConstructor
public class ContenidoPaginaImagenApiController {
    private final PaginaImagenRepository paginaImagenRepository;

    @PostMapping
    public List<PaginaImagen> saveAll(@RequestBody List<PaginaImagen> paginaImagenes) {
        return paginaImagenRepository.saveAll(paginaImagenes);
    }
}

package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.PaginaDTO;
import com.kiwisha.project.model.Pagina;
import com.kiwisha.project.repository.PaginaRepository;
import com.kiwisha.project.service.PaginaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenido/paginas")
@RequiredArgsConstructor
public class ContenidoPaginaApiController {
    private final PaginaService paginaService;
    private final PaginaRepository paginaRepository;

    @PostMapping
    public Pagina save(@RequestBody Pagina pagina) {
        return paginaRepository.save(pagina);
    }

    @GetMapping
    public List<PaginaDTO> findAll() {
        return paginaService.findAll();
    }

    @GetMapping("/{url}")
    public PaginaDTO findByUrl(@PathVariable String url) {
        return paginaService.findByUrl(url);
    }
}

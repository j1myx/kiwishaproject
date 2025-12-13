package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.PaginaDTO;
import com.kiwisha.project.model.Pagina;
import com.kiwisha.project.repository.PaginaRepository;
import com.kiwisha.project.service.PaginaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/{paginaId}")
    public void update(@PathVariable Integer paginaId, @RequestBody Pagina pagina) {
        pagina.setPaginaId(paginaId);
        paginaRepository.save(pagina);
    }

    @GetMapping
    public Page<PaginaDTO> findAll(Pageable pageable, @RequestParam(name = "titulo") String titulo) {
        return paginaService.findAll(titulo, pageable);
    }

    @DeleteMapping("/{paginaId}")
    public void delete(@PathVariable Integer paginaId) {
        paginaService.delete(paginaId);
    }

    @GetMapping("/{url}")
    public PaginaDTO findByUrl(@PathVariable String url) {
        return paginaService.findByUrl(url);
    }

    @GetMapping("/one/{paginaId}")
    public PaginaDTO findOneByPaginaId(@PathVariable Integer paginaId) {
        return paginaService.findById(paginaId);
    }
}

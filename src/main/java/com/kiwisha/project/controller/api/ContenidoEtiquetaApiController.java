package com.kiwisha.project.controller.api;

import com.kiwisha.project.dto.EtiquetaDTO;
import com.kiwisha.project.model.Etiqueta;
import com.kiwisha.project.repository.EtiquetaRepository;
import com.kiwisha.project.service.EtiquetaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenido/etiquetas")
@RequiredArgsConstructor
public class ContenidoEtiquetaApiController {
    private final EtiquetaService etiquetaService;
    private final EtiquetaRepository etiquetaRepository;

    @GetMapping
    public List<EtiquetaDTO> findAll() {
        return etiquetaService.findAll();
    }

    @GetMapping("/{nombre}")
    public EtiquetaDTO findByNombre(@PathVariable String nombre) {
        return etiquetaService.findByNombre(nombre);
    }

    @PostMapping
    public Etiqueta save(@RequestBody Etiqueta etiqueta) {
        return etiquetaRepository.save(etiqueta);
    }
}

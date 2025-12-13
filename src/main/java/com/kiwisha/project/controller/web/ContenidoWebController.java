package com.kiwisha.project.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class ContenidoWebController {
    @GetMapping({"/admin/contenidos"})
    public String paginas() {
        return "/admin/contenido/paginas";
    }

    @GetMapping({"/admin/contenidos/nuevo"})
    public String nuevaPagina() {
        return "/admin/contenido/nueva-pagina";
    }

    @GetMapping({"/admin/contenidos/editar/{paginaId}"})
    public String editarPagina(@PathVariable Integer paginaId) {
        return "/admin/contenido/editar-pagina";
    }
}

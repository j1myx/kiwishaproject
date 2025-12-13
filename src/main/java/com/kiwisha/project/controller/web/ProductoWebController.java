package com.kiwisha.project.controller.web;

import com.kiwisha.project.dto.ProductoDTO;
import com.kiwisha.project.service.CategoriaService;
import com.kiwisha.project.service.PaginaService;
import com.kiwisha.project.service.ProductoImagenService;
import com.kiwisha.project.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controlador web para vistas públicas de productos.
 * Maneja: Home, Catálogo (PLP), Detalle de Producto (PDP).
 */
@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class ProductoWebController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final PaginaService paginaService;
    private final ProductoImagenService productoImagenService;

    /**
     * Página de inicio (Home/Landing)
     * GET /
     */
    @GetMapping({"/", "/inicio"})
    public String home(Model model) {
        log.debug("Accediendo a página de inicio");
        
        try {
            // Productos destacados
            var productosDestacados = productoService.obtenerProductosDestacados();
            
            // Productos nuevos
            var productosNuevos = productoService.obtenerProductosNuevos();
            
            model.addAttribute("productosDestacados", productosDestacados);
            model.addAttribute("productosNuevos", productosNuevos);
            model.addAttribute("paginaActual", "inicio");
            model.addAttribute("paginasTop", paginaService.getTops());
        } catch (Exception e) {
            log.error("Error al cargar productos en home", e);
            // Continuar con listas vacías
            model.addAttribute("productosDestacados", List.of());
            model.addAttribute("productosNuevos", List.of());
            model.addAttribute("paginaActual", "inicio");
            model.addAttribute("paginasTop", List.of());
        }
        
        return "public/home";
    }

    @GetMapping("pagina/{url}")
    public String getPage(@PathVariable String url, Model model) {
        model.addAttribute("pagina", paginaService.findByUrl(url));
        return "public/page";
    }

    /**
     * Catálogo de productos (Product Listing Page - PLP)
     * GET /catalogo
     */
    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String ordenar,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamanio,
            Model model) {
        
        log.debug("Accediendo a catálogo - página: {}, categoría: {}, búsqueda: {}", 
                  pagina, categoriaId, busqueda);
        
        // Configurar ordenamiento
        Sort sort = Sort.by(Sort.Direction.DESC, "creadoEn"); // Por defecto: más recientes
        if ("precio_asc".equals(ordenar)) {
            sort = Sort.by(Sort.Direction.ASC, "precio");
        } else if ("precio_desc".equals(ordenar)) {
            sort = Sort.by(Sort.Direction.DESC, "precio");
        } else if ("nombre".equals(ordenar)) {
            sort = Sort.by(Sort.Direction.ASC, "titulo");
        }
        
        Pageable pageable = PageRequest.of(pagina, tamanio, sort);
        Page<ProductoDTO> productos;
        
        // Filtrar según parámetros
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            productos = productoService.buscarProductos(busqueda, pageable);
        } else if (categoriaId != null) {
            productos = productoService.obtenerProductosPorCategoria(categoriaId, pageable);
        } else {
            productos = productoService.obtenerProductosPublicados(pageable);
        }
        
        // Obtener categorías para el sidebar
        var categorias = categoriaService.obtenerTodasCategoriasLista();
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoriaSeleccionada", categoriaId);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("ordenar", ordenar);
        model.addAttribute("paginaActual", "catalogo");
        
        return "public/catalogo";
    }

    /**
     * Detalle de producto (Product Detail Page - PDP)
     * GET /producto/{slug}
     */
    @GetMapping("/producto/{slug}")
    public String detalleProducto(@PathVariable String slug, Model model) {
        log.debug("Accediendo a detalle de producto: {}", slug);
        
        var producto = productoService.obtenerProductoPorSlug(slug);

        var imagenesAdicionales = productoImagenService.listarRutasPorProductoId(producto.getProductoId());
        
        // Productos relacionados de la misma categoría (todos, excluyendo el actual)
        var productosRelacionados = productoService.obtenerProductosPorCategoria(
            producto.getCategoriaId(),
            PageRequest.of(0, 1000)
        ).getContent().stream()
         .filter(p -> !p.getProductoId().equals(producto.getProductoId()))
         .toList();
        
        model.addAttribute("producto", producto);
        model.addAttribute("imagenesAdicionales", imagenesAdicionales);
        model.addAttribute("productosRelacionados", productosRelacionados);
        model.addAttribute("paginaActual", "producto");
        
        return "public/producto-detalle";
    }

    /**
     * Búsqueda de productos (AJAX)
     * GET /buscar
     */
    @GetMapping("/buscar")
    public String buscar(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        
        log.debug("Búsqueda: {}", q);
        
        Pageable pageable = PageRequest.of(pagina, 12);
        var productos = productoService.buscarProductos(q, pageable);
        
        model.addAttribute("productos", productos);
        model.addAttribute("busqueda", q);
        model.addAttribute("paginaActual", "busqueda");
        
        return "public/busqueda-resultados";
    }
}

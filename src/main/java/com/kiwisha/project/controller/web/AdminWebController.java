package com.kiwisha.project.controller.web;

import com.kiwisha.project.dto.ActualizarProductoDTO;
import com.kiwisha.project.dto.CrearProductoDTO;
import com.kiwisha.project.model.EstadoProducto;
import com.kiwisha.project.service.CategoriaService;
import com.kiwisha.project.service.PedidoService;
import com.kiwisha.project.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador web para el panel de administración.
 * Requiere autenticación y rol ADMIN.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Slf4j
public class AdminWebController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final PedidoService pedidoService;

    /**
     * Dashboard principal del administrador
     * GET /admin/dashboard
     */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        log.debug("Accediendo al dashboard de administración");
        
        // KPIs básicos
        var kpis = calcularKPIs();
        
        model.addAttribute("kpis", kpis);
        model.addAttribute("paginaActual", "dashboard");
        
        return "admin/dashboard";
    }

    // ============================================
    // GESTIÓN DE PRODUCTOS
    // ============================================

    /**
     * Lista de productos (tabla de gestión)
     * GET /admin/productos
     */
    @GetMapping("/productos")
    public String listarProductos(
            @RequestParam(required = false) EstadoProducto estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            Model model) {
        
        log.debug("Listando productos en admin - página: {}, estado: {}", pagina, estado);
        
        var sort = Sort.by(Sort.Direction.DESC, "creadoEn");
        var pageable = PageRequest.of(pagina, tamanio, sort);
        
        var productos = productoService.obtenerTodosLosProductos(pageable);
        var categorias = categoriaService.obtenerTodasCategoriasLista();
        
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("estadoFiltro", estado);
        model.addAttribute("estadosProducto", EstadoProducto.values());
        model.addAttribute("paginaActual", "productos");
        
        return "admin/productos/lista";
    }

    /**
     * Formulario para crear nuevo producto
     * GET /admin/productos/nuevo
     */
    @GetMapping("/productos/nuevo")
    public String mostrarFormularioCrear(Model model) {
        log.debug("Mostrando formulario de creación de producto");
        
        if (!model.containsAttribute("producto")) {
            model.addAttribute("producto", new CrearProductoDTO());
        }
        
        var categorias = categoriaService.obtenerTodasCategoriasLista();
        model.addAttribute("categorias", categorias);
        model.addAttribute("estadosProducto", EstadoProducto.values());
        model.addAttribute("paginaActual", "productos");
        
        return "admin/productos/formulario";
    }

    /**
     * Procesar creación de producto
     * POST /admin/productos
     */
    @PostMapping("/productos")
    public String crearProducto(
            @Valid @ModelAttribute("producto") CrearProductoDTO producto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Creando nuevo producto: {}", producto.getTitulo());
        
        if (result.hasErrors()) {
            var categorias = categoriaService.obtenerTodasCategoriasLista();
            model.addAttribute("categorias", categorias);
            model.addAttribute("estadosProducto", EstadoProducto.values());
            return "admin/productos/formulario";
        }
        
        try {
            var productoCreado = productoService.crearProducto(producto);
            redirectAttributes.addFlashAttribute("success", 
                "Producto creado exitosamente");
            return "redirect:/admin/productos/" + productoCreado.getProductoId() + "/editar";
        } catch (Exception e) {
            log.error("Error al crear producto", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al crear el producto");
            return "redirect:/admin/productos/nuevo";
        }
    }

    /**
     * Formulario para editar producto existente
     * GET /admin/productos/{id}/editar
     */
    @GetMapping("/productos/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {
        log.debug("Mostrando formulario de edición para producto: {}", id);
        
        try {
            var producto = productoService.obtenerProductoPorId(id);
            var categorias = categoriaService.obtenerTodasCategoriasLista();
            
            // Convertir ProductoDTO a ActualizarProductoDTO para el formulario
            var actualizarDTO = convertirAActualizarDTO(producto);
            
            model.addAttribute("producto", actualizarDTO);
            model.addAttribute("productoId", id);
            model.addAttribute("categorias", categorias);
            model.addAttribute("estadosProducto", EstadoProducto.values());
            model.addAttribute("paginaActual", "productos");
            
            return "admin/productos/formulario";
            
        } catch (Exception e) {
            log.error("Error al cargar producto para editar", e);
            return "redirect:/admin/productos";
        }
    }

    /**
     * Procesar actualización de producto
     * POST /admin/productos/{id}
     */
    @PostMapping("/productos/{id}")
    public String actualizarProducto(
            @PathVariable Integer id,
            @Valid @ModelAttribute("producto") ActualizarProductoDTO producto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Actualizando producto: {}", id);
        
        if (result.hasErrors()) {
            var categorias = categoriaService.obtenerTodasCategoriasLista();
            model.addAttribute("categorias", categorias);
            model.addAttribute("estadosProducto", EstadoProducto.values());
            model.addAttribute("productoId", id);
            return "admin/productos/formulario";
        }
        
        try {
            productoService.actualizarProducto(id, producto);
            redirectAttributes.addFlashAttribute("success", 
                "Producto actualizado exitosamente");
            return "redirect:/admin/productos";
        } catch (Exception e) {
            log.error("Error al actualizar producto", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar el producto");
            return "redirect:/admin/productos/" + id + "/editar";
        }
    }

    /**
     * Eliminar producto (soft delete)
     * POST /admin/productos/{id}/eliminar
     */
    @PostMapping("/productos/{id}/eliminar")
    public String eliminarProducto(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Eliminando producto: {}", id);
        
        try {
            productoService.eliminarProducto(id);
            redirectAttributes.addFlashAttribute("success", 
                "Producto eliminado exitosamente");
        } catch (Exception e) {
            log.error("Error al eliminar producto", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar el producto");
        }
        
        return "redirect:/admin/productos";
    }

    // ============================================
    // GESTIÓN DE PEDIDOS
    // ============================================

    /**
     * Lista de pedidos
     * GET /admin/pedidos
     */
    @GetMapping("/pedidos")
    public String listarPedidos(
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            Model model) {
        
        log.debug("Listando pedidos - estado: {}", estado);
        
        var sort = Sort.by(Sort.Direction.DESC, "creadoEn");
        var pageable = PageRequest.of(pagina, tamanio, sort);
        
        var pedidos = estado != null 
            ? pedidoService.obtenerPedidosPorEstado(estado, pageable)
            : pedidoService.obtenerTodosPedidos(pageable);
        
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("estadoFiltro", estado);
        model.addAttribute("paginaActual", "pedidos");
        
        return "admin/pedidos/lista";
    }

    /**
     * Detalle de pedido
     * GET /admin/pedidos/{id}
     */
    @GetMapping("/pedidos/{id}")
    public String verDetallePedido(@PathVariable Integer id, Model model) {
        log.debug("Viendo detalle del pedido: {}", id);
        
        try {
            var pedido = pedidoService.obtenerPedidoPorId(id);
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("paginaActual", "pedidos");
            
            return "admin/pedidos/detalle";
            
        } catch (Exception e) {
            log.error("Error al cargar pedido", e);
            return "redirect:/admin/pedidos";
        }
    }

    /**
     * Actualizar estado de pedido
     * POST /admin/pedidos/{id}/estado
     */
    @PostMapping("/pedidos/{id}/estado")
    public String actualizarEstadoPedido(
            @PathVariable Integer id,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Actualizando estado del pedido {} a {}", id, estado);
        
        try {
            pedidoService.actualizarEstadoPedido(id, estado);
            redirectAttributes.addFlashAttribute("success", 
                "Estado actualizado exitosamente");
        } catch (Exception e) {
            log.error("Error al actualizar estado", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar el estado");
        }
        
        return "redirect:/admin/pedidos/" + id;
    }

    // ============================================
    // MÉTODOS PRIVADOS
    // ============================================

    private Map<String, Object> calcularKPIs() {
        var kpis = new HashMap<String, Object>();
        
        try {
            // Total de productos
            var todosProductos = productoService.obtenerTodosLosProductos(
                PageRequest.of(0, 1)
            );
            kpis.put("totalProductos", todosProductos.getTotalElements());
            
            // Productos publicados
            var publicados = productoService.obtenerProductosPublicados(
                PageRequest.of(0, 1)
            );
            kpis.put("productosPublicados", publicados.getTotalElements());
            
            // Productos con stock bajo
            var stockBajo = productoService.obtenerProductosStockBajo(5);
            kpis.put("productosStockBajo", stockBajo.size());
            
            // Total de pedidos (últimos 30 días - simplificado)
            var pedidos = pedidoService.obtenerTodosPedidos(PageRequest.of(0, 1));
            kpis.put("totalPedidos", pedidos.getTotalElements());
            
        } catch (Exception e) {
            log.error("Error al calcular KPIs", e);
            kpis.put("totalProductos", 0);
            kpis.put("productosPublicados", 0);
            kpis.put("productosStockBajo", 0);
            kpis.put("totalPedidos", 0);
        }
        
        return kpis;
    }

    private ActualizarProductoDTO convertirAActualizarDTO(
            com.kiwisha.project.dto.ProductoDTO productoDTO) {
        
        var dto = new ActualizarProductoDTO();
        dto.setTitulo(productoDTO.getTitulo());
        dto.setDescripcion(productoDTO.getDescripcion());
        dto.setPrecio(productoDTO.getPrecio());
        dto.setCantidad(productoDTO.getCantidad());
        dto.setCategoriaId(productoDTO.getCategoriaId());
        dto.setSku(productoDTO.getSku());
        dto.setEstado(productoDTO.getEstado());
        
        return dto;
    }
}

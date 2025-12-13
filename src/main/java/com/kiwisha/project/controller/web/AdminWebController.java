package com.kiwisha.project.controller.web;

import com.kiwisha.project.dto.ActualizarProductoDTO;
import com.kiwisha.project.dto.CrearProductoDTO;
import com.kiwisha.project.model.EstadoProducto;
import com.kiwisha.project.service.CategoriaService;
import com.kiwisha.project.service.ImagenProductoService;
import com.kiwisha.project.service.PedidoService;
import com.kiwisha.project.service.ProductoImagenService;
import com.kiwisha.project.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
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

    private static final String DEFAULT_PRODUCT_IMAGE_PATH = "defaults/producto-default.svg";

    @Value("${app.powerbi.report-url:}")
    private String powerBiReportUrl;

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final PedidoService pedidoService;
    private final ImagenProductoService imagenProductoService;
    private final ProductoImagenService productoImagenService;

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

    /**
     * Reportes (Power BI)
     * GET /admin/reportes
     */
    @GetMapping("/reportes")
    public String reportes(RedirectAttributes redirectAttributes) {
        if (powerBiReportUrl == null || powerBiReportUrl.isBlank()) {
            redirectAttributes.addFlashAttribute("error",
                    "Power BI no está configurado. Define 'app.powerbi.report-url' en application.properties.");
            return "redirect:/admin/dashboard";
        }

        var url = powerBiReportUrl.trim();
        if (!url.startsWith("https://")) {
            redirectAttributes.addFlashAttribute("error",
                    "URL de Power BI inválida. Debe iniciar con https://");
            return "redirect:/admin/dashboard";
        }

        return "redirect:" + url;
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
            @RequestParam(name = "accion", required = false) String accion,
            @RequestParam(name = "imagenFile", required = false) MultipartFile imagenFile,
            @RequestParam(name = "imagenesFiles", required = false) List<MultipartFile> imagenesFiles,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Creando nuevo producto: {} con acción: {}", producto.getTitulo(), accion);
        
        if (result.hasErrors()) {
            var categorias = categoriaService.obtenerTodasCategoriasLista();
            model.addAttribute("categorias", categorias);
            model.addAttribute("estadosProducto", EstadoProducto.values());
            return "admin/productos/formulario";
        }
        
        try {
            if (imagenFile != null && !imagenFile.isEmpty()) {
                producto.setImagen(imagenProductoService.guardarImagenPrincipal(imagenFile));
            } else if (producto.getImagen() == null || producto.getImagen().isBlank()) {
                producto.setImagen(DEFAULT_PRODUCT_IMAGE_PATH);
            }

            // Establecer el estado según el botón presionado
            if (accion != null) {
                producto.setEstado(EstadoProducto.valueOf(accion));
            } else {
                producto.setEstado(EstadoProducto.BORRADOR); // Por defecto
            }

            var creado = productoService.crearProducto(producto);

            if (imagenesFiles != null && !imagenesFiles.isEmpty()) {
                productoImagenService.agregarImagenes(creado.getProductoId(), imagenesFiles);
            }

            redirectAttributes.addFlashAttribute("success", 
                "Producto creado exitosamente");
            return "redirect:/admin/productos";
        } catch (Exception e) {
            log.error("Error al crear producto", e);
            var categorias = categoriaService.obtenerTodasCategoriasLista();
            model.addAttribute("categorias", categorias);
            model.addAttribute("estadosProducto", EstadoProducto.values());
            model.addAttribute("error", "Error al crear el producto: " + e.getMessage());
            return "admin/productos/formulario";
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
            model.addAttribute("imagenesAdicionalesExistentes", productoImagenService.listarImagenesPorProductoId(id));
            model.addAttribute("categorias", categorias);
            model.addAttribute("estadosProducto", EstadoProducto.values());
            model.addAttribute("paginaActual", "productos");
            
            return "admin/productos/formulario";
            
        } catch (Exception e) {
            log.error("Error al cargar producto para editar", e);
            return "redirect:/admin/productos";
        }
    }

    // Nota: la eliminación de imágenes adicionales se procesa al guardar/publicar
    // (para que el usuario pueda cancelar sin perder imágenes existentes).

    /**
     * Procesar actualización de producto
     * POST /admin/productos/{id}
     */
    @PostMapping("/productos/{id}")
    public String actualizarProducto(
            @PathVariable Integer id,
            @Valid @ModelAttribute("producto") ActualizarProductoDTO producto,
            BindingResult result,
            @RequestParam(name = "accion", required = false) String accion,
            @RequestParam(name = "imagenFile", required = false) MultipartFile imagenFile,
            @RequestParam(name = "imagenesFiles", required = false) List<MultipartFile> imagenesFiles,
            @RequestParam(name = "imagenesEliminarIds", required = false) List<Integer> imagenesEliminarIds,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Actualizando producto: {} con acción: {}", id, accion);
        
        if (result.hasErrors()) {
            var categorias = categoriaService.obtenerTodasCategoriasLista();
            model.addAttribute("categorias", categorias);
            model.addAttribute("estadosProducto", EstadoProducto.values());
            model.addAttribute("productoId", id);
            model.addAttribute("imagenesAdicionalesExistentes", productoImagenService.listarImagenesPorProductoId(id));
            return "admin/productos/formulario";
        }
        
        try {
            if (imagenFile != null && !imagenFile.isEmpty()) {
                producto.setImagen(imagenProductoService.guardarImagenPrincipal(imagenFile));
            } else {
                // Si el producto no tiene imagen aún, asignar una por defecto
                var actual = productoService.obtenerProductoPorId(id);
                if (actual.getImagen() == null || actual.getImagen().isBlank()) {
                    producto.setImagen(DEFAULT_PRODUCT_IMAGE_PATH);
                }
            }

            // Establecer el estado según el botón presionado
            if (accion != null) {
                producto.setEstado(EstadoProducto.valueOf(accion));
            }
            
            productoService.actualizarProducto(id, producto);

            if (imagenesEliminarIds != null && !imagenesEliminarIds.isEmpty()) {
                for (Integer imagenId : imagenesEliminarIds) {
                    if (imagenId == null) continue;
                    productoImagenService.eliminarImagen(id, imagenId);
                }
            }

            if (imagenesFiles != null && !imagenesFiles.isEmpty()) {
                productoImagenService.agregarImagenes(id, imagenesFiles);
            }

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
        @ResponseBody
        public Map<String, Object> eliminarProducto(@PathVariable Integer id) {
        
        log.debug("Eliminando producto: {}", id);
        
        try {
            productoService.eliminarProducto(id);
            return Map.of(
                "status", "success",
                "mensaje", "Producto eliminado exitosamente"
            );
        } catch (Exception e) {
            log.error("Error al eliminar producto", e);
            return Map.of(
                "status", "error",
                "mensaje", "Error al eliminar el producto: " + e.getMessage()
            );
        }
    }

    /**
     * Eliminar todos los productos (operación administrativa)
     * POST /admin/productos/eliminar-todos
     */
    @PostMapping("/productos/eliminar-todos")
    @ResponseBody
    public Map<String, Object> eliminarTodosLosProductos() {
        log.warn("Eliminando TODOS los productos desde el panel admin");

        try {
            long eliminados = productoService.eliminarTodosLosProductos();
            return Map.of(
                    "status", "success",
                    "eliminados", eliminados,
                    "mensaje", "Se eliminaron " + eliminados + " productos"
            );
        } catch (Exception e) {
            log.error("Error al eliminar todos los productos", e);
            return Map.of(
                    "status", "error",
                    "mensaje", "Error al eliminar todos los productos: " + e.getMessage()
            );
        }
    }

    /**
     * Duplicar producto
     * POST /admin/productos/{id}/duplicar
     */
    @PostMapping("/productos/{id}/duplicar")
    @ResponseBody
    public Map<String, Object> duplicarProducto(@PathVariable Integer id) {
        log.debug("Duplicando producto: {}", id);
        
        try {
            var nuevoProducto = productoService.duplicarProducto(id);
            return Map.of(
                "status", "success",
                "nuevoId", nuevoProducto.getProductoId(),
                "mensaje", "Producto duplicado exitosamente"
            );
        } catch (Exception e) {
            log.error("Error al duplicar producto", e);
            return Map.of(
                "status", "error",
                "mensaje", "Error al duplicar el producto: " + e.getMessage()
            );
        }
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
            kpis.put("totalProductos", productoService.contarProductos());

            // Productos publicados
            kpis.put("productosPublicados", productoService.contarProductosPublicados());

            // Productos con stock bajo
            kpis.put("productosStockBajo", productoService.contarProductosStockBajo(5));

            // Total de pedidos (simplificado)
            kpis.put("totalPedidos", pedidoService.contarPedidos());
            
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
        dto.setResumen((productoDTO.getResumen() != null && !productoDTO.getResumen().isBlank())
            ? productoDTO.getResumen()
            : productoDTO.getDescripcion());
        dto.setDescripcion(productoDTO.getDescripcion());
        dto.setPrecio(productoDTO.getPrecio());
        dto.setCantidad(productoDTO.getCantidad());
        dto.setCategoriaId(productoDTO.getCategoriaId());
        dto.setSku(productoDTO.getSku());
        dto.setEstado(productoDTO.getEstado());
        dto.setImagen(productoDTO.getImagen());
        
        return dto;
    }
}

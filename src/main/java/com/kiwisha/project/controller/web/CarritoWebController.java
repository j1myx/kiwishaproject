package com.kiwisha.project.controller.web;

import com.kiwisha.project.dto.ProductoDTO;
import com.kiwisha.project.service.ProductoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.*;

/**
 * Controlador web para el carrito de compras.
 * Gestiona el carrito en sesión HTTP.
 */
@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
@Slf4j
public class CarritoWebController {

    private final ProductoService productoService;
    
    private static final String CARRITO_SESSION_KEY = "carrito";

    /**
     * Ver carrito de compras
     * GET /carrito
     */
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        log.debug("Mostrando carrito de compras");
        
        var itemsCarrito = obtenerCarrito(session);
        var totales = calcularTotales(itemsCarrito);
        
        model.addAttribute("itemsCarrito", itemsCarrito);
        model.addAttribute("subtotal", totales.get("subtotal"));
        model.addAttribute("total", totales.get("total"));
        model.addAttribute("cantidadItems", itemsCarrito.size());
        model.addAttribute("paginaActual", "carrito");
        
        return "public/carrito";
    }

    /**
     * Agregar producto al carrito
     * POST /carrito/agregar
     */
    @PostMapping("/agregar")
    public String agregarAlCarrito(
            @RequestParam Integer productoId,
            @RequestParam(defaultValue = "1") Integer cantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Agregando producto {} al carrito (cantidad: {})", productoId, cantidad);
        
        try {
            var producto = productoService.obtenerProductoPorId(productoId);
            
            // Verificar stock
            if (!productoService.verificarStock(productoId, cantidad)) {
                redirectAttributes.addFlashAttribute("error", 
                    "No hay suficiente stock disponible");
                return "redirect:/producto/" + producto.getSlug();
            }
            
            var carrito = obtenerCarrito(session);
            agregarItem(carrito, producto, cantidad);
            guardarCarrito(session, carrito);
            
            redirectAttributes.addFlashAttribute("success", 
                "Producto agregado al carrito");
                
        } catch (Exception e) {
            log.error("Error al agregar producto al carrito", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al agregar producto al carrito");
        }
        
        return "redirect:/carrito";
    }

    /**
     * Actualizar cantidad de un item
     * POST /carrito/actualizar/{productoId}
     */
    @PostMapping("/actualizar/{productoId}")
    public String actualizarCantidad(
            @PathVariable Integer productoId,
            @RequestParam Integer cantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Actualizando cantidad del producto {} a {}", productoId, cantidad);
        
        try {
            var carrito = obtenerCarrito(session);
            var item = carrito.stream()
                .filter(i -> i.getProducto().getProductoId().equals(productoId))
                .findFirst()
                .orElseThrow();
            
            // Verificar stock
            if (!productoService.verificarStock(productoId, cantidad)) {
                redirectAttributes.addFlashAttribute("error", 
                    "No hay suficiente stock para la cantidad solicitada");
                return "redirect:/carrito";
            }
            
            item.setCantidad(cantidad);
            item.setSubtotal(item.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
            
            guardarCarrito(session, carrito);
            redirectAttributes.addFlashAttribute("success", "Cantidad actualizada");
            
        } catch (Exception e) {
            log.error("Error al actualizar cantidad", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar cantidad");
        }
        
        return "redirect:/carrito";
    }

    /**
     * Eliminar producto del carrito
     * POST /carrito/eliminar/{productoId}
     */
    @PostMapping("/eliminar/{productoId}")
    public String eliminarDelCarrito(
            @PathVariable Integer productoId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Eliminando producto {} del carrito", productoId);
        
        var carrito = obtenerCarrito(session);
        carrito.removeIf(item -> item.getProducto().getProductoId().equals(productoId));
        
        guardarCarrito(session, carrito);
        redirectAttributes.addFlashAttribute("success", "Producto eliminado del carrito");
        
        return "redirect:/carrito";
    }

    /**
     * Vaciar carrito completo
     * POST /carrito/vaciar
     */
    @PostMapping("/vaciar")
    public String vaciarCarrito(HttpSession session, RedirectAttributes redirectAttributes) {
        log.debug("Vaciando carrito");
        
        session.removeAttribute(CARRITO_SESSION_KEY);
        redirectAttributes.addFlashAttribute("success", "Carrito vaciado");
        
        return "redirect:/carrito";
    }

    // ============================================
    // MÉTODOS PRIVADOS
    // ============================================

    @SuppressWarnings("unchecked")
    private List<ItemCarrito> obtenerCarrito(HttpSession session) {
        var carrito = (List<ItemCarrito>) session.getAttribute(CARRITO_SESSION_KEY);
        if (carrito == null) {
            carrito = new ArrayList<>();
            session.setAttribute(CARRITO_SESSION_KEY, carrito);
        }
        return carrito;
    }

    private void guardarCarrito(HttpSession session, List<ItemCarrito> carrito) {
        session.setAttribute(CARRITO_SESSION_KEY, carrito);
    }

    private void agregarItem(List<ItemCarrito> carrito, ProductoDTO producto, Integer cantidad) {
        // Buscar si ya existe el producto en el carrito
        var itemExistente = carrito.stream()
            .filter(item -> item.getProducto().getProductoId().equals(producto.getProductoId()))
            .findFirst();
        
        if (itemExistente.isPresent()) {
            // Actualizar cantidad
            var item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            item.setSubtotal(item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
        } else {
            // Agregar nuevo item
            carrito.add(new ItemCarrito(producto, cantidad));
        }
    }

    private Map<String, BigDecimal> calcularTotales(List<ItemCarrito> items) {
        var subtotal = items.stream()
            .map(ItemCarrito::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Por ahora, total = subtotal (sin envío ni impuestos)
        var total = subtotal;
        
        return Map.of(
            "subtotal", subtotal,
            "total", total
        );
    }

    // ============================================
    // CLASE INTERNA: ItemCarrito
    // ============================================

    /**
     * Representa un item en el carrito de compras
     */
    public static class ItemCarrito {
        private ProductoDTO producto;
        private Integer cantidad;
        private BigDecimal precio;
        private BigDecimal subtotal;

        public ItemCarrito(ProductoDTO producto, Integer cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
            this.precio = producto.getPrecio();
            this.subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
        }

        // Getters y Setters
        public ProductoDTO getProducto() {
            return producto;
        }

        public void setProducto(ProductoDTO producto) {
            this.producto = producto;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }

        public BigDecimal getPrecio() {
            return precio;
        }

        public void setPrecio(BigDecimal precio) {
            this.precio = precio;
        }

        public BigDecimal getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(BigDecimal subtotal) {
            this.subtotal = subtotal;
        }
    }
}

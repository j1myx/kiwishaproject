package com.kiwisha.project.controller.web;

import com.kiwisha.project.dto.AgregarCarritoDTO;
import com.kiwisha.project.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web para el carrito de compras.
 * Gestiona el carrito en sesión HTTP.
 */
@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
@Slf4j
public class CarritoWebController {

    private final CarritoService carritoService;

    /**
     * Ver carrito de compras
     * GET /carrito
     */
    @GetMapping
    public String verCarrito(HttpSession session, Model model) {
        log.debug("Mostrando carrito de compras");

        var carrito = carritoService.obtenerCarrito(session.getId());

        model.addAttribute("itemsCarrito", carrito.getItems());
        model.addAttribute("subtotal", carrito.getSubtotal());
        model.addAttribute("total", carrito.getTotal());
        model.addAttribute("cantidadItems", carrito.getCantidadItems());
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
            var carrito = carritoService.agregarItem(session.getId(),
                    AgregarCarritoDTO.builder().productoId(productoId).cantidad(cantidad).build());

            redirectAttributes.addFlashAttribute("success", "Producto agregado al carrito");
            redirectAttributes.addFlashAttribute("cantidadItems", carrito.getCantidadItems());
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
            carritoService.actualizarCantidad(session.getId(), productoId, cantidad);
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
        
        carritoService.eliminarItem(session.getId(), productoId);
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

        carritoService.limpiarCarrito(session.getId());
        redirectAttributes.addFlashAttribute("success", "Carrito vaciado");
        
        return "redirect:/carrito";
    }
}

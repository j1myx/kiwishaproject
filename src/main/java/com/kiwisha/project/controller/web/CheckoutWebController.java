package com.kiwisha.project.controller.web;

import com.kiwisha.project.controller.web.CarritoWebController.ItemCarrito;
import com.kiwisha.project.dto.CrearPedidoDTO;
import com.kiwisha.project.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador web para el proceso de checkout.
 * Maneja: formulario de datos, procesamiento de pedido, confirmación.
 */
@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
@Slf4j
public class CheckoutWebController {

    private final PedidoService pedidoService;
    
    private static final String CARRITO_SESSION_KEY = "carrito";

    /**
     * Paso 1: Formulario de checkout (datos personales y envío)
     * GET /checkout
     */
    @GetMapping
    public String mostrarFormularioCheckout(HttpSession session, Model model) {
        log.debug("Mostrando formulario de checkout");
        
        var carrito = obtenerCarrito(session);
        
        // Validar que el carrito no esté vacío
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/carrito";
        }
        
        // Crear DTO vacío para el formulario
        if (!model.containsAttribute("datosCheckout")) {
            model.addAttribute("datosCheckout", new DatosCheckoutForm());
        }
        
        model.addAttribute("itemsCarrito", carrito);
        model.addAttribute("paginaActual", "checkout");
        
        return "public/checkout";
    }

    /**
     * Paso 2: Procesar pedido
     * POST /checkout/procesar
     */
    @PostMapping("/procesar")
    public String procesarPedido(
            @Valid @ModelAttribute("datosCheckout") DatosCheckoutForm datosCheckout,
            BindingResult result,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Procesando pedido de checkout");
        
        var carrito = obtenerCarrito(session);
        
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/carrito";
        }
        
        // Si hay errores de validación, volver al formulario
        if (result.hasErrors()) {
            model.addAttribute("itemsCarrito", carrito);
            return "public/checkout";
        }
        
        try {
            // Convertir formulario a CrearPedidoDTO
            var crearPedidoDTO = convertirACrearPedidoDTO(datosCheckout);
            
            // Crear pedido (el servicio obtendrá los elementos del carrito en sesión)
            var pedido = pedidoService.crearPedido(session.getId(), crearPedidoDTO);
            
            // El servicio ya limpia el carrito, pero por consistencia lo hacemos aquí también
            session.removeAttribute(CARRITO_SESSION_KEY);
            
            // Guardar pedidoId en sesión para la confirmación
            session.setAttribute("ultimoPedidoId", pedido.getPedidoId());
            
            log.info("Pedido creado exitosamente: {}", pedido.getPedidoId());
            
            // Redirigir a pasarela de pago (por ahora simulado)
            return "redirect:/checkout/pago/" + pedido.getPedidoId();
            
        } catch (Exception e) {
            log.error("Error al procesar pedido", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar el pedido. Por favor intente nuevamente.");
            return "redirect:/checkout";
        }
    }

    /**
     * Paso 3: Página de pago (simulada)
     * GET /checkout/pago/{pedidoId}
     */
    @GetMapping("/pago/{pedidoId}")
    public String mostrarPago(@PathVariable Integer pedidoId, Model model) {
        log.debug("Mostrando página de pago para pedido: {}", pedidoId);
        
        try {
            var pedido = pedidoService.obtenerPedidoPorId(pedidoId);
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("paginaActual", "pago");
            
            return "public/pago";
            
        } catch (Exception e) {
            log.error("Error al cargar página de pago", e);
            return "redirect:/";
        }
    }

    /**
     * Confirmar pago (simulado)
     * POST /checkout/confirmar-pago
     */
    @PostMapping("/confirmar-pago")
    public String confirmarPago(
            @RequestParam Integer pedidoId,
            @RequestParam(required = false) String metodoPago,
            RedirectAttributes redirectAttributes) {
        
        log.debug("Confirmando pago para pedido: {}", pedidoId);
        
        try {
            // Aquí iría la integración con pasarela de pago real
            // Por ahora solo actualizamos el estado del pedido
            
            // Simular pago exitoso
            pedidoService.actualizarEstadoPedido(pedidoId, "PAGADO");
            
            redirectAttributes.addFlashAttribute("success", 
                "¡Pago realizado exitosamente!");
            
            return "redirect:/checkout/confirmacion/" + pedidoId;
            
        } catch (Exception e) {
            log.error("Error al confirmar pago", e);
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar el pago. Por favor intente nuevamente.");
            return "redirect:/checkout/pago/" + pedidoId;
        }
    }

    /**
     * Página de confirmación de compra
     * GET /checkout/confirmacion/{pedidoId}
     */
    @GetMapping("/confirmacion/{pedidoId}")
    public String mostrarConfirmacion(@PathVariable Integer pedidoId, Model model) {
        log.debug("Mostrando confirmación para pedido: {}", pedidoId);
        
        try {
            var pedido = pedidoService.obtenerPedidoPorId(pedidoId);
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("paginaActual", "confirmacion");
            
            return "public/confirmacion";
            
        } catch (Exception e) {
            log.error("Error al cargar confirmación", e);
            return "redirect:/";
        }
    }

    // ============================================
    // MÉTODOS PRIVADOS
    // ============================================

    @SuppressWarnings("unchecked")
    private List<ItemCarrito> obtenerCarrito(HttpSession session) {
        return (List<ItemCarrito>) session.getAttribute(CARRITO_SESSION_KEY);
    }

    private CrearPedidoDTO convertirACrearPedidoDTO(DatosCheckoutForm form) {
        
        var crearPedidoDTO = new CrearPedidoDTO();
        
        // Datos del cliente
        crearPedidoDTO.setNombre(form.getNombre());
        crearPedidoDTO.setApellido(form.getApellido());
        crearPedidoDTO.setEmail(form.getEmail());
        crearPedidoDTO.setTelefono(form.getTelefono());
        
        // Datos de envío
        crearPedidoDTO.setDireccion(form.getDireccion());
        crearPedidoDTO.setCiudad(form.getCiudad());
        crearPedidoDTO.setProvincia(form.getProvincia());
        crearPedidoDTO.setCodigoPostal(form.getCodigoPostal());
        crearPedidoDTO.setPais(form.getPais() != null ? form.getPais() : "Perú");
        
        // Método de envío (por defecto 1, debería venir del formulario)
        crearPedidoDTO.setMetodoEnvioId(form.getMetodoEnvioId() != null ? form.getMetodoEnvioId() : 1);
        
        // Aceptación de términos
        crearPedidoDTO.setAceptaTerminos(form.getAceptaTerminos() != null ? form.getAceptaTerminos() : true);
        
        // Notas adicionales
        if (form.getNotas() != null && !form.getNotas().trim().isEmpty()) {
            crearPedidoDTO.setNotas(form.getNotas());
        }
        
        return crearPedidoDTO;
    }

    // ============================================
    // CLASE INTERNA: DatosCheckoutForm
    // ============================================

    /**
     * Formulario para capturar datos del checkout
     */
    public static class DatosCheckoutForm {
        private String nombre;
        private String apellido;
        private String email;
        private String telefono;
        private String direccion;
        private String ciudad;
        private String provincia;
        private String codigoPostal;
        private String pais;
        private Integer metodoEnvioId;
        private Boolean aceptaTerminos;
        private String notas;

        // Getters y Setters
        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }

        public String getCiudad() {
            return ciudad;
        }

        public void setCiudad(String ciudad) {
            this.ciudad = ciudad;
        }

        public String getProvincia() {
            return provincia;
        }

        public void setProvincia(String provincia) {
            this.provincia = provincia;
        }

        public String getCodigoPostal() {
            return codigoPostal;
        }

        public void setCodigoPostal(String codigoPostal) {
            this.codigoPostal = codigoPostal;
        }

        public String getPais() {
            return pais;
        }

        public void setPais(String pais) {
            this.pais = pais;
        }

        public Integer getMetodoEnvioId() {
            return metodoEnvioId;
        }

        public void setMetodoEnvioId(Integer metodoEnvioId) {
            this.metodoEnvioId = metodoEnvioId;
        }

        public Boolean getAceptaTerminos() {
            return aceptaTerminos;
        }

        public void setAceptaTerminos(Boolean aceptaTerminos) {
            this.aceptaTerminos = aceptaTerminos;
        }

        public String getNotas() {
            return notas;
        }

        public void setNotas(String notas) {
            this.notas = notas;
        }
    }
}

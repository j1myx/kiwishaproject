package com.kiwisha.project.controller.web;

import com.kiwisha.project.dto.CrearPedidoDTO;
import com.kiwisha.project.service.CarritoService;
import com.kiwisha.project.service.MercadoPagoService;
import com.kiwisha.project.service.PedidoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

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
    private final CarritoService carritoService;
    private final MercadoPagoService mercadoPagoService;

    @Value("${app.public-base-url:}")
    private String publicBaseUrl;
    
    /**
     * Paso 1: Formulario de checkout (datos personales y envío)
     * GET /checkout
     */
    @GetMapping
    public String mostrarFormularioCheckout(HttpSession session, Model model) {
        log.debug("Mostrando formulario de checkout");

        var carrito = carritoService.obtenerCarrito(session.getId());
        
        // Validar que el carrito no esté vacío
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            return "redirect:/carrito";
        }
        
        // Crear DTO vacío para el formulario
        if (!model.containsAttribute("datosCheckout")) {
            model.addAttribute("datosCheckout", new DatosCheckoutForm());
        }
        
        model.addAttribute("carrito", carrito);
        model.addAttribute("itemsCarrito", carrito.getItems());
        model.addAttribute("paginaActual", "checkout");
        model.addAttribute("pageTitle", "Checkout");
        
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

        var carrito = carritoService.obtenerCarrito(session.getId());
        
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            return "redirect:/carrito";
        }
        
        // Si hay errores de validación, volver al formulario
        if (result.hasErrors()) {
            model.addAttribute("carrito", carrito);
            model.addAttribute("itemsCarrito", carrito.getItems());
            return "public/checkout";
        }
        
        try {
            // Convertir formulario a CrearPedidoDTO
            var crearPedidoDTO = convertirACrearPedidoDTO(datosCheckout);
            
            // Crear pedido (el servicio obtendrá los elementos del carrito en sesión)
            var pedido = pedidoService.crearPedido(session.getId(), crearPedidoDTO);

            // El servicio limpia el carrito (persistido por sesión)
            carritoService.limpiarCarrito(session.getId());
            
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
    public String mostrarPago(
            @PathVariable Integer pedidoId,
            HttpSession session,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {
        log.debug("Mostrando página de pago para pedido: {}", pedidoId);
        
        try {
            var pedido = pedidoService.obtenerPedidoPorId(pedidoId);

            String preferenceSessionKey = "mpPreferenceId:" + pedidoId;
            String checkoutUrlSessionKey = "mpCheckoutUrl:" + pedidoId;
            String mpPreferenceId = (String) session.getAttribute(preferenceSessionKey);
            String mpCheckoutUrl = (String) session.getAttribute(checkoutUrlSessionKey);

            if (mpPreferenceId == null || mpPreferenceId.isBlank() || mpCheckoutUrl == null || mpCheckoutUrl.isBlank()) {
                String baseUrl = buildBaseUrl(request);
                try {
                    var pref = mercadoPagoService.crearPreferenciaParaPedido(pedido, baseUrl);
                    mpPreferenceId = pref != null ? pref.getPreferenceId() : null;

                    boolean useSandbox = false;
                    if (mercadoPagoService instanceof com.kiwisha.project.service.impl.MercadoPagoServiceImpl impl) {
                        useSandbox = impl.usarSandboxInitPoint();
                    }
                    mpCheckoutUrl = pref != null ? (useSandbox ? pref.getSandboxInitPoint() : pref.getInitPoint()) : null;

                    session.setAttribute(preferenceSessionKey, mpPreferenceId);
                    session.setAttribute(checkoutUrlSessionKey, mpCheckoutUrl);
                } catch (Exception e) {
                    log.error("No se pudo crear preferencia de Mercado Pago", e);
                    redirectAttributes.addFlashAttribute("error",
                            "No se pudo inicializar Mercado Pago. Verifica la configuración de credenciales.");
                }
            }
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("paginaActual", "pago");
            model.addAttribute("pageTitle", "Pago");

            model.addAttribute("mpPublicKey", mercadoPagoService.obtenerPublicKey());
            model.addAttribute("mpPreferenceId", mpPreferenceId);
            model.addAttribute("mpCheckoutUrl", mpCheckoutUrl);
            
            return "public/pago";
            
        } catch (Exception e) {
            log.error("Error al cargar página de pago", e);
            return "redirect:/";
        }
    }

    /**
     * Endpoint simple para que la página de pago pueda consultar el estado del pedido y
     * actualizarse automáticamente incluso si el usuario no vuelve desde Mercado Pago.
     */
    @GetMapping("/estado/{pedidoId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerEstadoPedido(@PathVariable Integer pedidoId) {
        Map<String, Object> payload = new HashMap<>();

        try {
            var pedido = pedidoService.obtenerPedidoPorId(pedidoId);
            String estado = pedido != null ? pedido.getEstado() : null;

            if (estado != null && estado.equalsIgnoreCase("PENDIENTE")) {
                String externalReference = pedido.getCodigo() != null ? pedido.getCodigo() : String.valueOf(pedidoId);
                String mpStatus = mercadoPagoService.buscarEstadoPagoPorExternalReference(externalReference);

                if (mpStatus != null) {
                    String normalized = mpStatus.trim().toLowerCase();
                    if (normalized.equals("approved")) {
                        pedidoService.actualizarEstadoPedido(pedidoId, "CONFIRMADO");
                        estado = "CONFIRMADO";
                    } else if (normalized.equals("rejected") || normalized.equals("cancelled")) {
                        pedidoService.actualizarEstadoPedido(pedidoId, "CANCELADO");
                        estado = "CANCELADO";
                    }
                }
            }

            payload.put("pedidoId", pedidoId);
            payload.put("estado", estado);
            return ResponseEntity.ok(payload);
        } catch (Exception e) {
            log.warn("No se pudo obtener estado del pedido para polling. pedidoId={}", pedidoId, e);
            payload.put("pedidoId", pedidoId);
            payload.put("estado", null);
            return ResponseEntity.ok(payload);
        }
    }

    /**
     * Return URLs de Mercado Pago (Checkout Pro)
     */
    @GetMapping("/mercadopago/success")
    public String mercadoPagoSuccess(@RequestParam Integer pedidoId, RedirectAttributes redirectAttributes) {
        log.info("MercadoPago success para pedidoId={}", pedidoId);
        try {
            pedidoService.actualizarEstadoPedido(pedidoId, "CONFIRMADO");
            redirectAttributes.addFlashAttribute("success", "¡Pago aprobado! Pedido confirmado.");
            return "redirect:/checkout/confirmacion/" + pedidoId;
        } catch (Exception e) {
            log.error("Error procesando success MercadoPago", e);
            redirectAttributes.addFlashAttribute("error", "Pago aprobado, pero no se pudo actualizar el pedido.");
            return "redirect:/checkout/confirmacion/" + pedidoId;
        }
    }

    @GetMapping("/mercadopago/failure")
    public String mercadoPagoFailure(@RequestParam Integer pedidoId, RedirectAttributes redirectAttributes) {
        log.info("MercadoPago failure para pedidoId={}", pedidoId);
        redirectAttributes.addFlashAttribute("error", "El pago fue rechazado. Intenta nuevamente.");
        return "redirect:/checkout/pago-rechazado/" + pedidoId;
    }

    @GetMapping("/mercadopago/pending")
    public String mercadoPagoPending(@RequestParam Integer pedidoId, RedirectAttributes redirectAttributes) {
        log.info("MercadoPago pending para pedidoId={}", pedidoId);
        redirectAttributes.addFlashAttribute("error", "El pago está pendiente de confirmación.");
        return "redirect:/checkout/pago/" + pedidoId;
    }

    /**
     * Página de pago rechazado
     * GET /checkout/pago-rechazado/{pedidoId}
     */
    @GetMapping("/pago-rechazado/{pedidoId}")
    public String mostrarPagoRechazado(@PathVariable Integer pedidoId, Model model) {
        try {
            var pedido = pedidoService.obtenerPedidoPorId(pedidoId);
            model.addAttribute("pedido", pedido);
            model.addAttribute("paginaActual", "pago");
            model.addAttribute("pageTitle", "Pago rechazado");
            return "public/pago-rechazado";
        } catch (Exception e) {
            log.error("Error al cargar pago rechazado", e);
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
            pedidoService.actualizarEstadoPedido(pedidoId, "CONFIRMADO");
            
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

    private String buildBaseUrl(HttpServletRequest request) {
        if (publicBaseUrl != null && !publicBaseUrl.isBlank()) {
            String normalized = publicBaseUrl.trim();
            while (normalized.endsWith("/")) {
                normalized = normalized.substring(0, normalized.length() - 1);
            }
            return normalized;
        }

        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath() != null ? request.getContextPath() : "";

        boolean isDefaultPort = ("http".equalsIgnoreCase(scheme) && serverPort == 80)
                || ("https".equalsIgnoreCase(scheme) && serverPort == 443);

        if (isDefaultPort) {
            return scheme + "://" + serverName + contextPath;
        }
        return scheme + "://" + serverName + ":" + serverPort + contextPath;
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

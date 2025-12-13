package com.kiwisha.project.controller.web;

import com.kiwisha.project.dto.RegistroUsuarioDTO;
import com.kiwisha.project.model.Rol;
import com.kiwisha.project.model.RolUsuario;
import com.kiwisha.project.model.Usuario;
import com.kiwisha.project.repository.RolRepository;
import com.kiwisha.project.repository.RolUsuarioRepository;
import com.kiwisha.project.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * Controlador web para autenticación (login/registro).
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class AuthWebController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Página de login
     * GET /login
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            @RequestParam(required = false) String registered,
            Model model) {
        
        log.debug("Accediendo a página de login");
        
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        
        if (logout != null) {
            model.addAttribute("message", "Has cerrado sesión exitosamente");
        }

        if (registered != null) {
            model.addAttribute("message", "Cuenta creada. Ahora puedes iniciar sesión");
        }
        
        return "public/login";
    }

    /**
     * Página de registro
     * GET /registro
     */
    @GetMapping("/registro")
    public String registro(Model model) {
        if (!model.containsAttribute("registro")) {
            model.addAttribute("registro", new RegistroUsuarioDTO());
        }
        return "public/registro";
    }

    /**
     * Procesa registro
     * POST /registro
     */
    @PostMapping("/registro")
    public String procesarRegistro(
            @Valid @ModelAttribute("registro") RegistroUsuarioDTO registro,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "public/registro";
        }

        if (!registro.getPassword().equals(registro.getConfirmPassword())) {
            result.addError(new FieldError("registro", "confirmPassword", "Las contraseñas no coinciden"));
            return "public/registro";
        }

        String email = registro.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            result.addError(new FieldError("registro", "email", "Ya existe una cuenta con este email"));
            return "public/registro";
        }

        String baseUsername = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9._-]", "");
        if (baseUsername.isBlank()) {
            baseUsername = "usuario";
        }

        String nombreUsuario = baseUsername;
        int suffix = 1;
        while (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            nombreUsuario = baseUsername + suffix;
            suffix++;
        }

        Usuario usuario = new Usuario();
        usuario.setPrimerNombre(registro.getPrimerNombre());
        usuario.setApellidos(registro.getApellidos());
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setEmail(email);
        usuario.setHashContrasena(passwordEncoder.encode(registro.getPassword()));
        usuario.setActivo(true);
        usuario.setCreadoPor(1);
        usuario.setCreadoEn(LocalDateTime.now());

        Usuario saved = usuarioRepository.save(usuario);

        Rol rolUsuario = rolRepository.findByNombre("USUARIO")
                .orElseGet(() -> {
                    Rol nuevoRol = new Rol();
                    nuevoRol.setNombre("USUARIO");
                    nuevoRol.setCreadoPor(1);
                    nuevoRol.setCreadoEn(LocalDateTime.now());
                    return rolRepository.save(nuevoRol);
                });

        RolUsuario ru = new RolUsuario();
        ru.setUsuario(saved);
        ru.setRol(rolUsuario);
        ru.setCreadoPor(1);
        ru.setCreadoEn(LocalDateTime.now());
        rolUsuarioRepository.save(ru);

        redirectAttributes.addFlashAttribute("message", "Cuenta creada. Ahora puedes iniciar sesión");
        return "redirect:/login?registered=true";
    }
}

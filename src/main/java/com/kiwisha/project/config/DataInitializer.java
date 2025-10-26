package com.kiwisha.project.config;

import com.kiwisha.project.model.Rol;
import com.kiwisha.project.model.RolUsuario;
import com.kiwisha.project.model.Usuario;
import com.kiwisha.project.repository.RolRepository;
import com.kiwisha.project.repository.RolUsuarioRepository;
import com.kiwisha.project.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/**
 * Inicializador de datos para crear roles y usuario admin por defecto
 * Se ejecuta al iniciar la aplicación si no existen datos
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Crear roles si no existen
            if (rolRepository.count() == 0) {
                log.info("Creando roles por defecto...");
                
                Rol adminRole = new Rol();
                adminRole.setNombre("ADMIN");
                adminRole.setCreadoPor(1); // Sistema
                adminRole.setCreadoEn(LocalDateTime.now());
                rolRepository.save(adminRole);
                
                Rol clienteRole = new Rol();
                clienteRole.setNombre("CLIENTE");
                clienteRole.setCreadoPor(1); // Sistema
                clienteRole.setCreadoEn(LocalDateTime.now());
                rolRepository.save(clienteRole);
                
                log.info("Roles creados exitosamente");
            }

            // Crear usuario admin si no existe
            if (usuarioRepository.findByEmail("admin@kiwisha.com").isEmpty()) {
                log.info("Creando usuario admin por defecto...");
                
                Usuario admin = new Usuario();
                admin.setPrimerNombre("Admin");
                admin.setApellidos("Kiwisha");
                admin.setNombreUsuario("admin");
                admin.setEmail("admin@kiwisha.com");
                admin.setMovil("999999999"); // Teléfono por defecto
                admin.setHashContrasena(passwordEncoder.encode("admin123"));
                admin.setActivo(true);
                admin.setCreadoPor(1); // Sistema
                admin.setCreadoEn(LocalDateTime.now());
                Usuario savedAdmin = usuarioRepository.save(admin);
                
                // Asignar rol ADMIN
                Rol adminRole = rolRepository.findByNombre("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
                
                RolUsuario rolUsuario = new RolUsuario();
                rolUsuario.setUsuario(savedAdmin);
                rolUsuario.setRol(adminRole);
                rolUsuario.setCreadoPor(1); // Sistema
                rolUsuario.setCreadoEn(LocalDateTime.now());
                rolUsuarioRepository.save(rolUsuario);
                
                log.info("Usuario admin creado exitosamente (email: admin@kiwisha.com, password: admin123)");
            } else {
                // Verificar que el usuario admin tenga el rol asignado
                log.info("Verificando roles del usuario admin...");
                Usuario admin = usuarioRepository.findByEmail("admin@kiwisha.com")
                        .orElseThrow(() -> new RuntimeException("Usuario admin no encontrado"));
                
                // Verificar si tiene rol ADMIN
                boolean tieneRolAdmin = admin.getRolUsuarios().stream()
                        .anyMatch(ru -> "ADMIN".equals(ru.getRol().getNombre()));
                
                if (!tieneRolAdmin) {
                    log.warn("Usuario admin no tiene rol ADMIN asignado. Asignando...");
                    Rol adminRole = rolRepository.findByNombre("ADMIN")
                            .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
                    
                    RolUsuario rolUsuario = new RolUsuario();
                    rolUsuario.setUsuario(admin);
                    rolUsuario.setRol(adminRole);
                    rolUsuario.setCreadoPor(1); // Sistema
                    rolUsuario.setCreadoEn(LocalDateTime.now());
                    rolUsuarioRepository.save(rolUsuario);
                    
                    log.info("Rol ADMIN asignado correctamente al usuario admin");
                } else {
                    log.info("Usuario admin ya tiene rol ADMIN asignado");
                }
            }
        };
    }
}

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
                adminRole.setCreadoEn(LocalDateTime.now());
                rolRepository.save(adminRole);
                
                Rol clienteRole = new Rol();
                clienteRole.setNombre("CLIENTE");
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
                admin.setHashContrasena(passwordEncoder.encode("admin123"));
                admin.setActivo(true);
                admin.setCreadoEn(LocalDateTime.now());
                Usuario savedAdmin = usuarioRepository.save(admin);
                
                // Asignar rol ADMIN
                Rol adminRole = rolRepository.findByNombre("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));
                
                RolUsuario rolUsuario = new RolUsuario();
                rolUsuario.setUsuario(savedAdmin);
                rolUsuario.setRol(adminRole);
                rolUsuario.setCreadoEn(LocalDateTime.now());
                rolUsuarioRepository.save(rolUsuario);
                
                log.info("Usuario admin creado exitosamente (email: admin@kiwisha.com, password: admin123)");
            }
        };
    }
}

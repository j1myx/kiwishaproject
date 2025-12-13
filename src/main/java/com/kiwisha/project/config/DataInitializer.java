package com.kiwisha.project.config;

import com.kiwisha.project.model.Categoria;
import com.kiwisha.project.model.Rol;
import com.kiwisha.project.model.RolUsuario;
import com.kiwisha.project.model.Usuario;
import com.kiwisha.project.repository.CategoriaRepository;
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
 * Inicializador de datos para crear roles, categorías y usuario admin por defecto
 * Se ejecuta al iniciar la aplicación si no existen datos
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Crear roles base si no existen
            asegurarRolExiste("ADMIN");
            asegurarRolExiste("CLIENTE");
            asegurarRolExiste("USUARIO");

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
            
            // Crear categorías por defecto si no existen
            if (categoriaRepository.count() == 0) {
                log.info("Creando categorías por defecto...");
                
                try {
                    String[] categoriasNombres = {"Textiles", "Cerámica", "Joyería", "Decoración", "Accesorios"};
                    String[] categoriasDescripciones = {
                        "Productos textiles artesanales peruanos",
                        "Artesanías en cerámica de alta calidad",
                        "Joyería artesanal peruana",
                        "Artículos decorativos hechos a mano",
                        "Accesorios y complementos artesanales"
                    };
                    
                    for (int i = 0; i < categoriasNombres.length; i++) {
                        Categoria categoria = new Categoria();
                        categoria.setTitulo(categoriasNombres[i]);
                        categoria.setResumen(categoriasDescripciones[i]);
                        categoria.setCreadoPor(1); // Sistema
                        categoria.setCreadoEn(LocalDateTime.now());
                        categoriaRepository.save(categoria);
                        log.info("Categoría creada: {}", categoriasNombres[i]);
                    }
                    
                    log.info("Categorías creadas exitosamente");
                } catch (Exception e) {
                    log.warn("No se pudieron crear categorías automáticamente. Es necesario ejecutar el script SQL manualmente.");
                    log.warn("Error: {}", e.getMessage());
                }
            } else {
                log.info("Las categorías ya existen en la base de datos");
            }
        };
    }

    private void asegurarRolExiste(String nombreRol) {
        if (rolRepository.findByNombre(nombreRol).isPresent()) {
            return;
        }

        log.info("Creando rol por defecto: {}", nombreRol);
        Rol rol = new Rol();
        rol.setNombre(nombreRol);
        rol.setCreadoPor(1);
        rol.setCreadoEn(LocalDateTime.now());
        rolRepository.save(rol);
    }
}

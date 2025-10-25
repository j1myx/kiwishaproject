package com.kiwisha.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para el sistema Kiwisha
 * Gestiona autenticación, autorización y seguridad de endpoints
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas - accesibles sin autenticación
                .requestMatchers(
                    "/api/public/**",
                    "/productos/**",
                    "/categorias/**",
                    "/carrito/**",
                    "/checkout/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/error",
                    "/login",
                    "/registro"
                ).permitAll()
                
                // Rutas de administrador - requieren rol ADMIN
                .requestMatchers(
                    "/admin/**",
                    "/api/admin/**"
                ).hasRole("ADMIN")
                
                // Rutas de cliente - requieren autenticación
                .requestMatchers(
                    "/mi-cuenta/**",
                    "/mis-pedidos/**",
                    "/api/cliente/**"
                ).hasAnyRole("CLIENTE", "ADMIN")
                
                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("kiwishaRememberMeKey")
                .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 días
                .rememberMeParameter("remember-me")
            )
            .csrf(csrf -> csrf
                // Habilitar CSRF para formularios, deshabilitar para APIs REST
                .ignoringRequestMatchers("/api/**")
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    /**
     * Bean para codificación de contraseñas usando BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

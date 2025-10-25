package com.kiwisha.project.service.impl;

import com.kiwisha.project.model.Usuario;
import com.kiwisha.project.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Implementación de UserDetailsService para Spring Security
 * Carga información de usuarios desde la base de datos para autenticación
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario por su email (usado como username)
     * 
     * @param email El email del usuario
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException Si el usuario no existe o está inactivo
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con email: " + email));

        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getHashContrasena())
                .authorities(getAuthorities(usuario))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!usuario.getActivo())
                .build();
    }

    /**
     * Obtiene las autoridades (roles) del usuario
     * 
     * @param usuario El usuario del cual obtener los roles
     * @return Colección de GrantedAuthority con prefijo ROLE_
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        return usuario.getRolUsuarios().stream()
                .map(rolUsuario -> new SimpleGrantedAuthority(
                        "ROLE_" + rolUsuario.getRol().getNombre().toUpperCase()))
                .collect(Collectors.toList());
    }
}

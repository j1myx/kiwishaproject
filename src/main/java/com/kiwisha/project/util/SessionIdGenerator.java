package com.kiwisha.project.util;

import java.util.UUID;

/**
 * Utilidad para generar IDs de sesión únicos.
 */
public class SessionIdGenerator {

    /**
     * Genera un ID de sesión único.
     * 
     * @return El ID de sesión generado
     */
    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Genera un código único para cupones o transacciones.
     * 
     * @param prefix El prefijo del código
     * @return El código generado
     */
    public static String generateCode(String prefix) {
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + uuid;
    }
}

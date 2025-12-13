package com.kiwisha.project.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio para gestionar imágenes de productos.
 *
 * Las imágenes se almacenan en el filesystem (uploads/) y se exponen por /files/**.
 */
public interface ImagenProductoService {

    /**
     * Guarda y optimiza la imagen principal del producto.
     *
     * @param file archivo de imagen
     * @return ruta relativa dentro de uploads/ (ej. "products/uuid.jpg")
     */
    String guardarImagenPrincipal(MultipartFile file);
}

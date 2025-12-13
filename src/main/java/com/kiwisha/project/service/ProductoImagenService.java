package com.kiwisha.project.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoImagenService {

    List<com.kiwisha.project.dto.ProductoImagenItemDTO> listarImagenesPorProductoId(Integer productoId);

    List<String> listarRutasPorProductoId(Integer productoId);

    void agregarImagenes(Integer productoId, List<MultipartFile> files);

    void eliminarImagen(Integer productoId, Integer productoImagenId);
}

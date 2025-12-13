package com.kiwisha.project.service.impl;

import com.kiwisha.project.exception.BusinessException;
import com.kiwisha.project.dto.ProductoImagenItemDTO;
import com.kiwisha.project.model.Producto;
import com.kiwisha.project.model.ProductoImagen;
import com.kiwisha.project.repository.ProductoImagenRepository;
import com.kiwisha.project.repository.ProductoRepository;
import com.kiwisha.project.service.ImagenProductoService;
import com.kiwisha.project.service.ProductoImagenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoImagenServiceImpl implements ProductoImagenService {

    private final ProductoImagenRepository productoImagenRepository;
    private final ProductoRepository productoRepository;
    private final ImagenProductoService imagenProductoService;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoImagenItemDTO> listarImagenesPorProductoId(Integer productoId) {
        if (productoId == null) {
            return List.of();
        }

        return productoImagenRepository.findByProducto_ProductoIdOrderByProductoImagenIdAsc(productoId)
                .stream()
                .map(pi -> ProductoImagenItemDTO.builder()
                        .productoImagenId(pi.getProductoImagenId())
                        .nombre(pi.getNombre())
                        .ruta(pi.getRuta())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> listarRutasPorProductoId(Integer productoId) {
        if (productoId == null) {
            return List.of();
        }
        return productoImagenRepository.findRutasByProductoId(productoId);
    }

    @Override
    @Transactional
    public void agregarImagenes(Integer productoId, List<MultipartFile> files) {
        if (productoId == null) {
            throw new BusinessException("Producto inválido para agregar imágenes");
        }
        if (files == null || files.isEmpty()) {
            return;
        }

        Producto productoRef = productoRepository.getReferenceById(productoId);

        int nextId = productoImagenRepository.findMaxId();
        List<ProductoImagen> nuevas = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }

            String ruta = imagenProductoService.guardarImagenPrincipal(file);

            ProductoImagen img = new ProductoImagen();
            img.setProductoImagenId(++nextId);
            img.setProducto(productoRef);
            img.setRuta(ruta);

            String nombre = file.getOriginalFilename();
            if (nombre == null) {
                nombre = "";
            }
            if (nombre.length() > 50) {
                nombre = nombre.substring(0, 50);
            }
            img.setNombre(nombre);

            nuevas.add(img);
        }

        if (!nuevas.isEmpty()) {
            productoImagenRepository.saveAll(nuevas);
            log.debug("Se guardaron {} imágenes adicionales para producto {}", nuevas.size(), productoId);
        }
    }

    @Override
    @Transactional
    public void eliminarImagen(Integer productoId, Integer productoImagenId) {
        if (productoId == null || productoImagenId == null) {
            throw new BusinessException("Parámetros inválidos para eliminar imagen");
        }

        int deleted = productoImagenRepository.deleteByProductoImagenIdAndProducto_ProductoId(productoImagenId, productoId);
        if (deleted <= 0) {
            throw new BusinessException("No se encontró la imagen para eliminar");
        }

        log.debug("Imagen adicional {} eliminada para producto {}", productoImagenId, productoId);
    }
}

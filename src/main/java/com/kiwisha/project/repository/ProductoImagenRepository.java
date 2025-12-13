package com.kiwisha.project.repository;

import com.kiwisha.project.model.ProductoImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Integer> {

    @Query("select coalesce(max(pi.productoImagenId), 0) from ProductoImagen pi")
    int findMaxId();

    @Query("select pi.ruta from ProductoImagen pi where pi.producto.productoId = :productoId order by pi.productoImagenId asc")
    List<String> findRutasByProductoId(@Param("productoId") Integer productoId);

    List<ProductoImagen> findByProducto_ProductoIdOrderByProductoImagenIdAsc(Integer productoId);

    int deleteByProductoImagenIdAndProducto_ProductoId(Integer productoImagenId, Integer productoId);
}

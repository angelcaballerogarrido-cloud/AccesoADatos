package com.example.accessingdatajpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.accessingdatajpa.entities.Producto;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // REQUISITO: Consulta NATIVA de SQL (nativeQuery = true) con parámetros posicionales (?1)
    @Query(value = "SELECT * FROM productos WHERE precio > ?1", nativeQuery = true)
    List<Producto> findProductosCaros(double precioMinimo);
}

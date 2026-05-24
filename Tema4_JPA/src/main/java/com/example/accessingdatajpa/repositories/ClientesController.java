package com.example.accessingdatajpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.accessingdatajpa.entities.Clientes;
import java.util.List;

@Repository
public interface ClientesController extends JpaRepository<Clientes, Long> {

    // REQUISITO: Consultas JPQL con parámetros con nombre (:parametro)
    @Query("SELECT c FROM Clientes c WHERE c.direccion = :direccion AND c.nombre = :nombre")
    List<Clientes> findByDireccionAndNombre(@Param("direccion") String direccion, @Param("nombre") String nombre);

    // REQUISITO: UPDATE usando @Modifying + @Query
    @Modifying
    @Query("UPDATE Clientes c SET c.direccion = :direccion WHERE c.nombre = :nombre")
    int updateDireccionByNombre(@Param("direccion") String direccion, @Param("nombre") String nombre);

    // REQUISITO: DELETE usando @Modifying + @Query
    @Modifying
    @Query("DELETE FROM Clientes c WHERE c.apellido = :apellido")
    void deleteClientesByApellido(@Param("apellido") String apellido);
}

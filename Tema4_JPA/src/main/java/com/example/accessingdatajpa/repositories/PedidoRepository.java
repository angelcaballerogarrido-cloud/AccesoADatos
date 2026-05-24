package com.example.accessingdatajpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.accessingdatajpa.entities.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Al extender de JpaRepository hereda automáticamente findAll(), save(), delete()...
}

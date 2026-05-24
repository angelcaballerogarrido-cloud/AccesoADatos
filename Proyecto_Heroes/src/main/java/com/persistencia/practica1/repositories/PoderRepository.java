package com.persistencia.practica1.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.persistencia.practica1.entities.Poder;

@Repository
public interface PoderRepository extends JpaRepository<Poder, Long> {}

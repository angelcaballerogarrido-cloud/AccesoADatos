package com.persistencia.practica1.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.persistencia.practica1.entities.Equipo;
import com.persistencia.practica1.entities.Heroe;
import com.persistencia.practica1.repositories.EquipoRepository;
import com.persistencia.practica1.repositories.HeroeRepository;

@Service
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepo;
    
    @Autowired
    private HeroeRepository heroeRepo;

    public List<Equipo> findAll() { return equipoRepo.findAll(); }
    public Optional<Equipo> findById(Long id) { return equipoRepo.findById(id); }

    @Transactional
    public Equipo save(Equipo equipo) { return equipoRepo.save(equipo); }

    // Requisito del profesor: Método transaccional para enlazar héroes a un equipo
    @Transactional
    public Equipo actualizarMiembros(Long equipoId, List<Long> heroeIds) {
        Equipo equipo = equipoRepo.findById(equipoId)
            .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));
            
        List<Heroe> nuevosHeroes = heroeRepo.findAllById(heroeIds);
        
        // Relación bidireccional, asignamos el equipo a los héroes también
        for (Heroe h : nuevosHeroes) {
            h.setMiEquipo(equipo);
        }
        
        equipo.setHeroes(nuevosHeroes);
        return equipoRepo.save(equipo);
    }
}

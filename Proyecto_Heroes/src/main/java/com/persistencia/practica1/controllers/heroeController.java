package com.persistencia.practica1.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.persistencia.practica1.entities.Heroe;
import com.persistencia.practica1.services.HeroeService;
import com.persistencia.practica1.dtos.PoderesUpdateDTO;

import org.springframework.security.access.annotation.Secured;

@RestController
@RequestMapping("/api/v1/heroes")
public class heroeController {
    
    @Autowired
    private HeroeService service;

    @GetMapping
    public List<Heroe> getAllHeroes() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Heroe> getHeroeById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Secured("ROLE_ADMIN") // Rúbrica: Prueba de roles por método
    public Heroe create(@RequestBody Heroe heroe) {
        return service.save(heroe);
    }

    // PUT para enlazar la relación N-N de Poderes
    @PutMapping("/{id}/poderes")
    public ResponseEntity<Heroe> actualizarPoderes(@PathVariable Long id, @RequestBody PoderesUpdateDTO updateDTO) {
        try {
            Heroe heroeActualizado = service.actualizarPoderes(id, updateDTO.getPoderIds());
            return ResponseEntity.ok(heroeActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

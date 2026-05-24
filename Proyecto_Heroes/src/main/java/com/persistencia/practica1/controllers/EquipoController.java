package com.persistencia.practica1.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.persistencia.practica1.entities.Equipo;
import com.persistencia.practica1.services.EquipoService;
import com.persistencia.practica1.dtos.MiembrosUpdateDTO;

@RestController
@RequestMapping("/api/v1/equipos")
public class EquipoController {

    @Autowired
    private EquipoService service;

    @GetMapping
    public List<Equipo> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> getById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Equipo create(@RequestBody Equipo equipo) {
        return service.save(equipo);
    }

    // REQUISITO: El famoso PUT de la guía del profesor para actualizar miembros mediante JSON DTO
    @PutMapping("/{id}/miembros")
    public ResponseEntity<Equipo> actualizarMiembros(@PathVariable Long id, @RequestBody MiembrosUpdateDTO updateDTO) {
        try {
            Equipo equipoActualizado = service.actualizarMiembros(id, updateDTO.getHeroeIds());
            return ResponseEntity.ok(equipoActualizado); // Retorna 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si el ID del equipo falla
        }
    }
}

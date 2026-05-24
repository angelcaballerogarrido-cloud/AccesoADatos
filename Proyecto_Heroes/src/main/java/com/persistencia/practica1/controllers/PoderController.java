package com.persistencia.practica1.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.persistencia.practica1.entities.Poder;
import com.persistencia.practica1.services.PoderService;

@RestController
@RequestMapping("/api/v1/poderes")
public class PoderController {

    @Autowired
    private PoderService service;

    @GetMapping
    public List<Poder> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poder> getById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Poder create(@RequestBody Poder poder) {
        return service.save(poder);
    }
}

package com.persistencia.practica1.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.persistencia.practica1.entities.Poder;
import com.persistencia.practica1.repositories.PoderRepository;

@Service
public class PoderService {

    @Autowired
    private PoderRepository repo;

    public List<Poder> findAll() { return repo.findAll(); }
    public Optional<Poder> findById(Long id) { return repo.findById(id); }

    @Transactional
    public Poder save(Poder poder) { return repo.save(poder); }
}

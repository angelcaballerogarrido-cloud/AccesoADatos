package com.persistencia.practica1.services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import com.persistencia.practica1.entities.Heroe;
import com.persistencia.practica1.entities.Poder;
import com.persistencia.practica1.repositories.HeroeRepository;
import com.persistencia.practica1.repositories.PoderRepository;

@Service
public class HeroeService {
    
    @Autowired
    private HeroeRepository heroeRepo;
    
    @Autowired
    private PoderRepository poderRepo;

    public List<Heroe> findAll() { return heroeRepo.findAll(); }
    public Optional<Heroe> findById(Long id) { return heroeRepo.findById(id); }

    @Transactional
    public Heroe save(Heroe miHeroe) { return heroeRepo.save(miHeroe); }
    
    // Método PUT para vincular múltiples poderes a un héroe (N-N)
    @Transactional
    public Heroe actualizarPoderes(Long heroeId, List<Long> poderIds) {
        Heroe heroe = heroeRepo.findById(heroeId)
            .orElseThrow(() -> new RuntimeException("Heroe no encontrado"));
            
        List<Poder> poderes = poderRepo.findAllById(poderIds);
        heroe.setPoderes(poderes); // Actualiza la tabla intermedia automáticamente
        
        return heroeRepo.save(heroe);
    }
}

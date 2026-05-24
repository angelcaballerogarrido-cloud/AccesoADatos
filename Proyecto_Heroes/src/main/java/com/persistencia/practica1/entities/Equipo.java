package com.persistencia.practica1.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    // Relación Inversa 1-N
    @OneToMany(mappedBy = "miEquipo", cascade = CascadeType.ALL)
    @JsonIgnore // Evita bucle infinito en las peticiones GET JSON
    private List<Heroe> heroes;

    public Equipo() {}

    public Equipo(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Heroe> getHeroes() { return heroes; }
    public void setHeroes(List<Heroe> heroes) { this.heroes = heroes; }
}

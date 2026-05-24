package com.persistencia.practica1.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "poderes")
public class Poder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;

    // Lado esclavo de la relación N-N
    @ManyToMany(mappedBy = "poderes")
    @JsonIgnore
    private List<Heroe> heroes;

    public Poder() {}

    public Poder(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<Heroe> getHeroes() { return heroes; }
    public void setHeroes(List<Heroe> heroes) { this.heroes = heroes; }
}

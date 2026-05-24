package com.persistencia.practica1.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name="Heroes")
public class Heroe {
    
    @Id    
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre")
    private String nombre;
    
    @Column(name="base")
    private String base;

    // Relación 1-N (Muchos héroes tienen 1 equipo)
    @ManyToOne
    @JoinColumn(name = "idEquipo") // Quito nullable=false para permitir que un héroe no tenga equipo inicialmente
    private Equipo miEquipo;

    // Relación N-N (Muchos héroes tienen Muchos poderes) -> Tabla Intermedia
    @ManyToMany
    @JoinTable(
        name = "heroe_poder",
        joinColumns = @JoinColumn(name = "heroe_id"),
        inverseJoinColumns = @JoinColumn(name = "poder_id")
    )
    private List<Poder> poderes;

    public Heroe() {}

    public Heroe(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getBase() { return base; }
    public void setBase(String base) { this.base = base; }
    public Equipo getMiEquipo() { return miEquipo; }
    public void setMiEquipo(Equipo miEquipo) { this.miEquipo = miEquipo; }
    public List<Poder> getPoderes() { return poderes; }
    public void setPoderes(List<Poder> poderes) { this.poderes = poderes; }
}

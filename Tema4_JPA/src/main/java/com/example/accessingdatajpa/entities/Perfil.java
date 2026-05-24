package com.example.accessingdatajpa.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "perfiles")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telefono;
    private String fechaNacimiento;

    // MappedBy hace que la tabla Perfiles NO tenga la foreign key, la tiene Clientes
    @OneToOne(mappedBy = "perfil")
    private Clientes cliente;

    public Perfil() {}

    public Perfil(String telefono, String fechaNacimiento) {
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getId() { return id; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Clientes getCliente() { return cliente; }
    public void setCliente(Clientes cliente) { this.cliente = cliente; }
}

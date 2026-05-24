package com.example.accessingdatajpa.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name="clientes")
public class Clientes {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long idEmpleado; 

    @Column(name="nombre", unique = true, length = 40)
    private String nombre;

    @Column(name="apellido", length = 40)
    private String apellido;

    @Column(name="direccion", length = 100)
    private String direccion;

    // --- REQUISITOS TAREA 2: RELACIONES ---

    // Relación 1:1 -> El cliente tiene un perfil extra con fecha nacimiento etc.
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_perfil", referencedColumnName = "id")
    private Perfil perfil;

    // Relación 1:N -> Un cliente hace muchos pedidos. MappedBy indica quién domina la relación.
    @OneToMany(mappedBy = "cliente", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE }, fetch = FetchType.LAZY)
    private List<Pedido> pedidos;

    public Clientes() {}

    public Clientes(String nombre) {
        this.nombre = nombre;
    }

    public Clientes(String nombre, String apellido, String direccion) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
    }

    public Long getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Long idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public List<Pedido> getPedidos() { return pedidos; }
    public void setPedidos(List<Pedido> pedidos) { this.pedidos = pedidos; }
}

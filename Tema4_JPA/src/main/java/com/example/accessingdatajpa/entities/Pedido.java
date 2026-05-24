package com.example.accessingdatajpa.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fecha;
    private String estado; 

    // Relación N:1 -> Muchos pedidos pertenecen a un Cliente.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false) 
    private Clientes cliente;

    // Relación N:M -> LADO PROPIETARIO. Crea la tabla intermedia.
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
        name = "pedido_producto",
        joinColumns = @JoinColumn(name = "id_pedido"),
        inverseJoinColumns = @JoinColumn(name = "id_producto"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_pedido", "id_producto"})
    )
    private List<Producto> productos;

    public Pedido() {}

    public Pedido(String fecha, String estado, Clientes cliente) {
        this.fecha = fecha;
        this.estado = estado;
        this.cliente = cliente;
    }

    public Long getId() { return id; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Clientes getCliente() { return cliente; }
    public void setCliente(Clientes cliente) { this.cliente = cliente; }
    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}

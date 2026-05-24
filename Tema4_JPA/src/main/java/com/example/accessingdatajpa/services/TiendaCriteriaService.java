package com.example.accessingdatajpa.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Service;

import com.example.accessingdatajpa.entities.Clientes;
import com.example.accessingdatajpa.entities.Pedido;
import com.example.accessingdatajpa.entities.Producto;

import java.util.List;

@Service
public class TiendaCriteriaService {

    // El EntityManager es el "dios" de JPA. Lo inyectamos.
    @PersistenceContext
    private EntityManager em;

    // =========================================================================
    // 1) Varios OR, AND, LIKE mezclados para filtrar + ORDER BY
    // =========================================================================
    public List<Clientes> buscarClientesFiltroComplejo() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Clientes> cq = cb.createQuery(Clientes.class);
        Root<Clientes> root = cq.from(Clientes.class);
        
        // LIKE y OR (Nombre empieza por J o por A)
        Predicate empiezaConJ = cb.like(root.get("nombre"), "J%");
        Predicate empiezaConA = cb.like(root.get("nombre"), "A%");
        Predicate oNombres = cb.or(empiezaConJ, empiezaConA);
        
        // LIKE y AND (Y además que vivan en Madrid)
        Predicate viveEnMadrid = cb.like(root.get("direccion"), "%Madrid%");
        Predicate andFinal = cb.and(oNombres, viveEnMadrid);
        
        cq.select(root).where(andFinal);
        
        // ORDER BY apellido ascendente
        cq.orderBy(cb.asc(root.get("apellido"))); 
        
        return em.createQuery(cq).getResultList();
    }

    // =========================================================================
    // 2) greaterThan y currentDate() juntos para comparar fechas
    // =========================================================================
    public List<Pedido> buscarPedidosFuturos() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);
        Root<Pedido> root = cq.from(Pedido.class);
        
        // Comparamos el campo fecha del Pedido contra el Reloj del sistema actual.
        // Como 'fecha' lo declaramos como String, casteamos la currentDate a String para evitar Crash de Hibernate.
        Predicate mayorQueHoy = cb.greaterThan(root.get("fecha"), cb.currentDate().as(String.class));
        
        cq.select(root).where(mayorQueHoy);
        
        return em.createQuery(cq).getResultList();
    }

    // =========================================================================
    // 3) selectCase() [Opcional mayor complejidad]
    // =========================================================================
    public List<Object[]> clasificarProductos() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        // Devolvemos Object[] porque mezclamos un String (nombre) y otro String autogenerado (Categoría)
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Producto> root = cq.from(Producto.class);
        
        // Construimos un CASE dinámico: Si precio > 100 -> "CARO", else -> "BARATO"
        Expression<String> categoria = cb.selectCase()
            .when(cb.greaterThan(root.get("precio"), 100.0), "CARO")
            .otherwise("BARATO");
            
        // Hacemos una multi-selección de columnas
        cq.multiselect(root.get("nombre"), categoria);
        
        return em.createQuery(cq).getResultList();
    }

    // =========================================================================
    // 4) Sum() y groupBy() [Opcional mayor complejidad]
    // =========================================================================
    public List<Object[]> sumarPreciosAgrupados() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
        Root<Producto> root = cq.from(Producto.class);
        
        // Sumamos el valor del campo precio
        Expression<Double> sumaPrecios = cb.sum(root.get("precio"));
        
        cq.multiselect(root.get("nombre"), sumaPrecios);
        
        // Agrupamos por el nombre
        cq.groupBy(root.get("nombre"));
        
        return em.createQuery(cq).getResultList();
    }
}

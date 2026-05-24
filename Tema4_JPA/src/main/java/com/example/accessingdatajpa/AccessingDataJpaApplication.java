package com.example.accessingdatajpa;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import com.example.accessingdatajpa.entities.Clientes;
import com.example.accessingdatajpa.entities.Producto;
import com.example.accessingdatajpa.repositories.ClientesController;
import com.example.accessingdatajpa.repositories.ProductoRepository;
import com.example.accessingdatajpa.services.TiendaCriteriaService;

@SpringBootApplication
public class AccessingDataJpaApplication implements CommandLineRunner {

    @Autowired
    private ClientesController clientRepository;
    
    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private TiendaCriteriaService criteriaService;

    public static void main(String[] args) {
        SpringApplication.run(AccessingDataJpaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Cargamos unos datos base
        insertarDatosSemilla();
        
        // Disparamos las funciones de la Criteria API (Tarea 3)
        pruebasCriteriaAPI();
    }

    @Transactional
    public void insertarDatosSemilla() {
        System.out.println(">>> INYECTANDO DATOS DE PRUEBA...");
        // Creamos clientes estratégicos para que caigan en los filtros de la Criteria API
        Clientes cl1 = new Clientes("Ana", "Zarza", "Calle Falsa Madrid 123"); 
        Clientes cl2 = new Clientes("Juan", "Alonso", "Paseo de Madrid 45");
        Clientes cl3 = new Clientes("Pepe", "Gomez", "Calle Barcelona");
        
        clientRepository.saveAll(List.of(cl1, cl2, cl3));
        
        // Creamos productos caros y baratos para probar el selectCase()
        Producto p1 = new Producto("Ordenador Gaming", 1200.50);
        Producto p2 = new Producto("Raton USB", 15.0);
        productoRepository.saveAll(List.of(p1, p2));
        System.out.println(">>> DATOS INYECTADOS CON ÉXITO.");
    }

    @Transactional
    public void pruebasCriteriaAPI() {
        System.out.println("\n===========================================================");
        System.out.println("--- INICIO PRUEBAS TAREA 3 (CRITERIA API) ---");
        
        System.out.println("\n[PRUEBA 1] Clientes Complejos (Empiezan por J o A, en Madrid, Ordenados):");
        List<Clientes> complejos = criteriaService.buscarClientesFiltroComplejo();
        complejos.forEach(c -> System.out.println("-> " + c.getNombre() + " " + c.getApellido() + " (" + c.getDireccion() + ")"));
        
        System.out.println("\n[PRUEBA 3] Clasificador Productos CASE (CARO > 100 / BARATO):");
        List<Object[]> clasificados = criteriaService.clasificarProductos();
        for (Object[] obj : clasificados) {
            System.out.println("-> Producto: " + obj[0] + " | Categoria: " + obj[1]);
        }
        
        System.out.println("\n[PRUEBA 4] Sumar Precios Agrupados (GroupBy):");
        List<Object[]> sumados = criteriaService.sumarPreciosAgrupados();
        for (Object[] obj : sumados) {
            System.out.println("-> Grupo: " + obj[0] + " | Suma Total: " + obj[1] + " euros.");
        }
        
        System.out.println("\n--- FIN PRUEBAS CRITERIA API ---");
        System.out.println("===========================================================\n");
    }
}

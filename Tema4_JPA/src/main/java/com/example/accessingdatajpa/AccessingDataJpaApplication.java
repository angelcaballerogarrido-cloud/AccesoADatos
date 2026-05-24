package com.example.accessingdatajpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import com.example.accessingdatajpa.entities.Clientes;
import com.example.accessingdatajpa.repositories.ClientesController;

@SpringBootApplication
public class AccessingDataJpaApplication implements CommandLineRunner {

    // Inyectamos el Repositorio mágicamente
    @Autowired
    private ClientesController clientRepository;

    public static void main(String[] args) {
        SpringApplication.run(AccessingDataJpaApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        funcionPruebas1();
    }

    @Transactional
    public void funcionPruebas1() {
        // ESCRITURA EN BBDD
        Clientes cl1 = new Clientes("Pepe");
        Clientes cl2 = new Clientes("Juan");

        // uno a uno
        clientRepository.save(cl1);
        clientRepository.save(cl2);

        // juntos
        Clientes cl3 = new Clientes("Ana");
        Clientes cl4 = new Clientes("Luis");
        clientRepository.saveAll(List.of(cl3, cl4));

        // LECTURAS DE BASE DE DATOS
        Optional<Clientes> optionalClient = clientRepository.findById(1L);
        if (optionalClient.isPresent()) {
            System.out.println("==================================================");
            System.out.println("Cliente nº 1 encontrado en la BBDD MySQL: " + optionalClient.get().getNombre());
            System.out.println("==================================================");
        }
    }
}

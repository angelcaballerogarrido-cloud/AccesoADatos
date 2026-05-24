package com.persistencia.practica1;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.persistencia.practica1.entities.Equipo;
import com.persistencia.practica1.entities.Heroe;
import com.persistencia.practica1.entities.Poder;
import com.persistencia.practica1.services.EquipoService;
import com.persistencia.practica1.services.HeroeService;
import com.persistencia.practica1.services.PoderService;
import java.util.List;

@SpringBootApplication
public class ProyectoHeroesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProyectoHeroesApplication.class, args);
    }

    // Este Bean se ejecuta automáticamente al arrancar Spring Boot
    @Bean
    public CommandLineRunner initData(HeroeService heroeService, EquipoService equipoService, PoderService poderService, com.persistencia.practica1.repositories.RoleRepository roleRepository) {
        return args -> {
            // 0. Inyectamos los Roles de Seguridad primero
            if (roleRepository.count() == 0) {
                System.out.println(">>> INYECTANDO ROLES DE SEGURIDAD (RBAC)...");
                roleRepository.save(new com.persistencia.practica1.entities.Role(com.persistencia.practica1.entities.Role.RoleName.ROLE_USER));
                roleRepository.save(new com.persistencia.practica1.entities.Role(com.persistencia.practica1.entities.Role.RoleName.ROLE_ADMIN));
                roleRepository.save(new com.persistencia.practica1.entities.Role(com.persistencia.practica1.entities.Role.RoleName.ROLE_MODERATOR));
            }

            // Verificamos si la base de datos ya tiene datos para no duplicarlos cada vez que des a Play
            if (heroeService.findAll().isEmpty()) {
                System.out.println(">>> LA BASE DE DATOS ESTÁ VACÍA. INYECTANDO DATOS DE PRUEBA...");

                // 1. Crear Equipos
                Equipo vengadores = equipoService.save(new Equipo("Los Vengadores"));
                Equipo xmen = equipoService.save(new Equipo("X-Men"));

                // 2. Crear Héroes
                Heroe ironman = new Heroe("Iron Man");
                ironman.setBase("Torre Stark");
                ironman.setMiEquipo(vengadores);
                ironman = heroeService.save(ironman);

                Heroe spiderman = new Heroe("Spider-Man");
                spiderman.setBase("Queens");
                spiderman.setMiEquipo(vengadores);
                spiderman = heroeService.save(spiderman);

                Heroe wolverine = new Heroe("Wolverine");
                wolverine.setBase("Escuela Xavier");
                wolverine.setMiEquipo(xmen);
                wolverine = heroeService.save(wolverine);

                // 3. Crear Poderes
                Poder volar = poderService.save(new Poder("Volar", "Desplazarse por el aire usando propulsores o magia"));
                Poder regeneracion = poderService.save(new Poder("Regeneración", "Curación ultra rápida de heridas físicas"));

                // 4. Asignar Poderes a los Héroes usando el método PUT del servicio (N-N)
                heroeService.actualizarPoderes(ironman.getId(), List.of(volar.getId()));
                heroeService.actualizarPoderes(wolverine.getId(), List.of(regeneracion.getId()));

                System.out.println(">>> DATOS INYECTADOS CON ÉXITO.");
            } else {
                System.out.println(">>> LA BASE DE DATOS YA TIENE HÉROES. IGNORANDO INYECCIÓN.");
            }
        };
    }
}

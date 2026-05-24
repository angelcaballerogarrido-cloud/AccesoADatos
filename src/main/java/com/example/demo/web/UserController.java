package com.example.demo.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Arrays;
import java.util.List;
import com.example.demo.models.Producto;

@Controller
public class UserController {
    
    @GetMapping("/pruebas")
    public String pruebas( Model model )
    {        
        // LE PASAMOS UN MODELO ( DATOS ) AL TEMPLATE
        model.addAttribute("title", "Título");
        model.addAttribute("nombre", "Pepe");
        model.addAttribute("apellido", "Gomez");
 
        // ESTE TEXTO QUE SE RETORNA ES EL NOMBRE DEL FICHERO/PLANTILLA WEB QUE SE ABRIRÁ EN EL NAVEGADOR
        return "pruebas";
    }    

    // --- NUEVOS MÉTODOS AÑADIDOS PARA LA PRÁCTICA ---

    @GetMapping("/contacto")
    public String contacto( Model model ){                  
        model.addAttribute("mensaje","hola desde spring mvc");
        return "contacto"; 
    }

    @GetMapping("/producto")
    public String mostrarUnProducto(Model model){
        // Enviamos un único Objeto dentro del Model
        model.addAttribute("objeto", new Producto(1, "ordenador", 200));
        return "producto";
    }

    @GetMapping("/productos")
    public String mostrarProductos(Model model) {
        // Simulamos el findAll() del repositorio creando una lista estática
        List<Producto> listaProductos = Arrays.asList(
            new Producto(1, "ordenador", 200),
            new Producto(2, "teclado", 50),
            new Producto(3, "ratón", 25)
        );
        model.addAttribute("lista", listaProductos);
        return "productos/ver_productos";
    }
}

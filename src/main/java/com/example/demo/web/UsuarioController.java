package com.example.demo.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios") // URL base: /api/usuarios
public class UsuarioController {

    /**
     * Maneja peticiones HTTP DELETE para eliminar un usuario por su 'username'.
     *
     * Ejemplo de Petición: DELETE /api/usuarios/juan.perez
     *
     * @param username El ID (nombre de usuario) del usuario a eliminar.
     * @return Una respuesta HTTP que indica el éxito o fracaso de la operación.
     */
    @DeleteMapping("/{username}")
    public ResponseEntity<String> eliminarUsuarioPorUsername(@PathVariable String username) {
        
        // 1. Lógica de Negocio (simulada)
        if ("admin".equalsIgnoreCase(username)) {
            // No se permite eliminar al usuario 'admin'
            return ResponseEntity
                   .status(403) // Código 403 Forbidden (Prohibido)
                   .body("No se puede eliminar el usuario de administración: " + username);
        }

        // 2. Simulación de la eliminación exitosa
        System.out.println("Intentando eliminar usuario con username: " + username);
        
        // Aquí iría la llamada al Servicio para la eliminación en la base de datos
        // ... usuarioService.deleteByUsername(username);

        // 3. Retornar Respuesta Exitosa (Código 204)
        // El código 204 No Content es el estándar para peticiones DELETE exitosas 
        // cuando no se necesita retornar contenido en el cuerpo de la respuesta.
        return ResponseEntity
               .noContent() // Código 204 No Content
               .build(); 
    }
}
